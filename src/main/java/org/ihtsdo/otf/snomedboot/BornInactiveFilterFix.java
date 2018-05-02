package org.ihtsdo.otf.snomedboot;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import org.ihtsdo.otf.snomedboot.domain.ConceptConstants;
import org.ihtsdo.otf.snomedboot.factory.ImpotentComponentFactory;
import org.ihtsdo.otf.snomedboot.factory.LoadingProfile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static java.lang.Long.parseLong;

public class BornInactiveFilterFix {

	public static final String LOINC_MODULE = "715515008";

	public static void main(String[] args) throws ReleaseImportException, IOException {
		new BornInactiveFilterFix().run();
	}

	private void run() throws ReleaseImportException, IOException {

		final List<Long> activeRels = new LongArrayList();
		final List<Long> inactiveRels = new LongArrayList();

		new ReleaseImporter().loadSnapshotReleaseFiles(
				"/Users/kai/release/SnomedCT_InternationalRF2_PRODUCTION_20180131T120000Z",
				LoadingProfile.complete.withoutAnyRefsets(),
				new ImpotentComponentFactory() {
					@Override
					public void newRelationshipState(String id, String effectiveTime, String active, String moduleId, String sourceId, String destinationId, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId) {
						if (ConceptConstants.STATED_RELATIONSHIP.equals(characteristicTypeId)) {
							Long idL = parseLong(id);
							if ("1".equals(active)) {
								activeRels.add(idL);
							} else {
								inactiveRels.add(idL);
							}
						}
					}
				});

		System.out.println("activeRels " + activeRels.size());
		System.out.println("inactiveRels " + inactiveRels.size());

		try (BufferedWriter inactiveWriter = new BufferedWriter(new FileWriter("inactive-rels.txt"));
			 BufferedWriter deleteWriter = new BufferedWriter(new FileWriter("delete-rels.txt"));
			 BufferedWriter loincWriter = new BufferedWriter(new FileWriter("loinc-rels.txt"))) {

			new ReleaseImporter().loadDeltaReleaseFiles(
					"/Users/kai/Downloads/prod-fix-INFRA-2392",
					LoadingProfile.complete.withoutAnyRefsets(),
					new ImpotentComponentFactory() {
						@Override
						public void newRelationshipState(String id, String effectiveTime, String active, String moduleId, String sourceId, String destinationId, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId) {
							if (ConceptConstants.STATED_RELATIONSHIP.equals(characteristicTypeId)) {
								try {
									Long idL = parseLong(id);
									if ("0".equals(active)) {
										// New inactive state

										if (!activeRels.contains(idL)) {
											// Not active in previous release
											if (LOINC_MODULE.equals(moduleId)) {
												loincWriter.write(idL.toString());
												loincWriter.newLine();
											} else if (inactiveRels.contains(idL)) {
												// Inactive in previous - why in delta? Group change?
												inactiveWriter.write(idL.toString());
												inactiveWriter.newLine();
											} else {
												// Doesn't exist in previous release - delete
												deleteWriter.write(idL.toString());
												deleteWriter.newLine();
											}
										}
									}
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					});
		}

	}

}

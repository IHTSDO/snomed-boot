package org.ihtsdo.snomed.boot;

import org.ihtsdo.snomed.boot.domain.Concept;
import org.ihtsdo.snomed.boot.domain.ConceptConstants;
import org.ihtsdo.snomed.boot.domain.Description;
import org.ihtsdo.snomed.boot.domain.Relationship;
import org.ihtsdo.snomed.boot.domain.rf2.ComponentFields;
import org.ihtsdo.snomed.boot.domain.rf2.DescriptionFields;
import org.ihtsdo.snomed.boot.domain.rf2.RefsetFields;
import org.ihtsdo.snomed.boot.domain.rf2.RelationshipFields;
import org.ihtsdo.snomed.boot.service.LoadingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.NumberFormat;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ReleaseImporter {

	private final ComponentFactory componentFactory;
	private final ComponentStore componentStore;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public ReleaseImporter() {
		componentStore = new ComponentStore();
		componentFactory = new ComponentFactory(componentStore);
	}

	public Map<Long, Concept> loadReleaseZip(String releaseDirPath, LoadingMode loadingMode) throws IOException {
		File zipFile = findZipFilePath(releaseDirPath);
		logger.info("Loading release archive {}", zipFile.getAbsolutePath());
		try (final ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))) {
			ZipEntry nextEntry;
			while ((nextEntry = zipInputStream.getNextEntry()) != null) {
				final String entryName = nextEntry.getName();
				if (entryName.contains("sct2_Concept_Snapshot")) {
					loadConcepts(zipInputStream);
					break;
				}
			}
		}
		try (final ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))) {
			ZipEntry nextEntry;
			while ((nextEntry = zipInputStream.getNextEntry()) != null) {
				final String entryName = nextEntry.getName();
				if (entryName.contains("sct2_Relationship_Snapshot")) {
					loadRelationships(zipInputStream, loadingMode);
				} else if (entryName.contains("sct2_Description_Snapshot")) {
					loadDescriptions(zipInputStream, loadingMode);
				} else if (entryName.contains("der2_") && entryName.contains("Snapshot")) {
//					loadRefsets(zipInputStream);
				}
			}
		}
		logger.info("All in memory. Using approx {} MB of memory.", formatAsMB(Runtime.getRuntime().totalMemory()));

		return componentStore.getConcepts();
	}

	private File findZipFilePath(String releaseDirPath) throws FileNotFoundException {
		final File releaseDir = new File(releaseDirPath);
		if (!releaseDir.isDirectory()) {
			throw new FileNotFoundException("Could not find release directory.");
		}
		final File[] zips = releaseDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".zip");
			}
		});
		if (zips.length == 0) {
			throw new FileNotFoundException("Please place a SNOMED-CT RF2 release zip file in the release directory. Content will be loaded from there.");
		}
		return zips[0];
	}

	private void loadConcepts(ZipInputStream zipInputStream) throws IOException {
		readLines(zipInputStream, new ValuesHandler() {
			@Override
			public void handle(String[] values) {
				Long conceptId = new Long(values[ComponentFields.id]);
				componentFactory.createConcept(conceptId, values);
			}
		}, "concepts");
	}

	private void loadRelationships(ZipInputStream zipInputStream, final LoadingMode loadingMode) throws IOException {
		readLines(zipInputStream, new ValuesHandler() {
			@Override
			public void handle(String[] values) {
				if (values[RelationshipFields.active].equals("1")) {
					final Concept concept = getCreateConcept(values[RelationshipFields.sourceId]);
					final String type = values[RelationshipFields.typeId];
					final String value = values[RelationshipFields.destinationId];
					concept.addAttribute(type, value);
					if (type.equals(ConceptConstants.isA)) {
						concept.addParent(getCreateConcept(value));
					}
					if (loadingMode == LoadingMode.full) {
						concept.addRelationship(new Relationship(values));
					}
				}
			}
		}, "relationships");
	}

	private void loadDescriptions(ZipInputStream zipInputStream, final LoadingMode loadingMode) throws IOException {
		readLines(zipInputStream, new ValuesHandler() {
			@Override
			public void handle(String[] values) {
				if ("1".equals(values[DescriptionFields.active])) {
					final Concept concept = getCreateConcept(new Long(values[DescriptionFields.conceptId]));
					if (ConceptConstants.FSN.equals(values[DescriptionFields.typeId])) {
						concept.setFsn(values[DescriptionFields.term]);
					}
					if (loadingMode == LoadingMode.full) {
						concept.addDescription(new Description(values));
					}
				}
			}
		}, "descriptions");
	}

	private void loadRefsets(ZipInputStream zipInputStream) throws IOException {
		readLines(zipInputStream, new ValuesHandler() {
			@Override
			public void handle(String[] values) {
				if ("1".equals(values[DescriptionFields.active])) {
					final String referencedComponentId = values[RefsetFields.referencedComponentId];
					if (Concept.isConceptId(referencedComponentId)) {
						getCreateConcept(new Long(referencedComponentId)).addMemberOfRefsetId(new Long(values[RefsetFields.refsetId]));
					}
				}
			}
		}, "reference set members");
	}

	private Concept getCreateConcept(String id) {
		return getCreateConcept(new Long(id));
	}

	private Concept getCreateConcept(Long id) {
		Concept concept = componentStore.getConcepts().get(id);
		if (concept == null) {
			concept = new Concept(id);
			componentStore.addConcept(concept);
		}
		return concept;
	}

	private void readLines(ZipInputStream conceptsFileStream, ValuesHandler valuesHandler, String componentType) throws IOException {
		logger.info("Reading {} ", componentType);
		long linesRead = 0L;
		final BufferedReader reader = new BufferedReader(new InputStreamReader(conceptsFileStream));
		String line;
		reader.readLine(); // discard header line
		while ((line = reader.readLine()) != null) {
			valuesHandler.handle(line.split("\\t"));
			linesRead++;
			if (linesRead % 100000 == 0) {
				logger.info("{} {} read", linesRead, componentType);
			}
		}
		logger.info("{} {} read in total", linesRead, componentType);
	}

	private String formatAsMB(long bytes) {
		return NumberFormat.getInstance().format((bytes / 1024) / 1024);
	}

	private interface ValuesHandler {
		void handle(String[] values);
	}

}

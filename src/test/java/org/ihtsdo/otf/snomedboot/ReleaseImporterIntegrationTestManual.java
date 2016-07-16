package org.ihtsdo.otf.snomedboot;

import org.ihtsdo.otf.snomedboot.domain.Concept;
import org.ihtsdo.otf.snomedboot.factory.LoadingProfile;
import org.ihtsdo.otf.snomedboot.factory.implementation.standard.ComponentFactoryImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

public class ReleaseImporterIntegrationTestManual {

	@Test
	public void testLoadReleaseZip() throws Exception {
		final ComponentStore componentStore = new ComponentStore();
		ReleaseImporter releaseImporter = new ReleaseImporter();
		releaseImporter.loadSnapshotReleaseFiles("release/SnomedCT_RF2Release_INT_20150731", LoadingProfile.light, new ComponentFactoryImpl(componentStore));
		final Map<Long, ? extends Concept> conceptMap = componentStore.getConcepts();

		int activeConcepts = 0;
		for (Concept concept : conceptMap.values()) {
			if (concept.isActive()) {
				activeConcepts++;
			}
		}

		Assert.assertEquals(421657, conceptMap.size());
		Assert.assertEquals(317057, activeConcepts);

		final Concept concept = conceptMap.get(70299008L);
		Assert.assertEquals("Decompression of auditory nerve (procedure)", concept.getFsn());
		Assert.assertEquals("900000000000073002", concept.getDefinitionStatusId());
		Assert.assertEquals("20040731", concept.getEffectiveTime());
		Assert.assertEquals("900000000000207008", concept.getModuleId());
		final Set<Long> ancestorIds = concept.getAncestorIds();
		Assert.assertEquals(30, ancestorIds.size());

		Assert.assertEquals("No description components expected with light loading profile", 0, concept.getDescriptions().size());
		Assert.assertEquals("No relationship components expected with light loading profile", 0, concept.getRelationships().size());

	}
}

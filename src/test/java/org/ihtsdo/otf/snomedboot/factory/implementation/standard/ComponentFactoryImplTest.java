package org.ihtsdo.otf.snomedboot.factory.implementation.standard;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import org.ihtsdo.otf.snomedboot.ReleaseImportException;
import org.ihtsdo.otf.snomedboot.ReleaseImporter;
import org.ihtsdo.otf.snomedboot.factory.LoadingProfile;
import org.junit.Test;
import org.snomed.otf.snomedboot.testutil.ZipUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ComponentFactoryImplTest {

	@Test
	public void test() throws IOException, ReleaseImportException {
		File baseRF2SnapshotZip = ZipUtil.zipDirectoryRemovingCommentsAndBlankLines("src/test/resources/SnomedCT_MiniRF2_Base_snapshot");

		ReleaseImporter releaseImporter = new ReleaseImporter();
		ComponentStore componentStore = new ComponentStore();
		ComponentFactoryImpl componentFactory = new ComponentFactoryImpl(componentStore);

		releaseImporter.loadSnapshotReleaseFiles(new FileInputStream(baseRF2SnapshotZip), LoadingProfile.complete, componentFactory);

		Map<Long, ConceptImpl> concepts = componentStore.getConcepts();
		assertEquals(12, concepts.size());

		ConceptImpl findingSite = concepts.get(363698007L);
		assertEquals("Finding site (attribute)", findingSite.getDescriptions().get(0).getTerm());
		assertEquals("20170131", findingSite.getEffectiveTime());

		ConceptImpl disorderOfEndocrineSystem = concepts.get(362969004L);
		assertEquals("Check transitive closure", Sets.newHashSet(138875005L, 404684003L), disorderOfEndocrineSystem.getInferredAncestorIds());
	}

}

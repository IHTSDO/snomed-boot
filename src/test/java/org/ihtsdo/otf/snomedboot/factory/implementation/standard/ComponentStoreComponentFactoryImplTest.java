package org.ihtsdo.otf.snomedboot.factory.implementation.standard;

import com.google.common.collect.Sets;
import org.ihtsdo.otf.snomedboot.ReleaseImportException;
import org.ihtsdo.otf.snomedboot.ReleaseImporter;
import org.ihtsdo.otf.snomedboot.factory.LoadingProfile;
import org.ihtsdo.otf.snomedboot.factory.implementation.HighLevelComponentFactoryAdapterImpl;
import org.junit.Test;
import org.snomed.otf.snomedboot.testutil.ZipUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ComponentStoreComponentFactoryImplTest {

	@Test
	public void test() throws IOException, ReleaseImportException {
		File baseRF2SnapshotZip = ZipUtil.zipDirectoryRemovingCommentsAndBlankLines("src/test/resources/SnomedCT_MiniRF2_Base_snapshot");

		ReleaseImporter releaseImporter = new ReleaseImporter();
		ComponentStore componentStore = new ComponentStore();
		final LoadingProfile loadingProfile = LoadingProfile.complete;
		ComponentStoreComponentFactoryImpl componentFactory = new ComponentStoreComponentFactoryImpl(componentStore);
		releaseImporter.loadSnapshotReleaseFiles(new FileInputStream(baseRF2SnapshotZip), loadingProfile,
				new HighLevelComponentFactoryAdapterImpl(loadingProfile, componentFactory, componentFactory));

		Map<Long, ConceptImpl> concepts = componentStore.getConcepts();
		assertEquals(12, concepts.size());

		ConceptImpl findingSite = concepts.get(363698007L);
		assertEquals("Finding site (attribute)", findingSite.getDescriptions().get(0).getTerm());
		assertEquals("Finding site (attribute)", findingSite.getFsn());
		assertEquals("20170131", findingSite.getEffectiveTime());

		ConceptImpl disorderOfEndocrineSystem = concepts.get(362969004L);
		assertEquals("Check transitive closure", Sets.newHashSet(138875005L, 404684003L), disorderOfEndocrineSystem.getInferredAncestorIds());
	}

}

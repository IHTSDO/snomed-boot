package org.ihtsdo.otf.snomedboot;

import com.google.common.collect.Sets;
import org.ihtsdo.otf.snomedboot.domain.ConceptConstants;
import org.ihtsdo.otf.snomedboot.factory.LoadingProfile;
import org.ihtsdo.otf.snomedboot.factory.TestComponentFactory;
import org.junit.Test;
import org.snomed.otf.snomedboot.testutil.ZipUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReleaseImporterTest {

	@Test
	public void testLoadEffectiveComponentsSnapshot() throws ReleaseImportException, IOException {
		File baseRF2SnapshotZip = ZipUtil.zipDirectoryRemovingCommentsAndBlankLines("src/test/resources/SnomedCT_MiniRF2_Base_snapshot");
		File extensionRF2SnapshotZip = ZipUtil.zipDirectoryRemovingCommentsAndBlankLines("src/test/resources/SnomedCT_MiniRF2_Extension_snapshot");

		ReleaseImporter releaseImporter = new ReleaseImporter();


		// Load base release
		TestComponentFactory testComponentFactory = new TestComponentFactory();
		LoadingProfile complete = LoadingProfile.complete;
		complete.getIncludedReferenceSetFilenamePatterns().add(".*OWL.*");
		releaseImporter.loadSnapshotReleaseFiles(new FileInputStream(baseRF2SnapshotZip), complete, testComponentFactory);

		List<String> conceptLines = testComponentFactory.getConceptLines();
		assertEquals(11, conceptLines.size());

		assertTrue("Concept 362969004 is active in the base release", conceptLines.contains("362969004|20170131|1|900000000000207008"));
		assertTrue("Concept 73211009 is in the international module in the base release", conceptLines.contains("73211009|20170131|1|900000000000207008"));
		assertTrue("Refset Member c69ad177-9756-4ad1-a8b3-02407ca95b36 is active in the base release", testComponentFactory.getRefsetMemberLines().contains("c69ad177-9756-4ad1-a8b3-02407ca95b36|20170131|1|900000000000012004"));
		assertTrue("Text Definition 1228465017 is active", testComponentFactory.getDescriptionLines().contains("1228465017|20170731|1|900000000000207008"));


		// Load extension release
		testComponentFactory = new TestComponentFactory();
		releaseImporter.loadSnapshotReleaseFiles(new FileInputStream(extensionRF2SnapshotZip), LoadingProfile.complete, testComponentFactory);

		conceptLines = testComponentFactory.getConceptLines();
		assertEquals(4, conceptLines.size());

		assertTrue("Concept 362969004 is inactive in the extension release", conceptLines.contains("362969004|20170231|0|100101001"));
		assertTrue("Concept 73211009 is in the extension module in the base release", conceptLines.contains("73211009|20160231|1|100101001"));
		assertTrue("Refset Member c69ad177-9756-4ad1-a8b3-02407ca95b36 is inactive in the base release", testComponentFactory.getRefsetMemberLines().contains("c69ad177-9756-4ad1-a8b3-02407ca95b36|20170231|0|100101001"));


		// Load just the model module
		testComponentFactory = new TestComponentFactory();
		LoadingProfile loadingProfileWithModuleId = LoadingProfile.complete.withModuleIds(ConceptConstants.MODEL_MODULE);
		releaseImporter.loadSnapshotReleaseFiles(new FileInputStream(baseRF2SnapshotZip), loadingProfileWithModuleId, testComponentFactory);

		conceptLines = testComponentFactory.getConceptLines();
		assertEquals(6, conceptLines.size());


		// Load effective components
		testComponentFactory = new TestComponentFactory();
		releaseImporter.loadEffectiveSnapshotReleaseFileStreams(Sets.newHashSet(new FileInputStream(baseRF2SnapshotZip), new FileInputStream(extensionRF2SnapshotZip)), LoadingProfile.complete, testComponentFactory);

		conceptLines = testComponentFactory.getConceptLines();
		assertEquals(12, conceptLines.size());

		// Example of concept state overridden in extension
		assertTrue("Concept 362969004 is inactive in the effective release", conceptLines.contains("362969004|20170231|0|100101001"));
		assertTrue("The state of 362969004 from the base release has not been loaded", !conceptLines.contains("362969004|20170131|1|900000000000207008"));

		// Example of donated content without extension inactivation
		assertTrue("Concept 73211009 is in the international module in the effective release", conceptLines.contains("73211009|20170131|1|900000000000207008"));
		assertTrue("The state of 73211009 from the extension release has not been loaded", !conceptLines.contains("73211009|20160231|1|100101001"));

		assertTrue("Refset Member c69ad177-9756-4ad1-a8b3-02407ca95b36 is inactive in the effective release", testComponentFactory.getRefsetMemberLines().contains("c69ad177-9756-4ad1-a8b3-02407ca95b36|20170231|0|100101001"));

		// Similar assertions for other component types
		assertTrue("Description 2638112015 is inactive in the effective release", testComponentFactory.getDescriptionLines().contains("2638112015|20170231|0|100101001"));
		assertTrue("Description 754737012 is inactive in the effective release", testComponentFactory.getDescriptionLines().contains("754737012|20170231|0|100101001"));
		assertTrue("Relationship 200007001 is inactive in the effective release", testComponentFactory.getRelationshipLines().contains("200007001|20170231|0|100101001"));
		assertTrue("Stated Relationship 100007001 is inactive in the effective release", testComponentFactory.getRelationshipLines().contains("100007001|20170231|0|100101001"));


		// Load effective components, exclude inactive
		testComponentFactory = new TestComponentFactory();
		// Exclude loading inactive concepts
		LoadingProfile loadingProfile = LoadingProfile.complete.withoutInactiveConcepts();
		releaseImporter.loadEffectiveSnapshotReleaseFileStreams(Sets.newHashSet(new FileInputStream(baseRF2SnapshotZip), new FileInputStream(extensionRF2SnapshotZip)), loadingProfile, testComponentFactory);

		conceptLines = testComponentFactory.getConceptLines();
		assertEquals("Only 10 concepts should be loaded. The later inactive concept row is used to block loading the earlier dated active row.",
				10, conceptLines.size());
	}

	@Test
	public void testLoadFull() throws IOException, ReleaseImportException {
		File baseRF2FullZip = ZipUtil.zipDirectoryRemovingCommentsAndBlankLines("src/test/resources/SnomedCT_MiniRF2_Base_full");

		ReleaseImporter releaseImporter = new ReleaseImporter();
		TestComponentFactory testComponentFactory = new TestComponentFactory();

		releaseImporter.loadFullReleaseFiles(new FileInputStream(baseRF2FullZip), LoadingProfile.complete, testComponentFactory);

		assertEquals(5, testComponentFactory.getVersionsLoaded().size());
		assertEquals("[20020131, 20030731, 20170131, 20180131, 20180731]", testComponentFactory.getVersionsLoaded().toString());

		assertEquals(11, testComponentFactory.getConceptLines().size());
	}

	@Test
	public void testCollectReleaseFile() {
		ReleaseFiles releaseFiles = new ReleaseFiles();
		ReleaseImporter.ImportRun.collectReleaseFile(new File("sct2_Concept_Snapshot_INT_20180731.txt").toPath(), "Snapshot", releaseFiles);
		assertEquals(1, releaseFiles.getConceptPaths().size());

		releaseFiles = new ReleaseFiles();
		ReleaseImporter.ImportRun.collectReleaseFile(new File("xsct2_Concept_Snapshot_INT_20180731.txt").toPath(), "Snapshot", releaseFiles);
		assertEquals(1, releaseFiles.getConceptPaths().size());

		releaseFiles = new ReleaseFiles();
		ReleaseImporter.ImportRun.collectReleaseFile(new File("sct2_Concept_XExtensionSnapshot_INT_20180731.txt").toPath(), "Snapshot", releaseFiles);
		assertEquals(1, releaseFiles.getConceptPaths().size());

		releaseFiles = new ReleaseFiles();
		ReleaseImporter.ImportRun.collectReleaseFile(new File("xsct2_Concept_XExtensionSnapshot_INT_20180731.txt").toPath(), "Snapshot", releaseFiles);
		assertEquals(1, releaseFiles.getConceptPaths().size());

		releaseFiles = new ReleaseFiles();
		ReleaseImporter.ImportRun.collectReleaseFile(new File("sct2_Concept_XExtension_Snapshot_INT_20180731.txt").toPath(), "Snapshot", releaseFiles);
		assertEquals(0, releaseFiles.getConceptPaths().size());

		releaseFiles = new ReleaseFiles();
		ReleaseImporter.ImportRun.collectReleaseFile(new File("sct2_Concept_SnapshotXExtension_INT_20180731.txt").toPath(), "Snapshot", releaseFiles);
		assertEquals(0, releaseFiles.getConceptPaths().size());

		releaseFiles = new ReleaseFiles();
		ReleaseImporter.ImportRun.collectReleaseFile(new File("sct2_ConceptXExtension_Snapshot_INT_20180731.txt").toPath(), "Snapshot", releaseFiles);
		assertEquals(0, releaseFiles.getConceptPaths().size());


		releaseFiles = new ReleaseFiles();
		ReleaseImporter.ImportRun.collectReleaseFile(new File("sct2_Description_Snapshot_INT_20180731.txt").toPath(), "Snapshot", releaseFiles);
		assertEquals(1, releaseFiles.getDescriptionPaths().size());

		releaseFiles = new ReleaseFiles();
		ReleaseImporter.ImportRun.collectReleaseFile(new File("sct2_Description_Snapshot-en_INT_20180731.txt").toPath(), "Snapshot", releaseFiles);
		assertEquals(1, releaseFiles.getDescriptionPaths().size());

		releaseFiles = new ReleaseFiles();
		ReleaseImporter.ImportRun.collectReleaseFile(new File("xsct2_Description_Snapshot-EN_INT_20180731.txt").toPath(), "Snapshot", releaseFiles);
		assertEquals(1, releaseFiles.getDescriptionPaths().size());

		releaseFiles = new ReleaseFiles();
		ReleaseImporter.ImportRun.collectReleaseFile(new File("sct2_Description_XExtensionSnapshot_INT_20180731.txt").toPath(), "Snapshot", releaseFiles);
		assertEquals(1, releaseFiles.getDescriptionPaths().size());

		releaseFiles = new ReleaseFiles();
		ReleaseImporter.ImportRun.collectReleaseFile(new File("xsct2_Description_XExtensionSnapshot_INT_20180731.txt").toPath(), "Snapshot", releaseFiles);
		assertEquals(1, releaseFiles.getDescriptionPaths().size());


		releaseFiles = new ReleaseFiles();
		ReleaseImporter.ImportRun.collectReleaseFile(new File("der2_Refset_SimpleSnapshot_INT_20180731.txt").toPath(), "Snapshot", releaseFiles);
		assertEquals(1, releaseFiles.getRefsetPaths().size());

		releaseFiles = new ReleaseFiles();
		ReleaseImporter.ImportRun.collectReleaseFile(new File("der2_cissccRefset_MRCMAttributeDomainSnapshot_INT_20180731.txt").toPath(), "Snapshot", releaseFiles);
		assertEquals(1, releaseFiles.getRefsetPaths().size());

		releaseFiles = new ReleaseFiles();
		ReleaseImporter.ImportRun.collectReleaseFile(new File("xder2_cissccRefset_MRCMAttributeDomainSnapshot_INT_20180731.txt").toPath(), "Snapshot", releaseFiles);
		assertEquals(1, releaseFiles.getRefsetPaths().size());

		releaseFiles = new ReleaseFiles();
		ReleaseImporter.ImportRun.collectReleaseFile(new File("xder2_cissccRefset_MRCMAttributeDomainXExtensionSnapshot_INT_20180731.txt").toPath(), "Snapshot", releaseFiles);
		assertEquals(1, releaseFiles.getRefsetPaths().size());

		releaseFiles = new ReleaseFiles();
		ReleaseImporter.ImportRun.collectReleaseFile(new File("der2_cRefset_LanguageSpanishExtensionSnapshot-es_INT_20180430.txt").toPath(), "Snapshot", releaseFiles);
		assertEquals(1, releaseFiles.getRefsetPaths().size());

		releaseFiles = new ReleaseFiles();
		ReleaseImporter.ImportRun.collectReleaseFile(new File("xder2_cisscczRefset_MRCMAttributeDomainXExtensionSnapshot_INT_20180731.txt").toPath(), "Snapshot", releaseFiles);
		assertEquals("Should not match because of z in additional columns type list.", 0, releaseFiles.getRefsetPaths().size());

		releaseFiles = new ReleaseFiles();
		ReleaseImporter.ImportRun.collectReleaseFile(new File("xder2_cissccRefset_MRCMAttributeDomainSnapshotXExtension_INT_20180731.txt").toPath(), "Snapshot", releaseFiles);
		assertEquals("Should not match because no underscore after Snapshot.", 0, releaseFiles.getRefsetPaths().size());

	}

}

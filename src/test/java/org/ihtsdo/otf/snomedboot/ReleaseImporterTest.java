package org.ihtsdo.otf.snomedboot;

import com.google.common.collect.Lists;
import org.ihtsdo.otf.snomedboot.factory.LoadingProfile;
import org.ihtsdo.otf.snomedboot.factory.TestComponentFactory;
import org.junit.Test;
import org.snomed.otf.snomedboot.testutil.ZipUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.*;

public class ReleaseImporterTest {

	@Test
	public void testLoadEffectiveComponentsSnapshot() throws ReleaseImportException, IOException {
		File baseRF2SnapshotZip = ZipUtil.zipDirectoryRemovingCommentsAndBlankLines("src/test/resources/SnomedCT_MiniRF2_Base_snapshot");
		File extensionRF2SnapshotZip = ZipUtil.zipDirectoryRemovingCommentsAndBlankLines("src/test/resources/SnomedCT_MiniRF2_Extension_snapshot");

		ReleaseImporter releaseImporter = new ReleaseImporter();


		// Load base release
		TestComponentFactory testComponentFactory = new TestComponentFactory();
		releaseImporter.loadSnapshotReleaseFiles(new FileInputStream(baseRF2SnapshotZip), LoadingProfile.complete, testComponentFactory);

		List<String> conceptLines = testComponentFactory.getConceptLines();
		assertEquals(11, conceptLines.size());

		assertTrue("Concept 362969004 is active in the base release", conceptLines.contains("362969004|20170131|1|900000000000207008"));
		assertTrue("Concept 73211009 is in the international module in the base release", conceptLines.contains("73211009|20170131|1|900000000000207008"));
		assertTrue("Refset Member c69ad177-9756-4ad1-a8b3-02407ca95b36 is active in the base release", testComponentFactory.getRefsetMemberLines().contains("c69ad177-9756-4ad1-a8b3-02407ca95b36|20170131|1|900000000000012004"));


		// Load extension release
		testComponentFactory = new TestComponentFactory();
		releaseImporter.loadSnapshotReleaseFiles(new FileInputStream(extensionRF2SnapshotZip), LoadingProfile.complete, testComponentFactory);

		conceptLines = testComponentFactory.getConceptLines();
		assertEquals(4, conceptLines.size());

		assertTrue("Concept 362969004 is inactive in the extension release", conceptLines.contains("362969004|20170231|0|100101001"));
		assertTrue("Concept 73211009 is in the extension module in the base release", conceptLines.contains("73211009|20160231|1|100101001"));
		assertTrue("Refset Member c69ad177-9756-4ad1-a8b3-02407ca95b36 is inactive in the base release", testComponentFactory.getRefsetMemberLines().contains("c69ad177-9756-4ad1-a8b3-02407ca95b36|20170231|0|100101001"));


		// Load effective components
		testComponentFactory = new TestComponentFactory();
		releaseImporter.loadEffectiveSnapshotReleaseFileStreams(Lists.newArrayList(new FileInputStream(baseRF2SnapshotZip), new FileInputStream(extensionRF2SnapshotZip)), LoadingProfile.complete, testComponentFactory);

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
	}

}

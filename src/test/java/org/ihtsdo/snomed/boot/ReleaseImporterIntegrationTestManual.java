package org.ihtsdo.snomed.boot;

import org.ihtsdo.snomed.boot.service.LoadingProfile;
import org.junit.Test;

public class ReleaseImporterIntegrationTestManual {

	@Test
	public void testLoadReleaseZip() throws Exception {
		ReleaseImporter releaseImporter = new ReleaseImporter();
//		releaseImporter.loadReleaseFiles("release", LoadingProfile.full);
		releaseImporter.loadReleaseFiles("release", LoadingProfile.light);
	}
}

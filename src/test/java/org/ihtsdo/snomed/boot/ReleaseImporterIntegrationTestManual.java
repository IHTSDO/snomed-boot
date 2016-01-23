package org.ihtsdo.snomed.boot;

import org.ihtsdo.snomed.boot.service.LoadingMode;
import org.junit.Test;

public class ReleaseImporterIntegrationTestManual {

	@Test
	public void testLoadReleaseZip() throws Exception {
		ReleaseImporter releaseImporter = new ReleaseImporter();
//		releaseImporter.loadReleaseFiles("release", LoadingMode.full);
		releaseImporter.loadReleaseFiles("release", LoadingMode.light);
	}
}

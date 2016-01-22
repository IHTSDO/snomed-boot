package org.ihtsdo.snomed.boot;

import org.ihtsdo.snomed.boot.service.LoadingMode;
import org.junit.Test;

public class ReleaseImporterIntegrationTestManual {

	@Test
	public void testLoadReleaseZip() throws Exception {
		ReleaseImporter releaseImporter = new ReleaseImporter();
		releaseImporter.loadReleaseZip("release", LoadingMode.full);
//		releaseImporter.loadReleaseZip("release", LoadingMode.light);
	}
}
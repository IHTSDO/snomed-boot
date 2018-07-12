/**
 * 
 */
package org.ihtsdo.otf.snomedboot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.ihtsdo.otf.snomedboot.factory.ComponentFactory;
import org.ihtsdo.otf.snomedboot.factory.HistoryAwareComponentFactory;
import org.ihtsdo.otf.snomedboot.factory.LoadingProfile;
import org.ihtsdo.otf.snomedboot.factory.implementation.standard.ComponentStore;
import org.ihtsdo.otf.snomedboot.factory.implementation.standard.HistoryAwareComponentFactoryImpl;

/**
 * @author dlkn02
 *
 */
public class LoadReleaseWithExtension {

	/**
	 * @param args	list of 1..n release zip files
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Usage: LoadReleaseWithExtension <release directory>*");
		}
		
		ReleaseImporter releaseImporter = new ReleaseImporter();
		LoadingProfile completeProfile = LoadingProfile.complete;
		ComponentStore componentStore = new ComponentStore();
		HistoryAwareComponentFactory componentFactory = new HistoryAwareComponentFactoryImpl(componentStore);

		
		for(String releaseZip : args) {
			try {
				releaseImporter.loadFullReleaseFiles(new FileInputStream(releaseZip), completeProfile, componentFactory);
			} catch (ReleaseImportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}



	}

}

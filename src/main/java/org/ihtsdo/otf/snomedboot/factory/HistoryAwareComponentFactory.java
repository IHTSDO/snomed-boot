package org.ihtsdo.otf.snomedboot.factory;

public interface HistoryAwareComponentFactory extends ComponentFactory {

	void loadingReleaseDeltaStarting(String releaseVersion);

	void loadingReleaseDeltaFinished(String releaseVersion);

}

package org.ihtsdo.otf.snomedboot.factory.implementation.standard;

import org.ihtsdo.otf.snomedboot.factory.HistoryAwareComponentFactory;

public class HistoryAwareComponentFactoryImpl extends ComponentFactoryImpl implements HistoryAwareComponentFactory {

	public HistoryAwareComponentFactoryImpl(ComponentStore componentStore) {
		super(componentStore);
	}

	@Override
	public void loadingReleaseDeltaStarting(String releaseVersion) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadingReleaseDeltaFinished(String releaseVersion) {
		// TODO Auto-generated method stub

	}

}

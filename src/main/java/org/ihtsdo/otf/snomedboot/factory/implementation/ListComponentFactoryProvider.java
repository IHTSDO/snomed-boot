package org.ihtsdo.otf.snomedboot.factory.implementation;

import org.ihtsdo.otf.snomedboot.factory.ComponentFactory;
import org.ihtsdo.otf.snomedboot.factory.ComponentFactoryProvider;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ListComponentFactoryProvider implements ComponentFactoryProvider {

	private final Iterator<ComponentFactory> iterator;

	public ListComponentFactoryProvider(ComponentFactory factory) {
		iterator = Collections.singletonList(factory).iterator();
	}

	public ListComponentFactoryProvider(List<ComponentFactory> factories) {
		iterator = factories.iterator();
	}

	@Override
	public ComponentFactory getNextComponentFactory() {
		return iterator.hasNext() ? iterator.next() : null;
	}
}

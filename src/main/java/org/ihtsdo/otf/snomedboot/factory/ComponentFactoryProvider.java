package org.ihtsdo.otf.snomedboot.factory;

/**
 * This provider supplies the RF2 loading process with one or more ComponentFactory objects.
 * The RF2 content will be loaded into each ComponentFactory in turn. Any filters will be reused, for example the effective time filter.
 * There are many use cases for multiple passes over the content including advanced content filtering and consuming content in batches.
 */
public interface ComponentFactoryProvider {

	/**
	 * Get next ComponentFactory to load components into, or null if there are no more.
	 * @return ComponentFactory
	 */
	ComponentFactory getNextComponentFactory();

}

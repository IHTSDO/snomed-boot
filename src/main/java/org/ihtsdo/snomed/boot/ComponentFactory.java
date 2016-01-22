package org.ihtsdo.snomed.boot;

import org.ihtsdo.snomed.boot.domain.Concept;

public class ComponentFactory {

	private final ComponentStore componentStore;

	public ComponentFactory(ComponentStore componentStore) {
		this.componentStore = componentStore;
	}

	public Concept createConcept(Long conceptId, String[] values) {
		return componentStore.addConcept(new Concept(conceptId, values));
	}
}

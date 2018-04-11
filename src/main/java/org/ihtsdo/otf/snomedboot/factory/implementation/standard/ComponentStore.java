package org.ihtsdo.otf.snomedboot.factory.implementation.standard;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.ihtsdo.otf.snomedboot.factory.implementation.standard.ConceptImpl;

public class ComponentStore {

	private Long2ObjectMap<ConceptImpl> concepts;

	public ComponentStore() {
		concepts = new Long2ObjectOpenHashMap<>();
	}

	public Long2ObjectMap<ConceptImpl> getConcepts() {
		return concepts;
	}

	public ConceptImpl addConcept(ConceptImpl concept) {
		concepts.put(concept.getId(), concept);
		return concept;
	}
}

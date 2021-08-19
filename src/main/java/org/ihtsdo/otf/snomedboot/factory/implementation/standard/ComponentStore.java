package org.ihtsdo.otf.snomedboot.factory.implementation.standard;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.Map;

public class ComponentStore {

	private final Map<Long, ConceptImpl> concepts;

	public ComponentStore() {
		concepts = new Long2ObjectOpenHashMap<>();
	}

	public Map<Long, ConceptImpl> getConcepts() {
		return concepts;
	}

	public ConceptImpl addConcept(ConceptImpl concept) {
		concepts.put(concept.getId(), concept);
		return concept;
	}
}

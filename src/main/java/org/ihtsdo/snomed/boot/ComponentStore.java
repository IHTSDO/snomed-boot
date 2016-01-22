package org.ihtsdo.snomed.boot;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.ihtsdo.snomed.boot.domain.Concept;

public class ComponentStore {

	private Long2ObjectMap<Concept> concepts;

	public ComponentStore() {
		concepts = new Long2ObjectOpenHashMap<>();
	}

	public Long2ObjectMap<Concept> getConcepts() {
		return concepts;
	}

	public Concept addConcept(Concept concept) {
		concepts.put(concept.getId(), concept);
		return concept;
	}
}

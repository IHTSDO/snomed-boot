package org.ihtsdo.otf.snomedboot.factory;

import org.ihtsdo.otf.snomedboot.domain.Concept;

public interface ComponentFactory {

	Concept createConcept(String conceptId, String effectiveTime, String active, String moduleId, String definitionStatusId);

	void addConceptFSN(String conceptId, String term);

	void addConceptParent(String sourceId, String parentId);

	void addConceptAttribute(String sourceId, String typeId, String valueId);

	void addRelationship(String id, String effectiveTime, String active, String moduleId, String sourceId,
			String destinationId, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId);

	void addDescription(String id, String active, String term, String conceptId);

	void addConceptReferencedInRefsetId(String refsetId, String conceptId);
}

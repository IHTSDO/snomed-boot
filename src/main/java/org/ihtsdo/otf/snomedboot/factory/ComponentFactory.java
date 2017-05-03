package org.ihtsdo.otf.snomedboot.factory;

public interface ComponentFactory {

	void loadingComponentsStarting();

	void loadingComponentsCompleted();

	void newConceptState(String conceptId, String effectiveTime, String active, String moduleId, String definitionStatusId);

	void newDescriptionState(String id, String effectiveTime, String active, String moduleId, String conceptId, String languageCode, String typeId, String term, String caseSignificanceId);

	void newRelationshipState(String id, String effectiveTime, String active, String moduleId, String sourceId,
							  String destinationId, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId);

	void newReferenceSetMemberState(String[] fieldNames, String id, String effectiveTime, String active, String moduleId, String refsetId, String referencedComponentId, String... otherValues);

	void addConceptFSN(String conceptId, String term);

	void addInferredConceptParent(String sourceId, String parentId);

	void addStatedConceptParent(String sourceId, String parentId);

	void removeInferredConceptParent(String sourceId, String destinationId);

	void removeStatedConceptParent(String sourceId, String destinationId);

	void addInferredConceptAttribute(String sourceId, String typeId, String valueId);

	void addStatedConceptAttribute(String sourceId, String typeId, String valueId);

	void addConceptReferencedInRefsetId(String refsetId, String conceptId);
	
	void addInferredConceptChild(String sourceId, String destinationId);
	
	void addStatedConceptChild(String sourceId, String destinationId);
	
	void removeInferredConceptChild(String sourceId, String destinationId);

	void removeStatedConceptChild(String sourceId, String destinationId);
}

package org.ihtsdo.otf.snomedboot.factory;

public interface ComponentFactory {

	void preprocessingContent();

	void loadingComponentsStarting();

	void loadingComponentsCompleted();

	void newConceptState(String conceptId, String effectiveTime, String active, String moduleId, String definitionStatusId);

	void newDescriptionState(String id, String effectiveTime, String active, String moduleId, String conceptId, String languageCode, String typeId, String term, String caseSignificanceId);

	void newRelationshipState(String id, String effectiveTime, String active, String moduleId, String sourceId,
							  String destinationId, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId);

	void newConcreteRelationshipState(String id, String effectiveTime, String active, String moduleId, String sourceId,
							  String value, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId);

	void newReferenceSetMemberState(String[] fieldNames, String id, String effectiveTime, String active, String moduleId, String refsetId, String referencedComponentId, String... otherValues);

}

package org.ihtsdo.otf.snomedboot.factory;

public class ImpotentComponentFactory implements ComponentFactory {
	@Override
	public void loadingComponentsStarting() {

	}

	@Override
	public void loadingComponentsCompleted() {

	}

	@Override
	public void newConceptState(String conceptId, String effectiveTime, String active, String moduleId, String definitionStatusId) {

	}

	@Override
	public void newDescriptionState(String id, String effectiveTime, String active, String moduleId, String conceptId, String languageCode, String typeId, String term, String caseSignificanceId) {

	}

	@Override
	public void newRelationshipState(String id, String effectiveTime, String active, String moduleId, String sourceId, String destinationId, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId) {

	}

	@Override
	public void newReferenceSetMemberState(String[] fieldNames, String id, String effectiveTime, String active, String moduleId, String refsetId, String referencedComponentId, String... otherValues) {

	}

	@Override
	public void addConceptFSN(String conceptId, String term) {

	}

	@Override
	public void addInferredConceptParent(String sourceId, String parentId) {

	}

	@Override
	public void removeInferredConceptParent(String sourceId, String destinationId) {

	}

	@Override
	public void addConceptAttribute(String sourceId, String typeId, String valueId) {

	}

	@Override
	public void addConceptReferencedInRefsetId(String refsetId, String conceptId) {

	}
}

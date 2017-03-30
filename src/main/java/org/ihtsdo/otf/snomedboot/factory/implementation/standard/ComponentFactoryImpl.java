package org.ihtsdo.otf.snomedboot.factory.implementation.standard;

import org.ihtsdo.otf.snomedboot.ComponentStore;
import org.ihtsdo.otf.snomedboot.factory.ComponentFactory;
import org.ihtsdo.otf.snomedboot.factory.FactoryUtils;

public class ComponentFactoryImpl implements ComponentFactory {

	private final ComponentStore componentStore;

	public ComponentFactoryImpl(ComponentStore componentStore) {
		this.componentStore = componentStore;
	}

	@Override
	public void newConceptState(String conceptId, String effectiveTime, String active, String moduleId, String definitionStatusId) {
		componentStore.addConcept(new ConceptImpl(conceptId, effectiveTime, FactoryUtils.parseActive(active), moduleId, definitionStatusId));
	}

	@Override
	public void newDescriptionState(String id, String effectiveTime, String active, String moduleId, String conceptId, String languageCode, String typeId, String term, String caseSignificanceId) {
		getConceptForReference(conceptId).addDescription(new DescriptionImpl(id, FactoryUtils.parseActive(active), term, conceptId));
	}

	@Override
	public void newRelationshipState(String id, String effectiveTime, String active, String moduleId, String sourceId,
									 String destinationId, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId) {
		getConceptForReference(sourceId).addRelationship(new RelationshipImpl(id, effectiveTime, active, moduleId, sourceId,
				destinationId, relationshipGroup, typeId, characteristicTypeId, modifierId));
	}

	@Override
	public void newReferenceSetMemberState(String[] fieldNames, String id, String effectiveTime, String active, String moduleId, String refsetId, String referencedComponentId, String... otherValues) {

	}

	@Override
	public void addConceptFSN(String conceptId, String term) {
		getConceptForReference(conceptId).setFsn(term);
	}

	@Override
	public void addInferredConceptParent(String sourceId, String parentId) {
		getConceptForReference(sourceId).addInferredParent(getConceptForReference(parentId));
	}

	@Override
	public void addStatedConceptParent(String sourceId, String parentId) {
		getConceptForReference(sourceId).addStatedParent(getConceptForReference(parentId));
	}

	@Override
	public void removeInferredConceptParent(String sourceId, String parentId) {
		getConceptForReference(sourceId).removeInferredParent(getConceptForReference(parentId));
	}

	@Override
	public void removeStatedConceptParent(String sourceId, String parentId) {
		getConceptForReference(sourceId).removeStatedParent(getConceptForReference(parentId));
	}

	@Override
	public void addInferredConceptAttribute(String sourceId, String typeId, String valueId) {
		getConceptForReference(sourceId).addInferredAttribute(typeId, valueId);
	}

	@Override
	public void addStatedConceptAttribute(String sourceId, String typeId, String valueId) {
		getConceptForReference(sourceId).addStatedAttribute(typeId, valueId);
	}

	@Override
	public void addConceptReferencedInRefsetId(String refsetId, String conceptId) {
		getConceptForReference(conceptId).addMemberOfRefsetId(Long.parseLong(refsetId));
	}

	@Override
	public void loadingComponentsStarting() {

	}

	@Override
	public void loadingComponentsCompleted() {

	}

	private ConceptImpl getConceptForReference(String id) {
		ConceptImpl concept = componentStore.getConcepts().get(Long.parseLong(id));
		if (concept == null) {
			// Could throw exception here depending on implementation
			concept = new ConceptImpl(id);
			componentStore.addConcept(concept);
		}
		return concept;

	}
}

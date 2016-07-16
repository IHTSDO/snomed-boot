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
	public void createConcept(String conceptId, String effectiveTime, String active, String moduleId, String definitionStatusId) {
		componentStore.addConcept(new ConceptImpl(conceptId, effectiveTime, FactoryUtils.parseActive(active), moduleId, definitionStatusId));
	}

	@Override
	public void addConceptFSN(String conceptId, String term) {
		getConceptForReference(conceptId).setFsn(term);
	}

	@Override
	public void addConceptParent(String sourceId, String parentId) {
		getConceptForReference(sourceId).addParent(getConceptForReference(parentId));
	}

	@Override
	public void addConceptAttribute(String sourceId, String typeId, String valueId) {
		getConceptForReference(sourceId).addAttribute(typeId, valueId);
	}

	@Override
	public void addRelationship(String id, String effectiveTime, String active, String moduleId, String sourceId,
			String destinationId, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId) {
		getConceptForReference(sourceId).addRelationship(new RelationshipImpl(id, effectiveTime, active, moduleId, sourceId,
				destinationId, relationshipGroup, typeId, characteristicTypeId, modifierId));
	}

	@Override
	public void addDescription(String id, String effectiveTime, String active, String moduleId, String conceptId, String languageCode, String typeId, String term, String caseSignificanceId) {
		getConceptForReference(conceptId).addDescription(new DescriptionImpl(id, FactoryUtils.parseActive(active), term, conceptId));
	}

	@Override
	public void addConceptReferencedInRefsetId(String refsetId, String conceptId) {
		getConceptForReference(conceptId).addMemberOfRefsetId(Long.parseLong(refsetId));
	}

	@Override
	public void addReferenceSetMember(String id, String effectiveTime, String active, String moduleId, String refsetId, String referencedComponentId, String... otherValues) {

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

package org.ihtsdo.otf.snomedboot.factory.implementation.standard;

import org.ihtsdo.otf.snomedboot.factory.FactoryUtils;
import org.ihtsdo.otf.snomedboot.factory.HighLevelComponentFactory;
import org.ihtsdo.otf.snomedboot.factory.ImpotentComponentFactory;

public class ComponentStoreComponentFactoryImpl extends ImpotentComponentFactory implements HighLevelComponentFactory {

	private final ComponentStore componentStore;

	public ComponentStoreComponentFactoryImpl(ComponentStore componentStore) {
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
	public void newConcreteRelationshipState(String id, String effectiveTime, String active, String moduleId, String sourceId, String value, String relationshipGroup,
			String typeId, String characteristicTypeId, String modifierId) {

		getConceptForReference(sourceId).addConcreteRelationship(new ConcreteRelationshipImpl(id, effectiveTime, active, moduleId, sourceId,
				value, relationshipGroup, typeId, characteristicTypeId, modifierId));
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
	public void addInferredConceptChild(String sourceId, String destinationId) {
		getConceptForReference(destinationId).addInferredChild(getConceptForReference(sourceId));
	}

	@Override
	public void addStatedConceptChild(String sourceId, String destinationId) {
		getConceptForReference(destinationId).addStatedChild(getConceptForReference(sourceId));
	}

	@Override
	public void removeInferredConceptChild(String sourceId, String destinationId) {
		getConceptForReference(destinationId).removeInferredChild(getConceptForReference(sourceId));
	}

	@Override
	public void removeStatedConceptChild(String sourceId, String destinationId) {
		getConceptForReference(destinationId).removeStatedChild(getConceptForReference(sourceId));
	}

	@Override
	public void addInferredConceptAttribute(String sourceId, String typeId, String valueId) {
		getConceptForReference(sourceId).addInferredAttribute(typeId, valueId);
	}

	@Override
	public void addInferredConceptConcreteAttribute(String sourceId, String typeId, String value) {
		getConceptForReference(sourceId).addInferredConcreteAttribute(typeId, value);
	}

	@Override
	public void addStatedConceptAttribute(String sourceId, String typeId, String valueId) {
		getConceptForReference(sourceId).addStatedAttribute(typeId, valueId);
	}

	@Override
	public void addConceptReferencedInRefsetId(String refsetId, String conceptId) {
		getConceptForReference(conceptId).addMemberOfRefsetId(Long.parseLong(refsetId));
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

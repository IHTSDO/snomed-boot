package org.ihtsdo.otf.snomedboot.factory.implementation;

import org.ihtsdo.otf.snomedboot.domain.ConceptConstants;
import org.ihtsdo.otf.snomedboot.factory.*;

public class HighLevelComponentFactoryAdapterImpl implements ComponentFactory {

	private final LoadingProfile loadingProfile;
	private final HighLevelComponentFactory highLevelFactory;
	private final ComponentFactory delegateComponentFactory;

	public HighLevelComponentFactoryAdapterImpl(final LoadingProfile loadingProfile, HighLevelComponentFactory highLevelComponentFactory, ComponentFactory delegateComponentFactory) {
		this.loadingProfile = loadingProfile;
		this.highLevelFactory = highLevelComponentFactory;
		this.delegateComponentFactory = delegateComponentFactory;
	}

	@Override
	public void preprocessingContent() {
		delegateComponentFactory.preprocessingContent();
	}

	@Override
	public void loadingComponentsStarting() {
		delegateComponentFactory.loadingComponentsStarting();
	}

	@Override
	public void loadingComponentsCompleted() {
		delegateComponentFactory.loadingComponentsCompleted();
	}

	@Override
	public void newConceptState(String conceptId, String effectiveTime, String active, String moduleId, String definitionStatusId) {
		delegateComponentFactory.newConceptState(conceptId, effectiveTime, active, moduleId, definitionStatusId);
	}

	@Override
	public void newDescriptionState(String id, String effectiveTime, String active, String moduleId, String conceptId, String languageCode, String typeId, String term, String caseSignificanceId) {
		if (ConceptConstants.FSN.equals(typeId)) {
			highLevelFactory.addConceptFSN(conceptId, term);
		}
		delegateComponentFactory.newDescriptionState(id, effectiveTime, active, moduleId, conceptId, languageCode, typeId, term, caseSignificanceId);
	}

	@Override
	public void newRelationshipState(String id, String effectiveTime, String active, String moduleId, String sourceId, String destinationId, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId) {
		boolean inferred = ConceptConstants.INFERRED_RELATIONSHIP.equals(characteristicTypeId);
		if (!inferred && loadingProfile.isStatedAttributeMapOnConcept()) {
			highLevelFactory.addStatedConceptAttribute(sourceId, typeId, destinationId);
		} else if (inferred && loadingProfile.isInferredAttributeMapOnConcept()) {
			highLevelFactory.addInferredConceptAttribute(sourceId, typeId, destinationId);
		}
		if (typeId.equals(ConceptConstants.isA)) {
			if (active.equals("1")) {
				if (inferred) {
					highLevelFactory.addInferredConceptParent(sourceId, destinationId);
					highLevelFactory.addInferredConceptChild(sourceId, destinationId);
				} else {
					highLevelFactory.addStatedConceptParent(sourceId, destinationId);
					highLevelFactory.addStatedConceptChild(sourceId, destinationId);
				}
			} else {
				if (inferred) {
					highLevelFactory.removeInferredConceptParent(sourceId, destinationId);
					highLevelFactory.removeInferredConceptChild(sourceId, destinationId);
				} else {
					highLevelFactory.removeStatedConceptParent(sourceId, destinationId);
					highLevelFactory.removeStatedConceptChild(sourceId, destinationId);
				}
			}
		}
		delegateComponentFactory.newRelationshipState(id, effectiveTime, active, moduleId, sourceId, destinationId, relationshipGroup, typeId, characteristicTypeId, modifierId);
	}

	@Override
	public void newConcreteRelationshipState(String id, String effectiveTime, String active, String moduleId, String sourceId, String value, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId) {
		if (loadingProfile.isInferredAttributeMapOnConcept()) {
			highLevelFactory.addInferredConceptConcreteAttribute(sourceId, typeId, value);
		}
		delegateComponentFactory.newConcreteRelationshipState(id, effectiveTime, active, moduleId, sourceId, value, relationshipGroup, typeId, characteristicTypeId, modifierId);
	}

	@Override
	public void newReferenceSetMemberState(String[] fieldNames, String id, String effectiveTime, String active, String moduleId, String refsetId, String referencedComponentId, String... otherValues) {
		if (FactoryUtils.isConceptId(referencedComponentId)) {
			highLevelFactory.addConceptReferencedInRefsetId(refsetId, referencedComponentId);
		}
		delegateComponentFactory.newReferenceSetMemberState(fieldNames, id, effectiveTime, active, moduleId, refsetId, referencedComponentId, otherValues);
	}
}

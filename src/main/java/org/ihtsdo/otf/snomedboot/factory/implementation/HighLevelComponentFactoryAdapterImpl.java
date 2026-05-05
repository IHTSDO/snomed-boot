package org.ihtsdo.otf.snomedboot.factory.implementation;

import org.ihtsdo.otf.snomedboot.domain.ConceptConstants;
import org.ihtsdo.otf.snomedboot.factory.*;

public class HighLevelComponentFactoryAdapterImpl extends DelegatingComponentFactory {

	private final LoadingProfile loadingProfile;
	private final HighLevelComponentFactory highLevelFactory;

	public HighLevelComponentFactoryAdapterImpl(final LoadingProfile loadingProfile, HighLevelComponentFactory highLevelComponentFactory, ComponentFactory delegateComponentFactory) {
		super(delegateComponentFactory);
		this.loadingProfile = loadingProfile;
		this.highLevelFactory = highLevelComponentFactory;
	}

	@Override
	public LoadingProfile getLoadingProfile() {
		return null;
	}

	@Override
	public void newDescriptionState(String filename, long lineNumber, String id, String effectiveTime, String active, String moduleId, String conceptId, String languageCode, String typeId, String term, String caseSignificanceId) {
		if (isActive(active) && ConceptConstants.FSN.equals(typeId)) {
			highLevelFactory.addConceptFSN(conceptId, term);
		}
		super.newDescriptionState(filename, lineNumber, id, effectiveTime, active, moduleId, conceptId, languageCode, typeId, term, caseSignificanceId);
	}

	@Override
	public void newRelationshipState(String filename, long lineNumber, String id, String effectiveTime, String active, String moduleId, String sourceId, String destinationId, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId) {
		boolean inferred = ConceptConstants.INFERRED_RELATIONSHIP.equals(characteristicTypeId);
		if (isActive(active)) {
			if (!inferred && loadingProfile.isStatedAttributeMapOnConcept()) {
				highLevelFactory.addStatedConceptAttribute(sourceId, typeId, destinationId);
			} else if (inferred && loadingProfile.isInferredAttributeMapOnConcept()) {
				highLevelFactory.addInferredConceptAttribute(sourceId, typeId, destinationId);
			}
		}
		if (typeId.equals(ConceptConstants.isA)) {
			if (isActive(active)) {
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
		super.newRelationshipState(filename, lineNumber, id, effectiveTime, active, moduleId, sourceId, destinationId, relationshipGroup, typeId, characteristicTypeId, modifierId);
	}

	@Override
	public void newConcreteRelationshipState(String filename, long lineNumber, String id, String effectiveTime, String active, String moduleId, String sourceId, String value, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId) {
		if (isActive(active) && loadingProfile.isInferredAttributeMapOnConcept()) {
			highLevelFactory.addInferredConceptConcreteAttribute(sourceId, typeId, value);
		}
		super.newConcreteRelationshipState(filename, lineNumber, id, effectiveTime, active, moduleId, sourceId, value, relationshipGroup, typeId, characteristicTypeId, modifierId);
	}

	@Override
	public void newReferenceSetMemberState(String filename, long lineNumber, String[] fieldNames, String id, String effectiveTime, String active, String moduleId, String refsetId, String referencedComponentId, String... otherValues) {
		if (isActive(active) && FactoryUtils.isConceptId(referencedComponentId)) {
			highLevelFactory.addConceptReferencedInRefsetId(refsetId, referencedComponentId);
		}
		super.newReferenceSetMemberState(filename, lineNumber, fieldNames, id, effectiveTime, active, moduleId, refsetId, referencedComponentId, otherValues);
	}

	private boolean isActive(String active) {
		return "1".equals(active);
	}
}

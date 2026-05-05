package org.ihtsdo.otf.snomedboot.factory;

import org.ihtsdo.otf.snomedboot.ReleaseImportException;

public class DelegatingComponentFactory extends ImpotentComponentFactory {

	protected final ComponentFactory delegateComponentFactory;

	public DelegatingComponentFactory(ComponentFactory delegateComponentFactory) {
		this.delegateComponentFactory = delegateComponentFactory;
	}

	@Override
	public LoadingProfile getLoadingProfile() {
		return delegateComponentFactory.getLoadingProfile();
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
	public void loadingComponentsCompleted() throws ReleaseImportException {
		delegateComponentFactory.loadingComponentsCompleted();
	}

	@Override
	public void newConceptState(String filename, long lineNumber, String conceptId, String effectiveTime, String active, String moduleId, String definitionStatusId) {
		delegateComponentFactory.newConceptState(filename, lineNumber, conceptId, effectiveTime, active, moduleId, definitionStatusId);
	}

	@Override
	public void newDescriptionState(String filename, long lineNumber, String id, String effectiveTime, String active, String moduleId, String conceptId, String languageCode, String typeId, String term, String caseSignificanceId) {
		delegateComponentFactory.newDescriptionState(filename, lineNumber, id, effectiveTime, active, moduleId, conceptId, languageCode, typeId, term, caseSignificanceId);
	}

	@Override
	public void newRelationshipState(String filename, long lineNumber, String id, String effectiveTime, String active, String moduleId, String sourceId, String destinationId, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId) {
		delegateComponentFactory.newRelationshipState(filename, lineNumber, id, effectiveTime, active, moduleId, sourceId, destinationId, relationshipGroup, typeId, characteristicTypeId, modifierId);
	}

	@Override
	public void newConcreteRelationshipState(String filename, long lineNumber, String id, String effectiveTime, String active, String moduleId, String sourceId, String value, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId) {
		delegateComponentFactory.newConcreteRelationshipState(filename, lineNumber, id, effectiveTime, active, moduleId, sourceId, value, relationshipGroup, typeId, characteristicTypeId, modifierId);
	}

	@Override
	public void newReferenceSetMemberState(String filename, long lineNumber, String[] fieldNames, String id, String effectiveTime, String active, String moduleId, String refsetId, String referencedComponentId, String... otherValues) {
		delegateComponentFactory.newReferenceSetMemberState(filename, lineNumber, fieldNames, id, effectiveTime, active, moduleId, refsetId, referencedComponentId, otherValues);
	}

	@Override
	public void newIdentifierState(String filename, long lineNumber, String alternateIdentifier, String effectiveTime, String active, String moduleId, String identifierSchemeId, String referencedComponentId) {
		delegateComponentFactory.newIdentifierState(filename, lineNumber, alternateIdentifier, effectiveTime, active, moduleId, identifierSchemeId, referencedComponentId);
	}
}

package org.ihtsdo.otf.snomedboot.factory;

import org.ihtsdo.otf.snomedboot.ReleaseImportException;

public class ImpotentComponentFactory implements ComponentFactory {

	@Override
	public LoadingProfile getLoadingProfile() {
		return null;
	}

	@Override
	public void preprocessingContent() {

	}

	@Override
	public void loadingComponentsStarting() {

	}

	@Override
	public void loadingComponentsCompleted() throws ReleaseImportException {

	}

	@Override
	public void newConceptState(String filename, long lineNumber, String conceptId, String effectiveTime, String active, String moduleId, String definitionStatusId) {

	}

	@Override
	public void newDescriptionState(String filename, long lineNumber, String id, String effectiveTime, String active, String moduleId, String conceptId, String languageCode, String typeId, String term, String caseSignificanceId) {

	}

	@Override
	public void newRelationshipState(String filename, long lineNumber, String id, String effectiveTime, String active, String moduleId, String sourceId, String destinationId, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId) {

	}

	@Override
	public void newConcreteRelationshipState(String filename, long lineNumber, String id, String effectiveTime, String active, String moduleId, String sourceId, String value, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId) {

	}

	@Override
	public void newReferenceSetMemberState(String filename, long lineNumber, String[] fieldNames, String id, String effectiveTime, String active, String moduleId, String refsetId, String referencedComponentId, String... otherValues) {

	}

	@Override
	public void newIdentifierState(String filename, long lineNumber, String alternateIdentifier, String effectiveTime, String active, String moduleId, String identifierSchemeId, String referencedComponentId) {

	}

}

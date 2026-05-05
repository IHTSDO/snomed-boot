package org.ihtsdo.otf.snomedboot.factory;

import org.ihtsdo.otf.snomedboot.ReleaseImportException;

public interface ComponentFactory {

	LoadingProfile getLoadingProfile();

	void preprocessingContent();

	void loadingComponentsStarting();

	void loadingComponentsCompleted() throws ReleaseImportException;

	void newConceptState(String filename, long lineNumber, String conceptId, String effectiveTime, String active, String moduleId, String definitionStatusId);

	void newDescriptionState(String filename, long lineNumber, String id, String effectiveTime, String active, String moduleId, String conceptId, String languageCode, String typeId, String term, String caseSignificanceId);

	void newRelationshipState(String filename, long lineNumber, String id, String effectiveTime, String active, String moduleId, String sourceId,
							  String destinationId, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId);

	void newConcreteRelationshipState(String filename, long lineNumber, String id, String effectiveTime, String active, String moduleId, String sourceId,
							  String value, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId);

	void newReferenceSetMemberState(String filename, long lineNumber, String[] fieldNames, String id, String effectiveTime, String active, String moduleId, String refsetId, String referencedComponentId, String... otherValues);

	void newIdentifierState(String filename, long lineNumber, String alternateIdentifier, String effectiveTime, String active, String moduleId, String identifierSchemeId, String referencedComponentId);

}

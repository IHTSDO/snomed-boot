package org.ihtsdo.otf.snomedboot.factory.filter;

import com.google.common.base.Strings;
import org.ihtsdo.otf.snomedboot.ReleaseImportException;
import org.ihtsdo.otf.snomedboot.factory.ComponentFactory;
import org.ihtsdo.otf.snomedboot.factory.ImpotentComponentFactory;
import org.ihtsdo.otf.snomedboot.factory.LoadingProfile;

import java.util.Map;

public class ModuleEffectiveTimeFilter extends ImpotentComponentFactory {

	private final ComponentFactory delegateComponentFactory;
	private final Map<String, Integer> moduleEffectiveTimesAlreadyImported;

	public ModuleEffectiveTimeFilter(ComponentFactory delegateComponentFactory, Map<String, Integer> moduleEffectiveTimesAlreadyImported) {
		this.delegateComponentFactory = delegateComponentFactory;
		this.moduleEffectiveTimesAlreadyImported = moduleEffectiveTimesAlreadyImported;
	}

	private boolean isImportRow(String moduleId, String effectiveTime) {
		Integer existingEffectiveTime = moduleEffectiveTimesAlreadyImported.get(moduleId);
		if (Strings.isNullOrEmpty(effectiveTime) || existingEffectiveTime == null) {
			return true;
		}
		return Integer.parseInt(effectiveTime) > existingEffectiveTime;
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
		if (isImportRow(moduleId, effectiveTime)) {
			delegateComponentFactory.newConceptState(filename, lineNumber, conceptId, effectiveTime, active, moduleId, definitionStatusId);
		}
	}

	@Override
	public void newDescriptionState(String filename, long lineNumber, String id, String effectiveTime, String active, String moduleId, String conceptId, String languageCode, String typeId, String term, String caseSignificanceId) {
		if (isImportRow(moduleId, effectiveTime)) {
			delegateComponentFactory.newDescriptionState(filename, lineNumber, id, effectiveTime, active, moduleId, conceptId, languageCode, typeId, term, caseSignificanceId);
		}
	}

	@Override
	public void newRelationshipState(String filename, long lineNumber, String id, String effectiveTime, String active, String moduleId, String sourceId, String destinationId, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId) {
		if (isImportRow(moduleId, effectiveTime)) {
			delegateComponentFactory.newRelationshipState(filename, lineNumber, id, effectiveTime, active, moduleId, sourceId, destinationId, relationshipGroup, typeId, characteristicTypeId, modifierId);
		}
	}

	@Override
	public void newConcreteRelationshipState(String filename, long lineNumber, String id, String effectiveTime, String active, String moduleId, String sourceId, String value, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId) {
		if (isImportRow(moduleId, effectiveTime)) {
			delegateComponentFactory.newConcreteRelationshipState(filename, lineNumber, id, effectiveTime, active, moduleId, sourceId, value, relationshipGroup, typeId, characteristicTypeId, modifierId);
		}
	}

	@Override
	public void newIdentifierState(String filename, long lineNumber, String alternateIdentifier, String effectiveTime, String active, String moduleId, String identifierSchemeId, String referencedComponentId) {
		if (isImportRow(moduleId, effectiveTime)) {
			delegateComponentFactory.newIdentifierState(filename, lineNumber, alternateIdentifier, effectiveTime, active, moduleId, identifierSchemeId, referencedComponentId);
		}
	}

	@Override
	public void newReferenceSetMemberState(String filename, long lineNumber, String[] fieldNames, String id, String effectiveTime, String active, String moduleId, String refsetId, String referencedComponentId, String... otherValues) {
		if (isImportRow(moduleId, effectiveTime)) {
			delegateComponentFactory.newReferenceSetMemberState(filename, lineNumber, fieldNames, id, effectiveTime, active, moduleId, refsetId, referencedComponentId, otherValues);
		}
	}
}

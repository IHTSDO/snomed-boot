package org.ihtsdo.otf.snomedboot.factory.filter;

import org.ihtsdo.otf.snomedboot.ReleaseImportException;
import org.ihtsdo.otf.snomedboot.factory.ComponentFactory;
import org.ihtsdo.otf.snomedboot.factory.ImpotentComponentFactory;
import org.ihtsdo.otf.snomedboot.factory.LoadingProfile;

public class LatestEffectiveDateFilter extends ImpotentComponentFactory {

	private final ComponentFactory delegateComponentFactory;
	private final LatestEffectiveDateComponentFactory effectiveDateHolder;

	public LatestEffectiveDateFilter(ComponentFactory delegateComponentFactory, LatestEffectiveDateComponentFactory effectiveDateHolder) {
		this.delegateComponentFactory = delegateComponentFactory;
		this.effectiveDateHolder = effectiveDateHolder;
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
	public void newConceptState(String conceptId, String effectiveTime, String active, String moduleId, String definitionStatusId) {
		if (effectiveDateHolder.isCoreComponentVersionInEffect(conceptId, effectiveTime)) {
			delegateComponentFactory.newConceptState(conceptId, effectiveTime, active, moduleId, definitionStatusId);
		}
	}

	@Override
	public void newDescriptionState(String id, String effectiveTime, String active, String moduleId, String conceptId, String languageCode, String typeId, String term, String caseSignificanceId) {
		if (effectiveDateHolder.isCoreComponentVersionInEffect(id, effectiveTime)) {
			delegateComponentFactory.newDescriptionState(id, effectiveTime, active, moduleId, conceptId, languageCode, typeId, term, caseSignificanceId);
		}
	}

	@Override
	public void newRelationshipState(String id, String effectiveTime, String active, String moduleId, String sourceId, String destinationId, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId) {
		if (effectiveDateHolder.isCoreComponentVersionInEffect(id, effectiveTime)) {
			delegateComponentFactory.newRelationshipState(id, effectiveTime, active, moduleId, sourceId, destinationId, relationshipGroup, typeId, characteristicTypeId, modifierId);
		}
	}

	@Override
	public void newConcreteRelationshipState(String id, String effectiveTime, String active, String moduleId, String sourceId, String value, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId) {
		if (effectiveDateHolder.isCoreComponentVersionInEffect(id, effectiveTime)) {
			delegateComponentFactory.newConcreteRelationshipState(id, effectiveTime, active, moduleId, sourceId, value, relationshipGroup, typeId, characteristicTypeId, modifierId);
		}
	}

	@Override
	public void newReferenceSetMemberState(String[] fieldNames, String id, String effectiveTime, String active, String moduleId, String refsetId, String referencedComponentId, String... otherValues) {
		if (effectiveDateHolder.isReferenceSetMemberVersionInEffect(id, effectiveTime)) {
			delegateComponentFactory.newReferenceSetMemberState(fieldNames, id, effectiveTime, active, moduleId, refsetId, referencedComponentId, otherValues);
		}
	}

	@Override
	public void newIdentifierState(String alternateIdentifier, String effectiveTime, String active, String moduleId, String identifierSchemeId, String referencedComponentId) {
		if (effectiveDateHolder.isIdentifierVersionInEffect(alternateIdentifier + "-" + identifierSchemeId, effectiveTime)) {
			delegateComponentFactory.newIdentifierState(alternateIdentifier, effectiveTime, active, moduleId, identifierSchemeId, referencedComponentId);
		}
	}
}

package org.ihtsdo.otf.snomedboot.factory.filter;

import org.ihtsdo.otf.snomedboot.factory.ComponentFactory;
import org.ihtsdo.otf.snomedboot.factory.ImpotentComponentFactory;

import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Long.parseLong;

public class ModuleFilter extends ImpotentComponentFactory {

	private final ComponentFactory delegateComponentFactory;
	private final Set<Long> moduleIdLongs;

	public ModuleFilter(ComponentFactory delegateComponentFactory, Set<String> moduleIds) {
		this.delegateComponentFactory = delegateComponentFactory;
		moduleIdLongs = moduleIds.stream().map(Long::parseLong).collect(Collectors.toSet());
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
		if (moduleIdLongs.contains(parseLong(moduleId))) {
			delegateComponentFactory.newConceptState(conceptId, effectiveTime, active, moduleId, definitionStatusId);
		}
	}

	@Override
	public void newDescriptionState(String id, String effectiveTime, String active, String moduleId, String conceptId, String languageCode, String typeId, String term, String caseSignificanceId) {
		if (moduleIdLongs.contains(parseLong(moduleId))) {
			delegateComponentFactory.newDescriptionState(id, effectiveTime, active, moduleId, conceptId, languageCode, typeId, term, caseSignificanceId);
		}
	}

	@Override
	public void newRelationshipState(String id, String effectiveTime, String active, String moduleId, String sourceId, String destinationId, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId) {
		if (moduleIdLongs.contains(parseLong(moduleId))) {
			delegateComponentFactory.newRelationshipState(id, effectiveTime, active, moduleId, sourceId, destinationId, relationshipGroup, typeId, characteristicTypeId, modifierId);
		}
	}

	@Override
	public void newConcreteRelationshipState(String id, String effectiveTime, String active, String moduleId, String sourceId, String value, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId) {
		if (moduleIdLongs.contains(parseLong(moduleId))) {
			delegateComponentFactory.newConcreteRelationshipState(id, effectiveTime, active, moduleId, sourceId, value, relationshipGroup, typeId, characteristicTypeId, modifierId);
		}
	}

	@Override
	public void newIdentifierState(String alternateIdentifier, String effectiveTime, String active, String moduleId, String identifierSchemeId, String referencedComponentId) {
		if (moduleIdLongs.contains(parseLong(moduleId))) {
			delegateComponentFactory.newIdentifierState(alternateIdentifier, effectiveTime, active, moduleId, identifierSchemeId, referencedComponentId);
		}
	}

	@Override
	public void newReferenceSetMemberState(String[] fieldNames, String id, String effectiveTime, String active, String moduleId, String refsetId, String referencedComponentId, String... otherValues) {
		if (moduleIdLongs.contains(parseLong(moduleId))) {
			delegateComponentFactory.newReferenceSetMemberState(fieldNames, id, effectiveTime, active, moduleId, refsetId, referencedComponentId, otherValues);
		}
	}
}

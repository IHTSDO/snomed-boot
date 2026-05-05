package org.ihtsdo.otf.snomedboot.factory.filter;

import org.ihtsdo.otf.snomedboot.factory.ComponentFactory;
import org.ihtsdo.otf.snomedboot.factory.DelegatingComponentFactory;

public class LatestEffectiveDateFilter extends DelegatingComponentFactory {

	private final LatestEffectiveDateComponentFactory effectiveDateHolder;

	public LatestEffectiveDateFilter(ComponentFactory delegateComponentFactory, LatestEffectiveDateComponentFactory effectiveDateHolder) {
		super(delegateComponentFactory);
		this.effectiveDateHolder = effectiveDateHolder;
	}

	@Override
	public void newConceptState(String filename, long lineNumber, String conceptId, String effectiveTime, String active, String moduleId, String definitionStatusId) {
		if (effectiveDateHolder.isCoreComponentVersionInEffect(conceptId, effectiveTime)) {
			delegateComponentFactory.newConceptState(filename, lineNumber, conceptId, effectiveTime, active, moduleId, definitionStatusId);
		}
	}

	@Override
	public void newDescriptionState(String filename, long lineNumber, String id, String effectiveTime, String active, String moduleId, String conceptId, String languageCode, String typeId, String term, String caseSignificanceId) {
		if (effectiveDateHolder.isCoreComponentVersionInEffect(id, effectiveTime)) {
			delegateComponentFactory.newDescriptionState(filename, lineNumber, id, effectiveTime, active, moduleId, conceptId, languageCode, typeId, term, caseSignificanceId);
		}
	}

	@Override
	public void newRelationshipState(String filename, long lineNumber, String id, String effectiveTime, String active, String moduleId, String sourceId, String destinationId, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId) {
		if (effectiveDateHolder.isCoreComponentVersionInEffect(id, effectiveTime)) {
			delegateComponentFactory.newRelationshipState(filename, lineNumber, id, effectiveTime, active, moduleId, sourceId, destinationId, relationshipGroup, typeId, characteristicTypeId, modifierId);
		}
	}

	@Override
	public void newConcreteRelationshipState(String filename, long lineNumber, String id, String effectiveTime, String active, String moduleId, String sourceId, String value, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId) {
		if (effectiveDateHolder.isCoreComponentVersionInEffect(id, effectiveTime)) {
			delegateComponentFactory.newConcreteRelationshipState(filename, lineNumber, id, effectiveTime, active, moduleId, sourceId, value, relationshipGroup, typeId, characteristicTypeId, modifierId);
		}
	}

	@Override
	public void newReferenceSetMemberState(String filename, long lineNumber, String[] fieldNames, String id, String effectiveTime, String active, String moduleId, String refsetId, String referencedComponentId, String... otherValues) {
		if (effectiveDateHolder.isReferenceSetMemberVersionInEffect(id, effectiveTime)) {
			delegateComponentFactory.newReferenceSetMemberState(filename, lineNumber, fieldNames, id, effectiveTime, active, moduleId, refsetId, referencedComponentId, otherValues);
		}
	}

	@Override
	public void newIdentifierState(String filename, long lineNumber, String alternateIdentifier, String effectiveTime, String active, String moduleId, String identifierSchemeId, String referencedComponentId) {
		if (effectiveDateHolder.isIdentifierVersionInEffect(alternateIdentifier + "-" + identifierSchemeId, effectiveTime)) {
			delegateComponentFactory.newIdentifierState(filename, lineNumber, alternateIdentifier, effectiveTime, active, moduleId, identifierSchemeId, referencedComponentId);
		}
	}
}

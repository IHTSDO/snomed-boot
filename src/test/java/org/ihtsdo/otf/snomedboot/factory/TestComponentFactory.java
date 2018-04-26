package org.ihtsdo.otf.snomedboot.factory;

import java.util.ArrayList;
import java.util.List;

public class TestComponentFactory extends ImpotentHistoryAwareComponentFactory {

	private List<String> conceptLines = new ArrayList<>();
	private List<String> descriptionLines = new ArrayList<>();
	private List<String> relationshipLines = new ArrayList<>();
	private List<String> refsetMemberLines = new ArrayList<>();
	private List<String> versionsLoaded = new ArrayList<>();

	@Override
	public void newConceptState(String conceptId, String effectiveTime, String active, String moduleId, String definitionStatusId) {
		conceptLines.add(String.join("|", conceptId, effectiveTime, active, moduleId));
	}

	@Override
	public void newDescriptionState(String id, String effectiveTime, String active, String moduleId, String conceptId, String languageCode, String typeId, String term, String caseSignificanceId) {
		descriptionLines.add(String.join("|", id, effectiveTime, active, moduleId));
	}

	@Override
	public void newRelationshipState(String id, String effectiveTime, String active, String moduleId, String sourceId, String destinationId, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId) {
		relationshipLines.add(String.join("|", id, effectiveTime, active, moduleId));
	}

	@Override
	public void newReferenceSetMemberState(String[] fieldNames, String id, String effectiveTime, String active, String moduleId, String refsetId, String referencedComponentId, String... otherValues) {
		refsetMemberLines.add(String.join("|", id, effectiveTime, active, moduleId));
	}

	@Override
	public void loadingReleaseDeltaStarting(String releaseVersion) {
		versionsLoaded.add(releaseVersion);
	}

	public List<String> getConceptLines() {
		return conceptLines;
	}

	public List<String> getDescriptionLines() {
		return descriptionLines;
	}

	public List<String> getRelationshipLines() {
		return relationshipLines;
	}

	public List<String> getRefsetMemberLines() {
		return refsetMemberLines;
	}

	public List<String> getVersionsLoaded() {
		return versionsLoaded;
	}
}

package org.ihtsdo.otf.snomedboot.factory.implementation.standard;

import org.ihtsdo.otf.snomedboot.domain.Relationship;

public class RelationshipImpl implements Relationship {

	private String id;
	private String effectiveTime;
	private String active;
	private String moduleId;
	private String sourceId;
	private final String destinationId;
	private final String relationshipGroup;
	private final String typeId;
	private String characteristicTypeId;
	private String modifierId;

	public RelationshipImpl(String id, String effectiveTime, String active, String moduleId, String sourceId, String destinationId,
			String relationshipGroup, String typeId, String characteristicTypeId, String modifierId) {
		this.id = id;
		this.effectiveTime = effectiveTime;
		this.active = active;
		this.moduleId = moduleId;
		this.sourceId = sourceId;
		this.destinationId = destinationId;
		this.relationshipGroup = relationshipGroup;
		this.typeId = typeId;
		this.characteristicTypeId = characteristicTypeId;
		this.modifierId = modifierId;
	}

	public RelationshipImpl(String destinationId, String relationshipGroup, String typeId) {
		this.destinationId = destinationId;
		this.relationshipGroup = relationshipGroup;
		this.typeId = typeId;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getEffectiveTime() {
		return effectiveTime;
	}

	@Override
	public String getActive() {
		return active;
	}

	@Override
	public String getModuleId() {
		return moduleId;
	}

	@Override
	public String getSourceId() {
		return sourceId;
	}

	@Override
	public String getDestinationId() {
		return destinationId;
	}

	@Override
	public String getRelationshipGroup() {
		return relationshipGroup;
	}

	@Override
	public String getTypeId() {
		return typeId;
	}

	@Override
	public String getCharacteristicTypeId() {
		return characteristicTypeId;
	}

	@Override
	public String getModifierId() {
		return modifierId;
	}
}

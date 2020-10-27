package org.ihtsdo.otf.snomedboot.factory.implementation.standard;

import org.ihtsdo.otf.snomedboot.domain.ConcreteRelationship;

public class ConcreteRelationshipImpl implements ConcreteRelationship {

	private String id;
	private String effectiveTime;
	private String active;
	private String moduleId;
	private String sourceId;
	private final String value;
	private final String relationshipGroup;
	private final String typeId;
	private String characteristicTypeId;
	private String modifierId;

	public ConcreteRelationshipImpl(String id, String effectiveTime, String active, String moduleId, String sourceId, String value,
			String relationshipGroup, String typeId, String characteristicTypeId, String modifierId) {
		this.id = id;
		this.effectiveTime = effectiveTime;
		this.active = active;
		this.moduleId = moduleId;
		this.sourceId = sourceId;
		this.value = value;
		this.relationshipGroup = relationshipGroup;
		this.typeId = typeId;
		this.characteristicTypeId = characteristicTypeId;
		this.modifierId = modifierId;
	}

	public ConcreteRelationshipImpl(String value, String relationshipGroup, String typeId) {
		this.value = value;
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
	public String getValue() {
		return value;
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

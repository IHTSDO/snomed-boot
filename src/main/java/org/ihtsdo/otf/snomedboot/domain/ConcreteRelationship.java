package org.ihtsdo.otf.snomedboot.domain;

public interface ConcreteRelationship {
	String getId();

	String getEffectiveTime();

	String getActive();

	String getModuleId();

	String getSourceId();

	String getValue();

	String getRelationshipGroup();

	String getTypeId();

	String getCharacteristicTypeId();

	String getModifierId();
}

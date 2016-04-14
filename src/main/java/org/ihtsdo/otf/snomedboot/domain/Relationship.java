package org.ihtsdo.otf.snomedboot.domain;

public interface Relationship {
	String getId();

	String getEffectiveTime();

	String getActive();

	String getModuleId();

	String getSourceId();

	String getDestinationId();

	String getRelationshipGroup();

	String getTypeId();

	String getCharacteristicTypeId();

	String getModifierId();
}

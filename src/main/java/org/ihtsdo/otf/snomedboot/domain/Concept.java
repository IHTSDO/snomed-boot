package org.ihtsdo.otf.snomedboot.domain;

import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Set;

public interface Concept {
	Long getId();

	Set<Long> getMemberOfRefsetIds();

	Set<Long> getAncestorIds() throws IllegalStateException;

	boolean isActive();

	String getEffectiveTime();

	String getModuleId();

	String getDefinitionStatusId();

	String getFsn();

	MultiValueMap<String, String> getAttributes();

	List<Relationship> getRelationships();

	List<Description> getDescriptions();
}

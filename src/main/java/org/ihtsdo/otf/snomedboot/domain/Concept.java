package org.ihtsdo.otf.snomedboot.domain;

import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Set;

public interface Concept {
	Long getId();

	Set<Long> getMemberOfRefsetIds();

	Set<Long> getInferredAncestorIds() throws IllegalStateException;

	Set<Long> getStatedAncestorIds() throws IllegalStateException;

	boolean isActive();

	String getEffectiveTime();

	String getModuleId();

	String getDefinitionStatusId();

	String getFsn();

	MultiValueMap<String, String> getInferredAttributes();

	MultiValueMap<String, String> getStatedAttributes();

	List<Relationship> getRelationships();

	List<Description> getDescriptions();
}

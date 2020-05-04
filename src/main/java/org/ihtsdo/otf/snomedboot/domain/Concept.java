package org.ihtsdo.otf.snomedboot.domain;

import java.util.List;
import java.util.Map;
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

	Map<String, Set<String>> getInferredAttributes();

	Map<String, Set<String>> getStatedAttributes();

	List<Relationship> getRelationships();

	List<Description> getDescriptions();

	Set<Long> getInferredDescendantIds() throws IllegalStateException;

	Set<Long> getStatedDescendantIds() throws IllegalStateException;
}

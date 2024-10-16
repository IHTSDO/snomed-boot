package org.ihtsdo.otf.snomedboot.factory;

import com.google.common.collect.ImmutableSet;
import org.ihtsdo.otf.snomedboot.domain.ConceptConstants;

import java.util.*;

public class LoadingProfile {

	public static final LoadingProfile light = new LoadingProfile();
	public static final LoadingProfile complete = new LoadingProfile();

	static {
		light.inferredAttributeMapOnConcept = true;
		light.concepts = true;
		light.descriptions = true;
		light.textDefinitions = true;
		light.relationships = true;
		light.identifiers = true;
		light.refsetIds.add(ConceptConstants.GB_EN_LANGUAGE_REFERENCE_SET);
		light.refsetIds.add(ConceptConstants.US_EN_LANGUAGE_REFERENCE_SET);

		complete.inferredAttributeMapOnConcept = true;
		complete.concepts = true;
		complete.descriptions = true;
		complete.textDefinitions = true;
		complete.relationships = true;
		complete.identifiers = true;
		complete.statedRelationships = true;
		complete.inactiveConcepts = true;
		complete.inactiveDescriptions = true;
		complete.inactiveRelationships = true;
		complete.inactiveIdentifiers = true;
		complete.inactiveRefsetMembers = true;
		complete.allRefsets = true;
	}

	private boolean effectiveComponentFilter;
	private boolean inferredAttributeMapOnConcept;
	private boolean statedAttributeMapOnConcept;
	private boolean concepts;
	private boolean descriptions;
	private boolean textDefinitions;
	private boolean relationships;
	private boolean identifiers;
	private boolean statedRelationships;
	private boolean inactiveConcepts;
	private boolean inactiveDescriptions;
	private boolean inactiveRelationships;
	private boolean inactiveIdentifiers;
	private boolean inactiveRefsetMembers;
	private boolean allRefsets;
	private boolean justRefsets;
	private Set<String> refsetIds = new HashSet<>();
	private Set<String> includedReferenceSetFilenamePatterns = new HashSet<>();
	private Set<String> moduleIds = new HashSet<>();
	private Map<String, Integer> moduleEffectiveTimeFilters = new HashMap<>();

	public LoadingProfile withEffectiveComponentFilter() {
		return this.cloneObject().setEffectiveComponentFilter(true);
	}

	public LoadingProfile withoutEffectiveComponentFilter() {
		return this.cloneObject().setEffectiveComponentFilter(false);
	}

	public LoadingProfile withInferredAttributeMapOnConcept() {
		return this.cloneObject().setInferredAttributeMapOnConcept(true);
	}

	public LoadingProfile withoutInferredAttributeMapOnConcept() {
		return this.cloneObject().setInferredAttributeMapOnConcept(false);
	}

	public LoadingProfile withStatedAttributeMapOnConcept() {
		return this.cloneObject().setStatedAttributeMapOnConcept(true);
	}

	public LoadingProfile withoutStatedAttributeMapOnConcept() {
		return this.cloneObject().setStatedAttributeMapOnConcept(false);
	}

	public LoadingProfile withStatedRelationships() {
		return this.cloneObject().setStatedRelationships(true);
	}

	public LoadingProfile withoutStatedRelationships() {
		return this.cloneObject().setStatedRelationships(false);
	}

	public LoadingProfile withConcepts() {
		return this.cloneObject().setConcepts(true);
	}

	public LoadingProfile withoutConcepts() {
		return this.cloneObject().setConcepts(false);
	}

	public LoadingProfile withDescriptions() {
		return this.cloneObject().setDescriptions(true);
	}

	public LoadingProfile withoutDescriptions() {
		return this.cloneObject().setDescriptions(false);
	}

	public LoadingProfile withTextDefinitions() {
		return this.cloneObject().setTextDefinitions(true);
	}

	public LoadingProfile withoutTextDefinitions() {
		return this.cloneObject().setTextDefinitions(false);
	}

	public LoadingProfile withRelationships() {
		return this.cloneObject().setRelationships(true);
	}

	public LoadingProfile withoutRelationships() {
		return this.cloneObject().setRelationships(false);
	}

	public LoadingProfile withIdentifiers() {
		return this.cloneObject().setIdentifiers(true);
	}

	public LoadingProfile withoutIdentifiers() {
		return this.cloneObject().setIdentifiers(false);
	}

	public LoadingProfile withInactiveComponents() {
		return this.cloneObject().setInactiveConcepts(true).setInactiveDescriptions(true).setInactiveRelationships(true);
	}

	public LoadingProfile withInactiveConcepts() {
		return this.cloneObject().setInactiveConcepts(true);
	}

	public LoadingProfile withoutInactiveConcepts() {
		return this.cloneObject().setInactiveConcepts(false);
	}

	public LoadingProfile withInactiveDescriptions() {
		return this.cloneObject().setInactiveDescriptions(true);
	}

	public LoadingProfile withoutInactiveDescriptions() {
		return this.cloneObject().setInactiveDescriptions(false);
	}

	public LoadingProfile withInactiveRelationships() {
		return this.cloneObject().setInactiveRelationships(true);
	}

	public LoadingProfile withoutInactiveRelationships() {
		return this.cloneObject().setInactiveRelationships(false);
	}

	public LoadingProfile withInactiveRefsetMembers() {
		return this.cloneObject().setInactiveRefsetMembers(true);
	}
	public LoadingProfile withoutInactiveRefsetMembers() {
		return this.cloneObject().setInactiveRefsetMembers(false);
	}

	public LoadingProfile withAllRefsets() {
		return this.cloneObject().setAllRefsets(true);
	}

	public LoadingProfile withoutAllRefsets() {
		return this.cloneObject().setAllRefsets(false);
	}

	public LoadingProfile withJustRefsets() {
		return this.cloneObject().setJustRefsets(true);
	}

	public LoadingProfile withoutJustRefsets() {
		return this.cloneObject().setJustRefsets(false);
	}

	public LoadingProfile withRefset(String refsetId) {
		return withRefsets(refsetId);
	}

	public LoadingProfile withRefsets(String... refsetId) {
		final LoadingProfile clone = this.cloneObject();
		Collections.addAll(clone.getRefsetIdsNoClone(), refsetId);
		return clone;
	}

	public LoadingProfile withoutRefset(String refsetId) {
		return withoutRefsets(refsetId);
	}

	public LoadingProfile withoutRefsets(String... refsetId) {
		final LoadingProfile clone = this.cloneObject();
		for (String id : refsetId) {
			clone.getRefsetIdsNoClone().remove(id);
		}
		return clone;
	}

	public LoadingProfile withoutAnyRefsets() {
		final LoadingProfile clone = this.cloneObject();
		clone.refsetIds.clear();
		return clone;
	}

	public LoadingProfile withModuleIds(String... moduleIds) {
		final LoadingProfile clone = this.cloneObject();
		Collections.addAll(clone.getModuleIdsNoClone(), moduleIds);
		return clone;
	}

	public LoadingProfile withoutModuleIds(String... moduleIds) {
		final LoadingProfile clone = this.cloneObject();
		for (String id : moduleIds) {
			clone.getModuleIdsNoClone().remove(id);
		}
		return clone;
	}

	public LoadingProfile withoutAnyModuleIds() {
		final LoadingProfile clone = this.cloneObject();
		clone.moduleIds.clear();
		return clone;
	}

	public LoadingProfile withModuleEffectiveTimeFilter(Map<String, Integer> moduleEffectiveTimesAlreadyImported) {
		final LoadingProfile clone = this.cloneObject();
		clone.setModuleEffectiveTimeFilters(moduleEffectiveTimesAlreadyImported);
		return clone;
	}

	public boolean isEffectiveComponentFilter() {
		return effectiveComponentFilter;
	}

	public boolean isInferredAttributeMapOnConcept() {
		return inferredAttributeMapOnConcept;
	}

	public boolean isStatedAttributeMapOnConcept() {
		return statedAttributeMapOnConcept;
	}

	public boolean isStatedRelationships() {
		return statedRelationships;
	}

	public boolean isConcepts() {
		return concepts;
	}

	public boolean isDescriptions() {
		return descriptions;
	}

	public boolean isTextDefinitions() {
		return textDefinitions;
	}

	public boolean isRelationships() {
		return relationships;
	}

	public boolean isIdentifiers() {
		return identifiers;
	}

	public boolean isInactiveConcepts() {
		return inactiveConcepts;
	}

	public boolean isInactiveDescriptions() {
		return inactiveDescriptions;
	}

	public boolean isInactiveRelationships() {
		return inactiveRelationships;
	}

	public boolean isInactiveIdentifiers() {
		return inactiveIdentifiers;
	}

	public boolean isInactiveRefsetMembers() {
		return inactiveRefsetMembers;
	}

	public boolean isAllRefsets() {
		return allRefsets;
	}

	public boolean isJustRefsets() {
		return justRefsets;
	}

	public boolean isRefset(String refsetId) {
		return refsetIds.contains(refsetId);
	}

	public ImmutableSet<String> getRefsetIds() {
		return ImmutableSet.copyOf(refsetIds);
	}

	public Set<String> getModuleIds() {
		return ImmutableSet.copyOf(moduleIds);
	}

	public Map<String, Integer> getModuleEffectiveTimeFilters() {
		return moduleEffectiveTimeFilters;
	}

	public LoadingProfile setEffectiveComponentFilter(boolean effectiveComponentFilter) {
		this.effectiveComponentFilter = effectiveComponentFilter;
		return this;
	}

	private LoadingProfile setInferredAttributeMapOnConcept(boolean inferredAttributeMapOnConcept) {
		this.inferredAttributeMapOnConcept = inferredAttributeMapOnConcept;
		return this;
	}

	private LoadingProfile setStatedAttributeMapOnConcept(boolean statedAttributeMapOnConcept) {
		this.statedAttributeMapOnConcept = statedAttributeMapOnConcept;
		return this;
	}

	private LoadingProfile setStatedRelationships(boolean statedRelationships) {
		this.statedRelationships = statedRelationships;
		return this;
	}

	public LoadingProfile setConcepts(boolean concepts) {
		this.concepts = concepts;
		return this;
	}

	private LoadingProfile setDescriptions(boolean descriptions) {
		this.descriptions = descriptions;
		return this;
	}

	public LoadingProfile setTextDefinitions(boolean textDefinitions) {
		this.textDefinitions = textDefinitions;
		return this;
	}

	public LoadingProfile setRelationships(boolean relationships) {
		this.relationships = relationships;
		return this;
	}

	public LoadingProfile setIdentifiers(boolean identifiers) {
		this.identifiers = identifiers;
		return this;
	}

	private LoadingProfile setInactiveConcepts(boolean inactiveConcepts) {
		this.inactiveConcepts = inactiveConcepts;
		return this;
	}

	private LoadingProfile setInactiveDescriptions(boolean inactiveDescriptions) {
		this.inactiveDescriptions = inactiveDescriptions;
		return this;
	}

	private LoadingProfile setInactiveRelationships(boolean inactiveRelationships) {
		this.inactiveRelationships = inactiveRelationships;
		return this;
	}

	public LoadingProfile setInactiveIdentifiers(boolean inactiveIdentifiers) {
		this.inactiveIdentifiers = inactiveIdentifiers;
		return this;
	}

	private LoadingProfile setInactiveRefsetMembers(boolean inactiveRefsetMembers) {
		this.inactiveRefsetMembers = inactiveRefsetMembers;
		return this;
	}

	private LoadingProfile setAllRefsets(boolean allRefsets) {
		this.allRefsets = allRefsets;
		return this;
	}

	private LoadingProfile setJustRefsets(boolean justRefsets) {
		this.justRefsets = justRefsets;
		return this;
	}

	public Set<String> getIncludedReferenceSetFilenamePatterns() {
		return includedReferenceSetFilenamePatterns;
	}

	private LoadingProfile setRefsetIds(Set<String> refsetIds) {
		this.refsetIds = refsetIds;
		return this;
	}

	public LoadingProfile withIncludedReferenceSetFilenamePattern(String includedReferenceSetFilenamePattern) {
		this.includedReferenceSetFilenamePatterns.add(includedReferenceSetFilenamePattern);
		return this;
	}

	public LoadingProfile setIncludedReferenceSetFilenamePatterns(Set<String> includedReferenceSetFilenamePatterns) {
		this.includedReferenceSetFilenamePatterns = includedReferenceSetFilenamePatterns;
		return this;
	}

	private LoadingProfile setModuleIds(Set<String> moduleIds) {
		this.moduleIds = moduleIds;
		return this;
	}

	public LoadingProfile setModuleEffectiveTimeFilters(Map<String, Integer> moduleEffectiveTimeFilters) {
		this.moduleEffectiveTimeFilters = moduleEffectiveTimeFilters;
		return this;
	}

	private Set<String> getRefsetIdsNoClone() {
		return refsetIds;
	}

	private Set<String> getModuleIdsNoClone() {
		return moduleIds;
	}

	private LoadingProfile cloneObject() {
		return new LoadingProfile()
				.setEffectiveComponentFilter(this.effectiveComponentFilter)
				.setInferredAttributeMapOnConcept(this.inferredAttributeMapOnConcept)
				.setStatedAttributeMapOnConcept(this.statedAttributeMapOnConcept)
				.setStatedRelationships(this.statedRelationships)
				.setConcepts(this.concepts)
				.setDescriptions(this.descriptions)
				.setTextDefinitions(this.textDefinitions)
				.setRelationships(this.relationships)
				.setIdentifiers(this.identifiers)
				.setInactiveConcepts(this.inactiveConcepts)
				.setInactiveDescriptions(this.inactiveDescriptions)
				.setInactiveRelationships(this.inactiveRelationships)
				.setInactiveRefsetMembers(this.inactiveRefsetMembers)
				.setAllRefsets(this.allRefsets)
				.setJustRefsets(this.justRefsets)
				.setRefsetIds(new HashSet<>(this.refsetIds))
				.setIncludedReferenceSetFilenamePatterns(new HashSet<>(this.includedReferenceSetFilenamePatterns))
				.setModuleIds(new HashSet<>(this.moduleIds))
				.setModuleEffectiveTimeFilters(new HashMap<>(this.moduleEffectiveTimeFilters));
	}
}

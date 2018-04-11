package org.ihtsdo.otf.snomedboot.factory;

import com.google.common.collect.ImmutableSet;
import org.ihtsdo.otf.snomedboot.domain.ConceptConstants;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LoadingProfile implements Cloneable {

	public static final LoadingProfile light = new LoadingProfile();
	public static final LoadingProfile complete = new LoadingProfile();

	static {
		light.inferredAttributeMapOnConcept = true;
		light.descriptions = true;
		light.refsetIds.add(ConceptConstants.GB_EN_LANGUAGE_REFERENCE_SET);
		light.refsetIds.add(ConceptConstants.US_EN_LANGUAGE_REFERENCE_SET);

		complete.inferredAttributeMapOnConcept = true;
		complete.descriptions = true;
		complete.statedRelationships = true;
		complete.fullDescriptionObjects = true;
		complete.fullRelationshipObjects = true;
		complete.inactiveConcepts = true;
		complete.inactiveDescriptions = true;
		complete.inactiveRelationships = true;
		complete.inactiveRefsetMembers = true;
		complete.allRefsets = true;
		complete.fullRefsetMemberObjects = true;
	}

	private boolean snapshotEffectiveComponentFilter;
	private boolean inferredAttributeMapOnConcept;
	private boolean statedAttributeMapOnConcept;
	private boolean statedRelationships;
	private boolean descriptions;
	private boolean fullDescriptionObjects;
	private boolean fullRelationshipObjects;
	private boolean inactiveConcepts;
	private boolean inactiveDescriptions;
	private boolean inactiveRelationships;
	private boolean inactiveRefsetMembers;
	private boolean allRefsets;
	private boolean fullRefsetMemberObjects;
	private boolean justRefsets;
	private Set<String> refsetIds = new HashSet<>();
	private Set<String> includedReferenceSetFilenamePatterns = new HashSet<>();

	public LoadingProfile withSnapshotEffectiveComponentFilter() {
		return this.cloneObject().setSnapshotEffectiveComponentFilter(true);
	}

	public LoadingProfile withoutSnapshotEffectiveComponentFilter() {
		return this.cloneObject().setSnapshotEffectiveComponentFilter(false);
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

	public LoadingProfile withFullDescriptionObjects() {
		return this.cloneObject().setFullDescriptionObjects(true);
	}
	
	public LoadingProfile withoutDescriptions() {
		return this.cloneObject().setDescriptions(false);
	}

	public LoadingProfile withoutFullDescriptionObjects() {
		return this.cloneObject().setFullDescriptionObjects(false);
	}

	public LoadingProfile withFullRelationshipObjects() {
		return this.cloneObject().setFullRelationshipObjects(true);
	}

	public LoadingProfile withoutFullRelationshipObjects() {
		return this.cloneObject().setFullRelationshipObjects(false);
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

	public LoadingProfile withFullRefsetMemberObjects() {
		return this.cloneObject().setFullRefsetMemberObjects(true);
	}

	public LoadingProfile withoutFullRefsetMemberObjects() {
		return this.cloneObject().setFullRefsetMemberObjects(false);
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

	public boolean isSnapshotEffectiveComponentFilter() {
		return snapshotEffectiveComponentFilter;
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
	
	public boolean isDescriptions() {
		return descriptions;
	}

	public boolean isFullDescriptionObjects() {
		return fullDescriptionObjects;
	}

	public boolean isFullRelationshipObjects() {
		return fullRelationshipObjects;
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

	public boolean isInactiveRefsetMembers() {
		return inactiveRefsetMembers;
	}

	public boolean isAllRefsets() {
		return allRefsets;
	}

	public boolean isFullRefsetMemberObjects() {
		return fullRefsetMemberObjects;
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

	private LoadingProfile setSnapshotEffectiveComponentFilter(boolean snapshotEffectiveComponentFilter) {
		this.snapshotEffectiveComponentFilter = snapshotEffectiveComponentFilter;
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
	
	private LoadingProfile setDescriptions(boolean descriptions) {
		this.descriptions = descriptions;
		return this;
	}

	private LoadingProfile setFullDescriptionObjects(boolean fullDescriptionObjects) {
		this.fullDescriptionObjects = fullDescriptionObjects;
		return this;
	}

	private LoadingProfile setFullRelationshipObjects(boolean fullRelationshipObjects) {
		this.fullRelationshipObjects = fullRelationshipObjects;
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

	private LoadingProfile setInactiveRefsetMembers(boolean inactiveRefsetMembers) {
		this.inactiveRefsetMembers = inactiveRefsetMembers;
		return this;
	}

	private LoadingProfile setAllRefsets(boolean allRefsets) {
		this.allRefsets = allRefsets;
		return this;
	}

	private LoadingProfile setFullRefsetMemberObjects(boolean fullRefsetMemberObjects) {
		this.fullRefsetMemberObjects = fullRefsetMemberObjects;
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

	private LoadingProfile setIncludedReferenceSetFilenamePatterns(Set<String> includedReferenceSetFilenamePatterns) {
		this.includedReferenceSetFilenamePatterns = includedReferenceSetFilenamePatterns;
		return this;
	}

	private Set<String> getRefsetIdsNoClone() {
		return refsetIds;
	}

	private LoadingProfile cloneObject() {
		return new LoadingProfile()
				.setSnapshotEffectiveComponentFilter(this.snapshotEffectiveComponentFilter)
				.setInferredAttributeMapOnConcept(this.inferredAttributeMapOnConcept)
				.setStatedAttributeMapOnConcept(this.statedAttributeMapOnConcept)
				.setStatedRelationships(this.statedRelationships)
				.setDescriptions(this.descriptions)
				.setFullDescriptionObjects(this.fullDescriptionObjects)
				.setFullRelationshipObjects(this.fullRelationshipObjects)
				.setInactiveConcepts(this.inactiveConcepts)
				.setInactiveDescriptions(this.inactiveDescriptions)
				.setInactiveRelationships(this.inactiveRelationships)
				.setInactiveRefsetMembers(this.inactiveRefsetMembers)
				.setAllRefsets(this.allRefsets)
				.setFullRefsetMemberObjects(this.fullRefsetMemberObjects)
				.setJustRefsets(this.justRefsets)
				.setRefsetIds(new HashSet<>(this.refsetIds))
				.setIncludedReferenceSetFilenamePatterns(new HashSet<>(this.includedReferenceSetFilenamePatterns));
	}
}

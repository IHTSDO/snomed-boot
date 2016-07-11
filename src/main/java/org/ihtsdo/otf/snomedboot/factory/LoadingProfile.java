package org.ihtsdo.otf.snomedboot.factory;

import com.google.common.collect.ImmutableSet;
import org.ihtsdo.otf.snomedboot.domain.ConceptConstants;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LoadingProfile implements Cloneable {

	public static final LoadingProfile light = new LoadingProfile();
	public static final LoadingProfile full = new LoadingProfile();

	static {
		light.attributeMapOnConcept = true;
		light.refsetIds.add(ConceptConstants.GB_EN_LANGUAGE_REFERENCE_SET);
		light.refsetIds.add(ConceptConstants.US_EN_LANGUAGE_REFERENCE_SET);

		full.attributeMapOnConcept = true;
		full.fullDescriptionObjects = true;
		full.fullRelationshipObjects = true;
		full.inactiveConcepts = true;
		full.inactiveDescriptions = true;
		full.inactiveRelationships = true;
		full.inactiveRefsetMembers = true;
		full.allRefsets = true;
		full.fullRefsetMemberObjects = true;
	}

	private boolean attributeMapOnConcept;
	private boolean statedRelationships;
	private boolean fullDescriptionObjects;
	private boolean fullRelationshipObjects;
	private boolean inactiveConcepts = true;
	private boolean inactiveDescriptions;
	private boolean inactiveRelationships;
	private boolean inactiveRefsetMembers;
	private boolean allRefsets;
	private boolean fullRefsetMemberObjects;
	private Set<String> refsetIds = new HashSet<>();

	public LoadingProfile withAttributeMapOnConcept() {
		return this.clone().setAttributeMapOnConcept(true);
	}

	public LoadingProfile withoutAttributeMapOnConcept() {
		return this.clone().setAttributeMapOnConcept(false);
	}

	public LoadingProfile withStatedRelationships() {
		return this.clone().setStatedRelationships(true);
	}

	public LoadingProfile withoutStatedRelationships() {
		return this.clone().setStatedRelationships(false);
	}

	public LoadingProfile withFullDescriptionObjects() {
		return this.clone().setFullDescriptionObjects(true);
	}

	public LoadingProfile withoutFullDescriptionObjects() {
		return this.clone().setFullDescriptionObjects(false);
	}

	public LoadingProfile withFullRelationshipObjects() {
		return this.clone().setFullRelationshipObjects(true);
	}

	public LoadingProfile withoutFullRelationshipObjects() {
		return this.clone().setFullRelationshipObjects(false);
	}

	public LoadingProfile withInactiveComponents() {
		return this.clone().setInactiveConcepts(true).setInactiveDescriptions(true).setInactiveRelationships(true);
	}

	public LoadingProfile withInactiveConcepts() {
		return this.clone().setInactiveConcepts(true);
	}

	public LoadingProfile withoutInactiveConcepts() {
		return this.clone().setInactiveConcepts(false);
	}

	public LoadingProfile withInactiveDescriptions() {
		return this.clone().setInactiveDescriptions(true);
	}

	public LoadingProfile withoutInactiveDescriptions() {
		return this.clone().setInactiveDescriptions(false);
	}

	public LoadingProfile withInactiveRelationships() {
		return this.clone().setInactiveRelationships(true);
	}

	public LoadingProfile withoutInactiveRelationships() {
		return this.clone().setInactiveRelationships(false);
	}

	public LoadingProfile withInactiveRefsetMembers() {
		return this.clone().setInactiveRefsetMembers(true);
	}
	public LoadingProfile withoutInactiveRefsetMembers() {
		return this.clone().setInactiveRefsetMembers(false);
	}

	public LoadingProfile withAllRefsets() {
		return this.clone().setAllRefsets(true);
	}

	public LoadingProfile withoutAllRefsets() {
		return this.clone().setAllRefsets(false);
	}

	public LoadingProfile withFullRefsetMemberObjects() {
		return this.clone().setFullRefsetMemberObjects(true);
	}

	public LoadingProfile withoutFullRefsetMemberObjects() {
		return this.clone().setFullRefsetMemberObjects(false);
	}

	public LoadingProfile withRefset(String refsetId) {
		return withRefsets(refsetId);
	}

	public LoadingProfile withRefsets(String... refsetId) {
		final LoadingProfile clone = this.clone();
		Collections.addAll(clone.getRefsetIdsNoClone(), refsetId);
		return clone;
	}

	public LoadingProfile withoutRefset(String refsetId) {
		return withoutRefsets(refsetId);
	}

	public LoadingProfile withoutRefsets(String... refsetId) {
		final LoadingProfile clone = this.clone();
		for (String id : refsetId) {
			clone.getRefsetIdsNoClone().remove(id);
		}
		return clone;
	}

	public LoadingProfile withoutAnyRefsets() {
		final LoadingProfile clone = this.clone();
		clone.refsetIds.clear();
		return clone;
	}

	public boolean isAttributeMapOnConcept() {
		return attributeMapOnConcept;
	}

	public boolean isStatedRelationships() {
		return statedRelationships;
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

	public boolean isRefset(String refsetId) {
		return refsetIds.contains(refsetId);
	}

	public ImmutableSet<String> getRefsetIds() {
		return ImmutableSet.copyOf(refsetIds);
	}

	private LoadingProfile setAttributeMapOnConcept(boolean attributeMapOnConcept) {
		this.attributeMapOnConcept = attributeMapOnConcept;
		return this;
	}

	public LoadingProfile setStatedRelationships(boolean statedRelationships) {
		this.statedRelationships = statedRelationships;
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

	public LoadingProfile setFullRefsetMemberObjects(boolean fullRefsetMemberObjects) {
		this.fullRefsetMemberObjects = fullRefsetMemberObjects;
		return this;
	}

	private LoadingProfile setRefsetIds(Set<String> refsetIds) {
		this.refsetIds = refsetIds;
		return this;
	}

	private Set<String> getRefsetIdsNoClone() {
		return refsetIds;
	}

	@Override
	protected LoadingProfile clone() {
		return new LoadingProfile()
				.setAttributeMapOnConcept(this.attributeMapOnConcept)
				.setStatedRelationships(this.statedRelationships)
				.setFullDescriptionObjects(this.fullDescriptionObjects)
				.setFullRelationshipObjects(this.fullRelationshipObjects)
				.setInactiveConcepts(this.inactiveConcepts)
				.setInactiveDescriptions(this.inactiveDescriptions)
				.setInactiveRelationships(this.inactiveRelationships)
				.setInactiveRefsetMembers(this.inactiveRefsetMembers)
				.setAllRefsets(this.allRefsets)
				.setFullRefsetMemberObjects(this.fullRefsetMemberObjects)
				.setRefsetIds(new HashSet<>(this.refsetIds));
	}
}

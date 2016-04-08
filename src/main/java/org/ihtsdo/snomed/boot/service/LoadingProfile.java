package org.ihtsdo.snomed.boot.service;

import com.google.common.collect.ImmutableSet;
import org.ihtsdo.snomed.boot.domain.ConceptConstants;

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
		full.descriptionsOfAllTypes = true;
		full.relationshipsOfAllTypes = true;
		full.inactiveConcepts = true;
		full.inactiveDescriptions = true;
		full.inactiveRelationships = true;
		full.inactiveRefsetMembers = true;
		full.allRefsets = true;
	}

	private boolean attributeMapOnConcept;
	private boolean descriptionsOfAllTypes;
	private boolean relationshipsOfAllTypes;
	private boolean inactiveConcepts = true;
	private boolean inactiveDescriptions;
	private boolean inactiveRelationships;
	private boolean inactiveRefsetMembers;
	private boolean allRefsets;
	private Set<String> refsetIds = new HashSet<>();

	public LoadingProfile withAttributeMapOnConcept() {
		return this.clone().setAttributeMapOnConcept(true);
	}

	public LoadingProfile withoutAttributeMapOnConcept() {
		return this.clone().setAttributeMapOnConcept(false);
	}

	public LoadingProfile withDescriptionsOfAllTypes() {
		return this.clone().setDescriptionsOfAllTypes(true);
	}

	public LoadingProfile withoutDescriptionsOfAllTypes() {
		return this.clone().setDescriptionsOfAllTypes(false);
	}

	public LoadingProfile withRelationshipsOfAllTypes() {
		return this.clone().setRelationshipsOfAllTypes(true);
	}

	public LoadingProfile withoutRelationshipsOfAllTypes() {
		return this.clone().setRelationshipsOfAllTypes(false);
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

	public boolean isDescriptionsOfAllTypes() {
		return descriptionsOfAllTypes;
	}

	public boolean isRelationshipsOfAllTypes() {
		return relationshipsOfAllTypes;
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

	private LoadingProfile setDescriptionsOfAllTypes(boolean descriptionsOfAllTypes) {
		this.descriptionsOfAllTypes = descriptionsOfAllTypes;
		return this;
	}

	private LoadingProfile setRelationshipsOfAllTypes(boolean relationshipsOfAllTypes) {
		this.relationshipsOfAllTypes = relationshipsOfAllTypes;
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
				.setDescriptionsOfAllTypes(this.descriptionsOfAllTypes)
				.setRelationshipsOfAllTypes(this.relationshipsOfAllTypes)
				.setInactiveConcepts(this.inactiveConcepts)
				.setInactiveDescriptions(this.inactiveDescriptions)
				.setInactiveRelationships(this.inactiveRelationships)
				.setInactiveRefsetMembers(this.inactiveRefsetMembers)
				.setRefsetIds(new HashSet<>(this.refsetIds));
	}
}

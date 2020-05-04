package org.ihtsdo.otf.snomedboot.factory.implementation.standard;

import org.ihtsdo.otf.snomedboot.domain.Concept;
import org.ihtsdo.otf.snomedboot.domain.Description;
import org.ihtsdo.otf.snomedboot.domain.Relationship;

import java.util.*;

public class ConceptImpl implements Concept {

	private final Long id;
	private String effectiveTime;
	private boolean active;
	private String moduleId;
	private String definitionStatusId;
	private String fsn;
	private final Map<String, Set<String>> inferredAttributes;
	private final Map<String, Set<String>> statedAttributes;
	private final Set<Concept> inferredParents;
	private final Set<Concept> statedParents;
	private final Set<Concept> inferredChildren;
	private final Set<Concept> statedChildren;
	private final Set<Long> memberOfRefsetIds;
	private final List<Relationship> relationships;
	private final List<Description> descriptions;

	public ConceptImpl(String id) {
		this.id = Long.parseLong(id);
		inferredAttributes = new HashMap<>();
		statedAttributes = new HashMap<>();
		inferredParents = new HashSet<>();
		statedParents = new HashSet<>();
		inferredChildren = new HashSet<>();
		statedChildren = new HashSet<>();
		memberOfRefsetIds = new HashSet<>();
		relationships = new ArrayList<>();
		descriptions = new ArrayList<>();
	}

	public ConceptImpl(String conceptId, String effectiveTime, boolean active, String moduleId, String definitionStatusId) {
		this(conceptId);
		this.effectiveTime = effectiveTime;
		this.active = active;
		this.moduleId = moduleId;
		this.definitionStatusId = definitionStatusId;
	}

	public void addMemberOfRefsetId(Long refsetId) {
		memberOfRefsetIds.add(refsetId);
	}

	@Override
	public Set<Long> getMemberOfRefsetIds() {
		return memberOfRefsetIds;
	}

	/**
	 * @return A set of all inferred ancestors
	 * @throws IllegalStateException if an active relationship is found pointing to an inactive parent concept
	 * or if an ancestor loop is found.
	 */
	@Override
	public Set<Long> getInferredAncestorIds() throws IllegalStateException {
		final Stack<Long> stack = new Stack<>();
		stack.push(id);
		return collectParentIds(this, new HashSet<Long>(), stack, true);
	}

	/**
	 * @return A set of all stated ancestors
	 * @throws IllegalStateException if an active relationship is found pointing to an inactive parent concept
	 * or if an ancestor loop is found.
	 */
	@Override
	public Set<Long> getStatedAncestorIds() throws IllegalStateException {
		final Stack<Long> stack = new Stack<>();
		stack.push(id);
		return collectParentIds(this, new HashSet<Long>(), stack, false);
	}

	private Set<Long> collectParentIds(ConceptImpl concept, Set<Long> ancestors, Stack<Long> stack, boolean inferred) {
		for (Concept parentInt : inferred ? concept.inferredParents : concept.statedParents) {
			ConceptImpl parent = (ConceptImpl) parentInt;
			if (!parent.isActive()) {
				throw new IllegalStateException("Is-a relationship points to inactive parent concept: " + concept.getId() + " -> " + parent.getId());
			}
			final Long parentId = parent.id;
			if (stack.contains(parentId)) {
				stack.push(parentId);
				throw new IllegalStateException("Ancestor loop detected: " + stack.toString());
			}
			ancestors.add(parentId);
			stack.push(parentId);
			collectParentIds(parent, ancestors, stack, inferred);
			stack.pop();
		}
		return ancestors;
	}
	
	/**
	 * @return A set of all inferred descendants
	 */
	@Override
	public Set<Long> getInferredDescendantIds() throws IllegalStateException {
		return collectChildIds(this, new HashSet<Long>(),true);
	}

	/**
	 * @return A set of all stated ancestors
	 */
	@Override
	public Set<Long> getStatedDescendantIds() throws IllegalStateException {
		return collectChildIds(this, new HashSet<Long>(),  false);
	}
	
	private Set<Long> collectChildIds(ConceptImpl concept, Set<Long> descendants, boolean inferred) {
		for (Concept childInt : inferred ? concept.inferredChildren : concept.statedChildren) {
			ConceptImpl child = (ConceptImpl) childInt;
			if (!child.isActive()) {
				throw new IllegalStateException("Is-a relationship points to inactive child concept: " + concept.getId() + " -> " + child.getId());
			}
			final Long childId = child.id;
			descendants.add(childId);
			collectChildIds(child, descendants, inferred);
		}
		return descendants;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	public void addInferredParent(Concept parentConcept) {
		inferredParents.add(parentConcept);
	}

	public void removeInferredParent(Concept parentConcept) {
		inferredParents.remove(parentConcept);
	}
	
	public Set<Concept> getInferredParents() {
		return inferredParents;
	}

	public void addStatedParent(Concept parentConcept) {
		statedParents.add(parentConcept);
	}

	public void removeStatedParent(Concept parentConcept) {
		statedParents.remove(parentConcept);
	}
	
	public Set<Concept> getStatedParents() {
		return statedParents;
	}
	
	public void addInferredChild(Concept childConcept) {
		inferredChildren.add(childConcept);
	}

	public void removeInferredChild(Concept childConcept) {
		inferredChildren.remove(childConcept);
	}

	public void addStatedChild(Concept childConcept) {
		statedChildren.add(childConcept);
	}

	public void removeStatedChild(Concept childConcept) {
		statedChildren.remove(childConcept);
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public String getEffectiveTime() {
		return effectiveTime;
	}

	@Override
	public String getModuleId() {
		return moduleId;
	}

	@Override
	public String getDefinitionStatusId() {
		return definitionStatusId;
	}

	public void setFsn(String fsn) {
		this.fsn = fsn;
	}

	@Override
	public String getFsn() {
		return fsn;
	}

	@Override
	public Map<String, Set<String>> getInferredAttributes() {
		return inferredAttributes;
	}

	public void addInferredAttribute(String type, String value) {
		inferredAttributes.computeIfAbsent(type, t -> new HashSet<>()).add(value);
	}

	@Override
	public Map<String, Set<String>> getStatedAttributes() {
		return statedAttributes;
	}

	public void addStatedAttribute(String type, String value) {
		statedAttributes.computeIfAbsent(type, t -> new HashSet<>()).add(value);
	}

	public void addRelationship(Relationship relationship) {
		relationships.add(relationship);
	}

	@Override
	public List<Relationship> getRelationships() {
		return relationships;
	}

	public void addDescription(Description description) {
		descriptions.add(description);
	}

	@Override
	public List<Description> getDescriptions() {
		return descriptions;
	}

	@Override
	public String toString() {
		return id + " | " + fsn + " | ";
	}
}

package org.ihtsdo.otf.snomedboot;

import org.ihtsdo.otf.snomedboot.factory.LoadingProfile;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

class ReleaseFiles {

	private final List<Path> conceptPaths = new ArrayList<>();
	private final List<Path> descriptionPaths = new ArrayList<>();
	private final List<Path> textDefinitionPaths = new ArrayList<>();
	private final List<Path> relationshipPaths = new ArrayList<>();
	private final List<Path> concreteRelationshipPaths = new ArrayList<>();
	private final List<Path> statedRelationshipPaths = new ArrayList<>();
	private final List<Path> identifierPaths = new ArrayList<>();
	private final List<Path> refsetPaths = new ArrayList<>();

	public void addAll(ReleaseFiles releaseFiles) {
		conceptPaths.addAll(releaseFiles.getConceptPaths());
		descriptionPaths.addAll(releaseFiles.getDescriptionPaths());
		textDefinitionPaths.addAll(releaseFiles.getTextDefinitionPaths());
		relationshipPaths.addAll(releaseFiles.getRelationshipPaths());
		concreteRelationshipPaths.addAll(releaseFiles.getConcreteRelationshipPaths());
		statedRelationshipPaths.addAll(releaseFiles.getStatedRelationshipPaths());
		identifierPaths.addAll(releaseFiles.getIdentifierPaths());
		refsetPaths.addAll(releaseFiles.getRefsetPaths());
	}

	public void addConceptPath(Path file) {
		conceptPaths.add(file);
	}

	public void addDescriptionPath(Path file) {
		descriptionPaths.add(file);
	}

	public void addTextDefinitionPath(Path file) {
		textDefinitionPaths.add(file);
	}

	public void addRelationshipPath(Path file) {
		relationshipPaths.add(file);
	}

	public void addConcreteRelationshipPath(Path file) {
		concreteRelationshipPaths.add(file);
	}

	public void addStatedRelationshipPath(Path file) {
		statedRelationshipPaths.add(file);
	}

	public void addIdentifierPath(Path file) {
		identifierPaths.add(file);
	}

	public void addRefsetPath(Path file) {
		refsetPaths.add(file);
	}

	public List<Path> getConceptPaths() {
		return conceptPaths;
	}

	public List<Path> getDescriptionPaths() {
		return descriptionPaths;
	}

	public List<Path> getTextDefinitionPaths() {
		return textDefinitionPaths;
	}

	public List<Path> getRelationshipPaths() {
		return relationshipPaths;
	}

	public List<Path> getConcreteRelationshipPaths() {
		return concreteRelationshipPaths;
	}

	public List<Path> getStatedRelationshipPaths() {
		return statedRelationshipPaths;
	}

	public List<Path> getIdentifierPaths() {
		return identifierPaths;
	}

	public List<Path> getRefsetPaths() {
		return refsetPaths;
	}

	public void assertFullSet(LoadingProfile loadingProfile, String debugInfo) throws FileNotFoundException {
		if (!loadingProfile.isJustRefsets()) {
			if (conceptPaths.isEmpty()) {
				throw new FileNotFoundException("No Concept RF2 file found " + debugInfo);
			} else if (loadingProfile.isDescriptions() && descriptionPaths.isEmpty()) {
				throw new FileNotFoundException("No Description RF2 file found " + debugInfo);
			} else if (relationshipPaths.isEmpty()) {
				throw new FileNotFoundException("No Relationship RF2 file found " + debugInfo);
			}
		}
	}

	@Override
	public String toString() {
		return "ReleaseFiles{" +
				"conceptPaths=" + conceptPaths +
				", descriptionPaths=" + descriptionPaths +
				", textDefinitionPaths=" + textDefinitionPaths +
				", relationshipPaths=" + relationshipPaths +
				", concreteRelationshipPaths=" + concreteRelationshipPaths +
				", statedRelationshipPaths=" + statedRelationshipPaths +
				", identifierPaths=" + identifierPaths +
				", refsetPaths=" + refsetPaths +
				'}';
	}
}

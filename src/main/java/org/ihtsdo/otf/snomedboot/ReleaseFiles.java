package org.ihtsdo.otf.snomedboot;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

class ReleaseFiles {

	private Path conceptPath;
	private Path descriptionPath;
	private Path textDefinitionPath;
	private Path relationshipPath;
	private List<Path> refsetPaths;

	public ReleaseFiles() {
		refsetPaths = new ArrayList<>();
	}

	public Path getConceptPath() {
		return conceptPath;
	}

	public void setConceptPath(Path conceptPath) {
		this.conceptPath = conceptPath;
	}

	public Path getDescriptionPath() {
		return descriptionPath;
	}

	public void setDescriptionPath(Path descriptionPath) {
		this.descriptionPath = descriptionPath;
	}

	public Path getTextDefinitionPath() {
		return textDefinitionPath;
	}

	public void setTextDefinitionPath(Path textDefinitionPath) {
		this.textDefinitionPath = textDefinitionPath;
	}

	public Path getRelationshipPath() {
		return relationshipPath;
	}

	public void setRelationshipPath(Path relationshipPath) {
		this.relationshipPath = relationshipPath;
	}

	public List<Path> getRefsetPaths() {
		return refsetPaths;
	}

	public void setRefsetPaths(List<Path> refsetPaths) {
		this.refsetPaths = refsetPaths;
	}

	public void assertFullSet() throws FileNotFoundException {
		if (conceptPath == null) {
			throw new FileNotFoundException("Concept RF2 file not found.");
		} else if (descriptionPath == null) {
			throw new FileNotFoundException("Description RF2 file not found.");
		} else if (textDefinitionPath == null) {
			throw new FileNotFoundException("TextDefinition RF2 file not found.");
		} else if (relationshipPath == null) {
			throw new FileNotFoundException("Relationship RF2 file not found.");
		}
	}

	@Override
	public String toString() {
		return "ReleaseFiles{" +
				"conceptPath=" + conceptPath +
				", descriptionPath=" + descriptionPath +
				", textDefinitionPath=" + textDefinitionPath +
				", relationshipPath=" + relationshipPath +
				", refsetPaths=" + refsetPaths +
				'}';
	}
}

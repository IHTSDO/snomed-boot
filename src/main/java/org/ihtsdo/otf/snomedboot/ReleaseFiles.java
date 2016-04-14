package org.ihtsdo.otf.snomedboot;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

class ReleaseFiles {

	private Path conceptSnapshot;
	private Path descriptionSnapshot;
	private Path textDefinitionSnapshot;
	private Path relationshipSnapshot;
	private List<Path> refsetSnapshots;

	public ReleaseFiles() {
		refsetSnapshots = new ArrayList<>();
	}

	public Path getConceptSnapshot() {
		return conceptSnapshot;
	}

	public void setConceptSnapshot(Path conceptSnapshot) {
		this.conceptSnapshot = conceptSnapshot;
	}

	public Path getDescriptionSnapshot() {
		return descriptionSnapshot;
	}

	public void setDescriptionSnapshot(Path descriptionSnapshot) {
		this.descriptionSnapshot = descriptionSnapshot;
	}

	public Path getTextDefinitionSnapshot() {
		return textDefinitionSnapshot;
	}

	public void setTextDefinitionSnapshot(Path textDefinitionSnapshot) {
		this.textDefinitionSnapshot = textDefinitionSnapshot;
	}

	public Path getRelationshipSnapshot() {
		return relationshipSnapshot;
	}

	public void setRelationshipSnapshot(Path relationshipSnapshot) {
		this.relationshipSnapshot = relationshipSnapshot;
	}

	public List<Path> getRefsetSnapshots() {
		return refsetSnapshots;
	}

	public void setRefsetSnapshots(List<Path> refsetSnapshots) {
		this.refsetSnapshots = refsetSnapshots;
	}

	public void assertFullSet() throws FileNotFoundException {
		if (conceptSnapshot == null) {
			throw new FileNotFoundException("Concept RF2 file not found.");
		} else if (descriptionSnapshot == null) {
			throw new FileNotFoundException("Description RF2 file not found.");
		} else if (textDefinitionSnapshot == null) {
			throw new FileNotFoundException("TextDefinition RF2 file not found.");
		} else if (relationshipSnapshot == null) {
			throw new FileNotFoundException("Relationship RF2 file not found.");
		}
	}

	@Override
	public String toString() {
		return "ReleaseFiles{" +
				"conceptSnapshot=" + conceptSnapshot +
				", descriptionSnapshot=" + descriptionSnapshot +
				", textDefinitionSnapshot=" + textDefinitionSnapshot +
				", relationshipSnapshot=" + relationshipSnapshot +
				", refsetSnapshots=" + refsetSnapshots +
				'}';
	}
}

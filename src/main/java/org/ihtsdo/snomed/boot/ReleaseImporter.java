package org.ihtsdo.snomed.boot;

import org.ihtsdo.snomed.boot.domain.Concept;
import org.ihtsdo.snomed.boot.domain.ConceptConstants;
import org.ihtsdo.snomed.boot.domain.Description;
import org.ihtsdo.snomed.boot.domain.Relationship;
import org.ihtsdo.snomed.boot.domain.rf2.ComponentFields;
import org.ihtsdo.snomed.boot.domain.rf2.DescriptionFields;
import org.ihtsdo.snomed.boot.domain.rf2.RefsetFields;
import org.ihtsdo.snomed.boot.domain.rf2.RelationshipFields;
import org.ihtsdo.snomed.boot.service.LoadingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.NumberFormat;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class ReleaseImporter {

	public static final Charset UTF_8 = Charset.forName("UTF-8");
	private final ComponentFactory componentFactory;
	private final ComponentStore componentStore;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public ReleaseImporter() {
		componentStore = new ComponentStore();
		componentFactory = new ComponentFactory(componentStore);
	}

	public Map<Long, Concept> loadReleaseFiles(String releaseDirPath, LoadingMode loadingMode) throws IOException {
		ReleaseFiles releaseFiles = findFiles(releaseDirPath);
		logger.info("Loading release files {}", releaseFiles);
		loadConcepts(releaseFiles.getConceptSnapshot());
		loadRelationships(releaseFiles.getRelationshipSnapshot(), loadingMode);
		loadDescriptions(releaseFiles.getDescriptionSnapshot(), loadingMode);
		final List<Path> refsetSnapshots = releaseFiles.getRefsetSnapshots();
		for (Path refsetSnapshot : refsetSnapshots) {
			loadRefsets(refsetSnapshot);
		}
		logger.info("All in memory. Using approx {} MB of memory.", formatAsMB(Runtime.getRuntime().totalMemory()));

		return componentStore.getConcepts();
	}

	private ReleaseFiles findFiles(String releaseDirPath) throws IOException {
		final File releaseDir = new File(releaseDirPath);
		if (!releaseDir.isDirectory()) {
			throw new FileNotFoundException("Could not find release directory.");
		}

		final ReleaseFiles releaseFiles = new ReleaseFiles();

		Files.walkFileTree(releaseDir.toPath(), EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				final String fileName = file.getFileName().toString();
				if (fileName.endsWith(".txt")) {
					if (fileName.startsWith("sct2_Concept_Snapshot")) {
						releaseFiles.setConceptSnapshot(file);
					} else if (fileName.startsWith("sct2_Description_Snapshot")) {
						releaseFiles.setDescriptionSnapshot(file);
					} else if (fileName.startsWith("sct2_TextDefinition_Snapshot")) {
						releaseFiles.setTextDefinitionSnapshot(file);
					} else if (fileName.startsWith("sct2_Relationship_Snapshot")) {
						releaseFiles.setRelationshipSnapshot(file);
					} else if (fileName.startsWith("der2_")) {
						releaseFiles.getRefsetSnapshots().add(file);
					}
				}
				return FileVisitResult.CONTINUE;
			}
		});

		releaseFiles.assertFullSet();

		return releaseFiles;
	}

	private void loadConcepts(Path rf2File) throws IOException {
		readLines(rf2File, new ValuesHandler() {
			@Override
			public void handle(String[] values) {
				Long conceptId = new Long(values[ComponentFields.id]);
				componentFactory.createConcept(conceptId, values);
			}
		}, "concepts");
	}

	private void loadRelationships(Path rf2File, final LoadingMode loadingMode) throws IOException {
		readLines(rf2File, new ValuesHandler() {
			@Override
			public void handle(String[] values) {
				if (values[RelationshipFields.active].equals("1")) {
					final Concept concept = getCreateConcept(values[RelationshipFields.sourceId]);
					final String type = values[RelationshipFields.typeId];
					final String value = values[RelationshipFields.destinationId];
					concept.addAttribute(type, value);
					if (type.equals(ConceptConstants.isA)) {
						concept.addParent(getCreateConcept(value));
					}
					if (loadingMode == LoadingMode.full) {
						concept.addRelationship(new Relationship(values));
					}
				}
			}
		}, "relationships");
	}

	private void loadDescriptions(Path rf2File, final LoadingMode loadingMode) throws IOException {
		readLines(rf2File, new ValuesHandler() {
			@Override
			public void handle(String[] values) {
				if ("1".equals(values[DescriptionFields.active])) {
					final Concept concept = getCreateConcept(new Long(values[DescriptionFields.conceptId]));
					if (ConceptConstants.FSN.equals(values[DescriptionFields.typeId])) {
						concept.setFsn(values[DescriptionFields.term]);
					}
					if (loadingMode == LoadingMode.full) {
						concept.addDescription(new Description(values));
					}
				}
			}
		}, "descriptions");
	}

	private void loadRefsets(Path rf2File) throws IOException {
		readLines(rf2File, new ValuesHandler() {
			@Override
			public void handle(String[] values) {
				if ("1".equals(values[DescriptionFields.active])) {
					final String referencedComponentId = values[RefsetFields.referencedComponentId];
					if (Concept.isConceptId(referencedComponentId)) {
						getCreateConcept(new Long(referencedComponentId)).addMemberOfRefsetId(new Long(values[RefsetFields.refsetId]));
					}
				}
			}
		}, "reference set members");
	}

	private Concept getCreateConcept(String id) {
		return getCreateConcept(new Long(id));
	}

	private Concept getCreateConcept(Long id) {
		Concept concept = componentStore.getConcepts().get(id);
		if (concept == null) {
			concept = new Concept(id);
			componentStore.addConcept(concept);
		}
		return concept;
	}

	private void readLines(Path rf2FilePath, ValuesHandler valuesHandler, String componentType) throws IOException {
		logger.info("Reading {} ", componentType);
		long linesRead = 0L;
		try (final BufferedReader reader = Files.newBufferedReader(rf2FilePath, UTF_8)) {
			String line;
			reader.readLine(); // discard header line
			while ((line = reader.readLine()) != null) {
				valuesHandler.handle(line.split("\\t"));
				linesRead++;
				if (linesRead % 100000 == 0) {
					logger.info("{} {} read", linesRead, componentType);
				}
			}
		}
		logger.info("{} {} read in total", linesRead, componentType);
	}

	private String formatAsMB(long bytes) {
		return NumberFormat.getInstance().format((bytes / 1024) / 1024);
	}

	private interface ValuesHandler {
		void handle(String[] values);
	}

}

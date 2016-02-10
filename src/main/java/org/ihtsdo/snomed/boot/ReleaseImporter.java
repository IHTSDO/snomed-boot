package org.ihtsdo.snomed.boot;

import org.ihtsdo.snomed.boot.domain.Concept;
import org.ihtsdo.snomed.boot.domain.ConceptConstants;
import org.ihtsdo.snomed.boot.domain.Description;
import org.ihtsdo.snomed.boot.domain.Relationship;
import org.ihtsdo.snomed.boot.domain.rf2.*;
import org.ihtsdo.snomed.boot.service.LoadingProfile;
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
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReleaseImporter {

	public static final Charset UTF_8 = Charset.forName("UTF-8");
	private final ComponentFactory componentFactory;
	private final ComponentStore componentStore;
	private final ExecutorService executorService;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public ReleaseImporter() {
		componentStore = new ComponentStore();
		componentFactory = new ComponentFactory(componentStore);
		executorService = Executors.newCachedThreadPool();
	}

	public Map<Long, Concept> loadReleaseFiles(String releaseDirPath, LoadingProfile loadingProfile) throws IOException, InterruptedException {
		ReleaseFiles releaseFiles = findFiles(releaseDirPath);
		logger.info("Loading release files {}", releaseFiles);
		loadConcepts(releaseFiles.getConceptSnapshot(), loadingProfile);

		List<Callable<String>> tasks = new ArrayList<>();
		tasks.add(loadRelationships(releaseFiles.getRelationshipSnapshot(), loadingProfile));
		tasks.add(loadDescriptions(releaseFiles.getDescriptionSnapshot(), loadingProfile));
		final List<Path> refsetSnapshots = releaseFiles.getRefsetSnapshots();
		for (Path refsetSnapshot : refsetSnapshots) {
			tasks.add(loadRefsets(refsetSnapshot, loadingProfile));
		}

		executorService.invokeAll(tasks);

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

	private void loadConcepts(Path rf2File, final LoadingProfile loadingProfile) throws IOException {
		readLines(rf2File, new ValuesHandler() {
			@Override
			public void handle(String[] values) {
				if (loadingProfile.isInactiveConcepts() || "1".equals(values[ConceptFields.active])) {
					Long conceptId = new Long(values[ComponentFields.id]);
					componentFactory.createConcept(conceptId, values);
				}
			}
		}, "concepts");
	}

	private Callable<String> loadRelationships(Path rf2File, final LoadingProfile loadingProfile) throws IOException {
		return readLinesCallable(rf2File, new ValuesHandler() {
			@Override
			public void handle(String[] values) {
				if (loadingProfile.isInactiveRelationships() || "1".equals(values[RelationshipFields.active])) {
					final Concept concept = getCreateConcept(values[RelationshipFields.sourceId]);
					final String type = values[RelationshipFields.typeId];
					final String value = values[RelationshipFields.destinationId];
					concept.addAttribute(type, value);
					if (type.equals(ConceptConstants.isA)) {
						concept.addParent(getCreateConcept(value));
					}
					if (loadingProfile.isRelationshipsOfAllTypes()) {
						concept.addRelationship(new Relationship(values));
					}
				}
			}
		}, "relationships");
	}

	private Callable<String> loadDescriptions(Path rf2File, final LoadingProfile loadingProfile) throws IOException {
		return readLinesCallable(rf2File, new ValuesHandler() {
			@Override
			public void handle(String[] values) {
				if (loadingProfile.isInactiveDescriptions() || "1".equals(values[DescriptionFields.active])) {
					final Concept concept = getCreateConcept(new Long(values[DescriptionFields.conceptId]));
					if (ConceptConstants.FSN.equals(values[DescriptionFields.typeId])) {
						concept.setFsn(values[DescriptionFields.term]);
					}
					if (loadingProfile.isDescriptionsOfAllTypes()) {
						concept.addDescription(new Description(values));
					}
				}
			}
		}, "descriptions");
	}

	private Callable<String> loadRefsets(Path rf2File, final LoadingProfile loadingProfile) throws IOException {
		return readLinesCallable(rf2File, new ValuesHandler() {
			@Override
			public void handle(String[] values) {
				if (loadingProfile.isInactiveRefsetMembers() || "1".equals(values[RefsetFields.active])) {
					final String refsetId = values[RefsetFields.refsetId];
					if (loadingProfile.isAllRefsets() || loadingProfile.isRefset(refsetId)) {
						final String referencedComponentId = values[RefsetFields.referencedComponentId];
						if (Concept.isConceptId(referencedComponentId)) {
							getCreateConcept(new Long(referencedComponentId)).addMemberOfRefsetId(new Long(refsetId));
						}
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

	private Callable<String> readLinesCallable(final Path rf2FilePath, final ValuesHandler valuesHandler, final String componentType) {
		return new Callable<String>() {
			@Override
			public String call() throws Exception {
				readLines(rf2FilePath, valuesHandler, componentType);
				return null;
			}
		};
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
			}
		}
		logger.info("{} {} read from {}", linesRead, componentType, rf2FilePath.getFileName().toString());
	}

	private String formatAsMB(long bytes) {
		return NumberFormat.getInstance().format((bytes / 1024) / 1024);
	}

	private interface ValuesHandler {
		void handle(String[] values);
	}

}

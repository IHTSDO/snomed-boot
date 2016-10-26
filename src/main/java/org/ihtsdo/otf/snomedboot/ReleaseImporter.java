package org.ihtsdo.otf.snomedboot;

import org.ihtsdo.otf.snomedboot.domain.ConceptConstants;
import org.ihtsdo.otf.snomedboot.domain.rf2.*;
import org.ihtsdo.otf.snomedboot.factory.ComponentFactory;
import org.ihtsdo.otf.snomedboot.factory.FactoryUtils;
import org.ihtsdo.otf.snomedboot.factory.HistoryAwareComponentFactory;
import org.ihtsdo.otf.snomedboot.factory.LoadingProfile;
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
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReleaseImporter {

	public static final Charset UTF_8 = Charset.forName("UTF-8");

	public void loadFullReleaseFiles(String releaseDirPath, LoadingProfile loadingProfile, HistoryAwareComponentFactory componentFactory) throws ReleaseImportException {
		new ImportRun(componentFactory).doLoadReleaseFiles(releaseDirPath, loadingProfile, ImportType.FULL);
	}

	public void loadSnapshotReleaseFiles(String releaseDirPath, LoadingProfile loadingProfile, ComponentFactory componentFactory) throws ReleaseImportException {
		new ImportRun(componentFactory).doLoadReleaseFiles(releaseDirPath, loadingProfile, ImportType.SNAPSHOT);
	}

	private enum ImportType {

		SNAPSHOT("Snapshot"), FULL("Full");

		private String filenamePart;

		ImportType(String filenamePart) {
			this.filenamePart = filenamePart;
		}

		public String getFilenamePart() {
			return filenamePart;
		}
	}

	private static final class ImportRun {

		private final ComponentFactory componentFactory;

		private final ExecutorService executorService;

		private final Logger logger = LoggerFactory.getLogger(getClass());
		public static final Pattern DATE_EXTRACT_PATTERN = Pattern.compile("[^\\t]*\\t([^\\t]*)\t");

		public ImportRun(ComponentFactory componentFactory) {
			executorService = Executors.newCachedThreadPool();
			this.componentFactory = componentFactory;
		}

		private void doLoadReleaseFiles(String releaseDirPath, LoadingProfile loadingProfile, ImportType importType) throws ReleaseImportException {
			ReleaseFiles releaseFiles;
			try {
				releaseFiles = findFiles(releaseDirPath, importType.getFilenamePart());
			} catch (IOException e) {
				throw new ReleaseImportException("Failed to find release files during release import process.", e);
			}

			logger.info("Loading release files {}", releaseFiles);


			try {
				componentFactory.loadingComponentsStarting();

				if (importType == ImportType.FULL) {
					final Set<String> releaseVersions = gatherVersions(releaseFiles);
					for (String releaseVersion : releaseVersions) {
						((HistoryAwareComponentFactory) componentFactory).loadingReleaseDeltaStarting(releaseVersion);
						logger.info("Loading release {}", releaseVersion);
						loadAll(loadingProfile, releaseFiles, releaseVersion);
						((HistoryAwareComponentFactory) componentFactory).loadingReleaseDeltaFinished(releaseVersion);
					}
				} else {
					loadAll(loadingProfile, releaseFiles, null);
				}

				componentFactory.loadingComponentsCompleted();

				logger.info("Release files read. JVM total memory is approx {} MB.", formatAsMB(Runtime.getRuntime().totalMemory()));
			} catch (IOException | InterruptedException e) {
				throw new ReleaseImportException("Failed to load release files during release import process.", e);
			}
		}

		private void loadAll(LoadingProfile loadingProfile, ReleaseFiles releaseFiles, String releaseVersion) throws IOException, InterruptedException {
			loadConcepts(releaseFiles.getConceptPath(), loadingProfile, releaseVersion);

			List<Callable<String>> coreComponentTasks = new ArrayList<>();
			coreComponentTasks.add(loadRelationships(releaseFiles.getRelationshipPath(), loadingProfile, releaseVersion));
			if (loadingProfile.isStatedRelationships()) {
				coreComponentTasks.add(loadRelationships(releaseFiles.getStatedRelationshipPath(), loadingProfile, releaseVersion));
			}
			coreComponentTasks.add(loadDescriptions(releaseFiles.getDescriptionPath(), loadingProfile, releaseVersion));
			coreComponentTasks.add(loadDescriptions(releaseFiles.getTextDefinitionPath(), loadingProfile, releaseVersion));

			List<Callable<String>> refsetTasks = new ArrayList<>();
			if (loadingProfile.isAllRefsets() || !loadingProfile.getRefsetIds().isEmpty()) {
				final List<Path> refsetSnapshots = releaseFiles.getRefsetPaths();
				for (Path refsetSnapshot : refsetSnapshots) {
					refsetTasks.add(loadRefsets(refsetSnapshot, loadingProfile, releaseVersion));
				}
			}

			executorService.invokeAll(coreComponentTasks);
			executorService.invokeAll(refsetTasks);
		}

		private ReleaseFiles findFiles(String releaseDirPath, final String fileType) throws IOException {
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
						if (fileName.startsWith("sct2_Concept_" + fileType)) {
							releaseFiles.setConceptPath(file);
						} else if (fileName.startsWith("sct2_Description_" + fileType)) {
							releaseFiles.setDescriptionPath(file);
						} else if (fileName.startsWith("sct2_TextDefinition_" + fileType)) {
							releaseFiles.setTextDefinitionPath(file);
						} else if (fileName.startsWith("sct2_Relationship_" + fileType)) {
							releaseFiles.setRelationshipPath(file);
						} else if (fileName.startsWith("sct2_StatedRelationship_" + fileType)) {
							releaseFiles.setStatedRelationshipPath(file);
						} else if (fileName.startsWith("der2_") && fileName.contains(fileType)) {
							releaseFiles.getRefsetPaths().add(file);
						}
					}
					return FileVisitResult.CONTINUE;
				}
			});

			releaseFiles.assertFullSet();

			return releaseFiles;
		}

		private void loadConcepts(Path rf2File, final LoadingProfile loadingProfile, final String releaseVersion) throws IOException {
			readLines(rf2File, new ValuesHandler() {
				@Override
				public void handle(String[] values) {
					if (loadingProfile.isInactiveConcepts() || "1".equals(values[ConceptFieldIndexes.active])) {
						String conceptId = values[ComponentFieldIndexes.id];
						componentFactory.createConcept(conceptId, values[ConceptFieldIndexes.effectiveTime], values[ConceptFieldIndexes.active],
								values[ConceptFieldIndexes.moduleId], values[ConceptFieldIndexes.definitionStatusId]);
					}
				}
			}, "concepts", releaseVersion);
		}

		private Callable<String> loadRelationships(Path rf2File, final LoadingProfile loadingProfile, String releaseVersion) {
			return readLinesCallable(rf2File, new ValuesHandler() {
				@Override
				public void handle(String[] values) {
					if (loadingProfile.isInactiveRelationships() || "1".equals(values[RelationshipFieldIndexes.active])) {
						final String sourceId = values[RelationshipFieldIndexes.sourceId];
						final String type = values[RelationshipFieldIndexes.typeId];
						final String value = values[RelationshipFieldIndexes.destinationId];
						if (!ConceptConstants.STATED_RELATIONSHIP.equals(type) || loadingProfile.isStatedRelationships()) {
							if (loadingProfile.isAttributeMapOnConcept()) {
								componentFactory.addConceptAttribute(sourceId, type, value);
							}
							if (type.equals(ConceptConstants.isA)) {
								componentFactory.addConceptParent(sourceId, value);
							}
							if (loadingProfile.isFullRelationshipObjects()) {
								componentFactory.addRelationship(
										values[RelationshipFieldIndexes.id],
										values[RelationshipFieldIndexes.effectiveTime],
										values[RelationshipFieldIndexes.active],
										values[RelationshipFieldIndexes.moduleId],
										values[RelationshipFieldIndexes.sourceId],
										values[RelationshipFieldIndexes.destinationId],
										values[RelationshipFieldIndexes.relationshipGroup],
										values[RelationshipFieldIndexes.typeId],
										values[RelationshipFieldIndexes.characteristicTypeId],
										values[RelationshipFieldIndexes.modifierId]
								);
							}
						}
					}
				}
			}, "relationships", releaseVersion);
		}

		private Callable<String> loadDescriptions(Path rf2File, final LoadingProfile loadingProfile, String releaseVersion) {
			return readLinesCallable(rf2File, new ValuesHandler() {
				@Override
				public void handle(String[] values) {
					if (loadingProfile.isInactiveDescriptions() || "1".equals(values[DescriptionFieldIndexes.active])) {
						final String conceptId = values[DescriptionFieldIndexes.conceptId];
						final String value = values[DescriptionFieldIndexes.typeId];
						if (ConceptConstants.FSN.equals(value)) {
							componentFactory.addConceptFSN(conceptId, values[DescriptionFieldIndexes.term]);
						}
						if (loadingProfile.isFullDescriptionObjects()) {
							componentFactory.addDescription(
									values[DescriptionFieldIndexes.id],
									values[DescriptionFieldIndexes.effectiveTime],
									values[DescriptionFieldIndexes.active],
									values[DescriptionFieldIndexes.moduleId],
									values[DescriptionFieldIndexes.conceptId],
									values[DescriptionFieldIndexes.languageCode],
									values[DescriptionFieldIndexes.typeId],
									values[DescriptionFieldIndexes.term],
									values[DescriptionFieldIndexes.caseSignificanceId]
							);
						}
					}
				}
			}, "descriptions", releaseVersion);
		}

		private Callable<String> loadRefsets(Path rf2File, final LoadingProfile loadingProfile, String releaseVersion) {
			return readLinesCallable(rf2File, new ValuesHandler() {
				@Override
				public void handle(String[] values) {
					if (loadingProfile.isInactiveRefsetMembers() || "1".equals(values[RefsetFieldIndexes.active])) {
						final String refsetId = values[RefsetFieldIndexes.refsetId];
						if (loadingProfile.isAllRefsets() || loadingProfile.isRefset(refsetId)) {
							final String referencedComponentId = values[RefsetFieldIndexes.referencedComponentId];
							if (FactoryUtils.isConceptId(referencedComponentId)) {
								componentFactory.addConceptReferencedInRefsetId(refsetId, referencedComponentId);
							}
							if (loadingProfile.isFullRefsetMemberObjects()) {
								componentFactory.addReferenceSetMember(
										values[RefsetFieldIndexes.id],
										values[RefsetFieldIndexes.effectiveTime],
										values[RefsetFieldIndexes.active],
										values[RefsetFieldIndexes.moduleId],
										values[RefsetFieldIndexes.refsetId],
										values[RefsetFieldIndexes.referencedComponentId],
										Arrays.copyOfRange(values, RefsetFieldIndexes.referencedComponentId + 1, values.length)
								);
							}
						}
					}
				}
			}, "reference set members", releaseVersion);
		}

		private Callable<String> readLinesCallable(final Path rf2FilePath, final ValuesHandler valuesHandler, final String componentType, final String releaseVersion) {
			return new Callable<String>() {
				@Override
				public String call() throws Exception {
					try {
						readLines(rf2FilePath, valuesHandler, componentType, releaseVersion);
					} catch (Exception e) {
						logger.error("Failed to read or process lines.", e);
					}
					return null;
				}
			};
		}

		private Set<String> gatherVersions(ReleaseFiles releaseFiles) throws IOException {
			logger.info("Gathering list of release versions...");
			Set<String> versions = new TreeSet<>();
			gatherVersions(releaseFiles.getConceptPath(), versions);
			gatherVersions(releaseFiles.getDescriptionPath(), versions);
			gatherVersions(releaseFiles.getTextDefinitionPath(), versions);
			gatherVersions(releaseFiles.getRelationshipPath(), versions);
			for (Path refsetPath : releaseFiles.getRefsetPaths()) {
				gatherVersions(refsetPath, versions);
			}
			logger.info("Release versions found: {}", versions);
			return versions;
		}

		private void gatherVersions(Path filePath, Set<String> versions) throws IOException {
			try (final BufferedReader reader = Files.newBufferedReader(filePath, UTF_8)) {
				String line;
				reader.readLine(); // discard header line
				Matcher matcher;
				while ((line = reader.readLine()) != null) {
					matcher = DATE_EXTRACT_PATTERN.matcher(line);
					matcher.find();
					versions.add(matcher.group(1));
				}
			}
		}

		private void readLines(Path rf2FilePath, ValuesHandler valuesHandler, String componentType, String releaseVersion) throws IOException {
			if (releaseVersion != null) {
				logger.info("Reading {} for release {}", componentType, releaseVersion);
			} else {
				logger.info("Reading {} ", componentType);
			}
			long linesRead = 0L;
			try (final BufferedReader reader = Files.newBufferedReader(rf2FilePath, UTF_8)) {
				String line;
				reader.readLine(); // discard header line
				String[] split;
				while ((line = reader.readLine()) != null) {
					linesRead++;
					split = line.split("\\t");
					if (releaseVersion == null || releaseVersion.equals(split[ComponentFieldIndexes.effectiveTime])) {
						valuesHandler.handle(split);
					}
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

}

package org.ihtsdo.otf.snomedboot;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.ihtsdo.otf.snomedboot.domain.ConceptConstants;
import org.ihtsdo.otf.snomedboot.domain.rf2.*;
import org.ihtsdo.otf.snomedboot.factory.ComponentFactory;
import org.ihtsdo.otf.snomedboot.factory.FactoryUtils;
import org.ihtsdo.otf.snomedboot.factory.HistoryAwareComponentFactory;
import org.ihtsdo.otf.snomedboot.factory.LoadingProfile;
import org.ihtsdo.otf.snomedboot.factory.filter.LatestEffectiveDateComponentFactory;
import org.ihtsdo.otf.snomedboot.factory.filter.LatestEffectiveDateFilter;
import org.ihtsdo.otf.snomedboot.factory.filter.ModuleFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.lang.String.format;

public class ReleaseImporter {

	public static final Charset UTF_8 = StandardCharsets.UTF_8;
	private static final Logger logger = LoggerFactory.getLogger(ReleaseImporter.class);

	public void loadFullReleaseFiles(String releaseDirPath, LoadingProfile loadingProfile, HistoryAwareComponentFactory componentFactory) throws ReleaseImportException {
		new ImportRun(componentFactory).doLoadReleaseFiles(releaseDirPath, loadingProfile, ImportType.FULL);
	}

	public void loadSnapshotReleaseFiles(String releaseDirPath, LoadingProfile loadingProfile, ComponentFactory componentFactory) throws ReleaseImportException {
		new ImportRun(componentFactory).doLoadReleaseFiles(releaseDirPath, loadingProfile, ImportType.SNAPSHOT);
	}

	/**
	 * Load only the effective components from multiple snapshots archives.
	 * This is achieved by gathering the latest effectiveTime for each component and using this information within a content filter.
	 * This is useful when loading Extension archives in combination with the International Edition.
	 */
	public void loadEffectiveSnapshotReleaseFiles(Set<String> releaseDirPaths, LoadingProfile loadingProfile, ComponentFactory componentFactory) throws ReleaseImportException {
		new ImportRun(componentFactory).doLoadReleaseFiles(new ArrayList<>(releaseDirPaths), loadingProfile.withSnapshotEffectiveComponentFilter(), ImportType.SNAPSHOT);
	}

	public void loadDeltaReleaseFiles(String releaseDirPath, LoadingProfile loadingProfile, ComponentFactory componentFactory) throws ReleaseImportException {
		new ImportRun(componentFactory).doLoadReleaseFiles(releaseDirPath, loadingProfile, ImportType.DELTA);
	}

	public void loadFullReleaseFiles(InputStream releaseZip, LoadingProfile loadingProfile, HistoryAwareComponentFactory componentFactory) throws ReleaseImportException {
		File releaseDir = unzipRelease(releaseZip, ImportType.FULL);
		loadFullReleaseFiles(releaseDir.getAbsolutePath(), loadingProfile, componentFactory);
		deleteDirectory(releaseDir);
	}

	public void loadSnapshotReleaseFiles(InputStream releaseZip, LoadingProfile loadingProfile, ComponentFactory componentFactory) throws ReleaseImportException {
		File releaseDir = unzipRelease(releaseZip, ImportType.SNAPSHOT);
		loadSnapshotReleaseFiles(releaseDir.getAbsolutePath(), loadingProfile, componentFactory);
		deleteDirectory(releaseDir);
	}

	/**
	 * Load only the effective components from multiple snapshot archives.
	 * This is achieved by gathering the latest effectiveTime for each component and using this information within a content filter.
	 * This is useful when loading Extension archives in combination with the International Edition.
	 */
	public void loadEffectiveSnapshotReleaseFileStreams(Set<InputStream> releaseZips, LoadingProfile loadingProfile, ComponentFactory componentFactory) throws ReleaseImportException {
		File tempDir = createTempDir();
		int a = 1;
		for (InputStream releaseZip : releaseZips) {
			File releaseTempDir = new File(tempDir, a++ + "");
			releaseTempDir.mkdirs();
			unzipRelease(releaseZip, ImportType.SNAPSHOT, releaseTempDir);
		}
		loadEffectiveSnapshotReleaseFiles(Collections.singleton(tempDir.getAbsolutePath()), loadingProfile, componentFactory);
		deleteDirectory(tempDir);
	}

	public void loadDeltaReleaseFiles(InputStream releaseZip, LoadingProfile loadingProfile, ComponentFactory componentFactory) throws ReleaseImportException {
		File releaseDir = unzipRelease(releaseZip, ImportType.DELTA);
		loadDeltaReleaseFiles(releaseDir.getAbsolutePath(), loadingProfile, componentFactory);
		deleteDirectory(releaseDir);
	}

	public File unzipRelease(InputStream releaseZip, ImportType filenameFilter) throws ReleaseImportException {
		return unzipRelease(releaseZip, filenameFilter, createTempDir());
	}

	private void deleteDirectory(File file) {
		try {
			FileUtils.deleteDirectory(file);
		} catch (IOException e) {
			logger.warn("Failed to remove directory {}", file.getAbsolutePath());
		}
	}

	private File createTempDir() throws ReleaseImportException {
		try {
			return Files.createTempDirectory("temp-rf2-unzip").toFile();
		} catch (IOException e) {
			throw new ReleaseImportException("Failed to unzip Snomed release file.", e);
		}
	}

	private File unzipRelease(InputStream releaseZip, ImportType filenameFilter, File tempDir) throws ReleaseImportException {
		try {
			try (InputStream snomedReleaseZipStream = releaseZip) {
				File zipFile = new File(tempDir, "release.zip");
				try (FileOutputStream out = new FileOutputStream(zipFile)) {
					IOUtils.copy(snomedReleaseZipStream, out);
				}

				ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
				ZipEntry zipEntry;
				int filesUnzipped = 0;
				while ((zipEntry = zipInputStream.getNextEntry()) != null) {
					String zipEntryName = zipEntry.getName();
					if (zipEntryName.contains(filenameFilter.getFilenamePart()) && !zipEntry.isDirectory()) {
						// Create file without directory nesting
						File file = new File(tempDir, new File(zipEntryName).getName());
						logger.info("Unzipping file to {}", file.getAbsolutePath());
						file.createNewFile();
						try (FileOutputStream entryOutputStream = new FileOutputStream(file)) {
							IOUtils.copy(zipInputStream, entryOutputStream);
							filesUnzipped++;
						}
					}
				}
				if (filesUnzipped == 0) {
					throw new IllegalStateException("No " + filenameFilter.getFilenamePart() + " files found in archive: " + zipFile.getAbsolutePath());
				}
			}
			return tempDir;
		} catch (IOException e) {
			throw new ReleaseImportException("Failed to unzip Snomed " + filenameFilter.getFilenamePart()  + " release file.", e);
		}
	}

	public enum ImportType {

		DELTA("Delta"), SNAPSHOT("Snapshot"), FULL("Full");

		private String filenamePart;

		ImportType(String filenamePart) {
			this.filenamePart = filenamePart;
		}

		public String getFilenamePart() {
			return filenamePart;
		}
	}

	static final class ImportRun {

		private ComponentFactory runComponentFactory;

		private final ExecutorService executorService;
		private final List<Exception> loadingExceptions;

		private static final Pattern DATE_EXTRACT_PATTERN = Pattern.compile("[^\\t]*\\t([^\\t]*)\t.*");

		private ImportRun(ComponentFactory componentFactory) {
			executorService = Executors.newCachedThreadPool();
			this.runComponentFactory = componentFactory;
			loadingExceptions = new ArrayList<>();
		}

		private void doLoadReleaseFiles(String releaseDirPath, LoadingProfile loadingProfile, ImportType importType) throws ReleaseImportException {
			doLoadReleaseFiles(Collections.singletonList(releaseDirPath), loadingProfile, importType);
		}

		private void doLoadReleaseFiles(List<String> releaseDirPaths, LoadingProfile loadingProfile, ImportType importType) throws ReleaseImportException {
			// Configuration Validation
			if (loadingProfile.isSnapshotEffectiveComponentFilter() && importType != ImportType.SNAPSHOT) {
				throw new ReleaseImportException("Configuration error. SnapshotEffectiveComponentFilter can only be used when loading a Snapshot.");
			}

			// Input Validation
			ReleaseFiles releaseFiles;
			try {
				releaseFiles = findFiles(releaseDirPaths, importType.getFilenamePart(), loadingProfile);
			} catch (IOException e) {
				throw new ReleaseImportException("Failed to find release files during release import process.", e);
			}

			logger.info("Loading {} release files {}", importType, releaseFiles);

			try {

				// Add any component loading filters
				if (!loadingProfile.getModuleIds().isEmpty()) {
					this.runComponentFactory = addModuleIdFilter(runComponentFactory, loadingProfile.getModuleIds());
				}
				if (loadingProfile.isSnapshotEffectiveComponentFilter()) {
					this.runComponentFactory = addEffectiveComponentFilter(runComponentFactory, releaseFiles, loadingProfile);
				}

				runComponentFactory.loadingComponentsStarting();

				if (importType == ImportType.FULL) {
					final Set<String> releaseVersions = gatherVersions(releaseFiles);
					for (String releaseVersion : releaseVersions) {
						((HistoryAwareComponentFactory) runComponentFactory).loadingReleaseDeltaStarting(releaseVersion);
						logger.info("Loading release delta {}", releaseVersion);
						loadAll(loadingProfile, releaseFiles, releaseVersion, runComponentFactory, true);
						((HistoryAwareComponentFactory) runComponentFactory).loadingReleaseDeltaFinished(releaseVersion);
					}
				} else {
					loadAll(loadingProfile, releaseFiles, null, runComponentFactory, true);
				}
				if (!loadingExceptions.isEmpty()) {
					Exception firstException = loadingExceptions.get(0);
					throw new ReleaseImportException(String.format("Failed to load release files during release import process. " +
							"%s exceptions caught in threads. First exception: %s", loadingExceptions.size(), firstException.getMessage()), firstException);
				}

				runComponentFactory.loadingComponentsCompleted();

				logger.info("Release files read. JVM total memory is approx {} MB.", formatAsMB(Runtime.getRuntime().totalMemory()));
			} catch (IOException | InterruptedException e) {
				throw new ReleaseImportException("Failed to load release files during release import process.", e);
			}
			executorService.shutdown();
		}

		private ComponentFactory addModuleIdFilter(ComponentFactory runComponentFactory, Set<String> moduleIds) {
			return new ModuleFilter(runComponentFactory, moduleIds);
		}

		private ComponentFactory addEffectiveComponentFilter(ComponentFactory runComponentFactory, ReleaseFiles releaseFiles, LoadingProfile loadingProfile) throws IOException, InterruptedException, ReleaseImportException {
			logger.info("Gathering effective dates for effective component filtering.");
			LatestEffectiveDateComponentFactory latestEffectiveDateComponentFactory = new LatestEffectiveDateComponentFactory();
			runComponentFactory.preprocessingContent();

			// Multi-threading disabled to avoid the need to synchronize LatestEffectiveDateComponentFactory.
			boolean multiThreaded = false;

			// Force loading inactive rows during this phase so we know if the latest state is inactive
			LoadingProfile effectiveComponentLoadingProfile = loadingProfile
					.withInactiveComponents()
					.withInactiveRefsetMembers();

			loadAll(effectiveComponentLoadingProfile, releaseFiles, null, latestEffectiveDateComponentFactory, multiThreaded);

			// Wrap component factory to only let effective components through
			return new LatestEffectiveDateFilter(this.runComponentFactory, latestEffectiveDateComponentFactory);
		}

		private void loadAll(LoadingProfile loadingProfile, ReleaseFiles releaseFiles, String releaseVersion, ComponentFactory componentFactory, boolean multiThreaded) throws IOException, InterruptedException, ReleaseImportException {
			List<Callable<String>> coreComponentTasks = new ArrayList<>();
			if (!loadingProfile.isJustRefsets()) {
				loadConcepts(releaseFiles.getConceptPaths(), loadingProfile, releaseVersion, componentFactory);

				coreComponentTasks.add(loadRelationships(releaseFiles.getRelationshipPaths(), loadingProfile, releaseVersion, componentFactory));
				coreComponentTasks.add(loadConcreteRelationships(releaseFiles.getConcreteRelationshipPaths(), loadingProfile, releaseVersion, componentFactory));
				if (loadingProfile.isStatedRelationships()) {
					if (!releaseFiles.getStatedRelationshipPaths().isEmpty()) {
						coreComponentTasks.add(loadRelationships(releaseFiles.getStatedRelationshipPaths(), loadingProfile, releaseVersion, componentFactory));
					}
				}
				
				if (loadingProfile.isDescriptions() || loadingProfile.isFullDescriptionObjects()) {
					coreComponentTasks.add(loadDescriptions(releaseFiles.getDescriptionPaths(), loadingProfile, releaseVersion, componentFactory));
					if (!releaseFiles.getTextDefinitionPaths().isEmpty()) {
						coreComponentTasks.add(loadDescriptions(releaseFiles.getTextDefinitionPaths(), loadingProfile, releaseVersion, componentFactory));
					}
				}
			}

			List<Callable<String>> refsetTasks = new ArrayList<>();
			Set<String> includedReferenceSetFilenamePatterns = loadingProfile.getIncludedReferenceSetFilenamePatterns();
			if (loadingProfile.isAllRefsets() || !loadingProfile.getRefsetIds().isEmpty() || !includedReferenceSetFilenamePatterns.isEmpty()) {
				logger.info("includedReferenceSetPathPatterns: {}", includedReferenceSetFilenamePatterns);
				final List<Path> refsetSnapshots = releaseFiles.getRefsetPaths();
				Set<String> filenamesMatchedByPattern = new HashSet<>();
				for (Path refsetSnapshot : refsetSnapshots) {
					if (includedReferenceSetFilenamePatterns.isEmpty()) {
						refsetTasks.add(loadRefsets(refsetSnapshot, loadingProfile, releaseVersion, componentFactory, false));
					} else {
						boolean patternMatch = false;
						String filename = refsetSnapshot.getFileName().toString();
						for (String pattern : includedReferenceSetFilenamePatterns) {
							if (filename.matches(pattern)) {
								logger.info("refset '{}' matches pattern '{}'", filename, pattern);
								refsetTasks.add(loadRefsets(refsetSnapshot, loadingProfile, releaseVersion, componentFactory, true));
								filenamesMatchedByPattern.add(filename);
								patternMatch = true;
								break;
							}
						}
						if (!patternMatch) {
							logger.debug("refset '{}' does not match any patterns", filename);
						}
					}
				}
				if (includedReferenceSetFilenamePatterns.size() > filenamesMatchedByPattern.size()) {
					logger.warn("{} reference set filename patterns provided but only {} file matches found. Patterns: {}, Matches: {}",
							includedReferenceSetFilenamePatterns.size(), filenamesMatchedByPattern.size(),
							includedReferenceSetFilenamePatterns, filenamesMatchedByPattern);
				}
			}

			if (multiThreaded) {
				executorService.invokeAll(coreComponentTasks);
				executorService.invokeAll(refsetTasks);
			} else {
				try {
					for (Callable<String> coreComponentTask : coreComponentTasks) {
						coreComponentTask.call();
					}
					for (Callable<String> refsetTask : refsetTasks) {
						refsetTask.call();
					}
				} catch (Exception e) {
					logger.error("Snomed content loading task failed.", e);
				}
			}
		}

		private ReleaseFiles findFiles(List<String> releaseDirPaths, final String fileType, LoadingProfile loadingProfile) throws IOException {
			final ReleaseFiles releaseFiles = new ReleaseFiles();
			
			for (String releaseDirPath : releaseDirPaths) {
				final File releaseDir = new File(releaseDirPath);
				if (!releaseDir.isDirectory()) {
					throw new FileNotFoundException("Could not find release directory '" + releaseDirPath + "'");
				}

				Files.walkFileTree(releaseDir.toPath(), EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
						collectReleaseFile(file, fileType, releaseFiles);
						return FileVisitResult.CONTINUE;
					}
				});
			}

			releaseFiles.assertFullSet(loadingProfile, "looking recursively in: " + String.join(", ", releaseDirPaths));

			return releaseFiles;
		}

		static void collectReleaseFile(Path file, String fileType, ReleaseFiles releaseFiles) {
			final String fileName = file.getFileName().toString();
			if (fileName.endsWith(".txt")) {
				if (fileName.matches("x?(sct|rel)2_Concept_[^_]*" + fileType + "_.*")) {
					releaseFiles.addConceptPath(file);
				} else if (fileName.matches("x?(sct|rel)2_Description_[^_]*" + fileType + "(-[a-zA-Z\\-]*)?_.*")) {
					releaseFiles.addDescriptionPath(file);
				} else if (fileName.matches("x?(sct|rel)2_TextDefinition_[^_]*" + fileType + "(-[a-zA-Z\\-]*)?_.*")) {
					releaseFiles.addTextDefinitionPath(file);
				} else if (fileName.matches("x?(sct|rel)2_Relationship_[^_]*" + fileType + "_.*")) {
					releaseFiles.addRelationshipPath(file);
				} else if (fileName.matches("x?(sct|rel)2_RelationshipConcreteValues_[^_]*" + fileType + "_.*")) {
					releaseFiles.addConcreteRelationshipPath(file);
				} else if (fileName.matches("x?(sct|rel)2_StatedRelationship_[^_]*" + fileType + "_.*")) {
					releaseFiles.addStatedRelationshipPath(file);
				} else if (fileName.matches("x?(sct|rel)2_sRefset_OWL.*[^_]*" + fileType + "_.*")) {
					releaseFiles.addRefsetPath(file);
				} else if (fileName.matches("x?(der|rel)2_[sci]*Refset_[^_]*" + fileType + "(-[a-zA-Z\\-]*)?_.*")) {
					releaseFiles.addRefsetPath(file);
				} else if (fileName.matches("x?der2_.*") || fileName.matches("x?(sct|rel)2_.*")) {
					logger.info("RF2 release filename not recognised '{}'. This file will not be loaded.", fileName);
				}
			}
		}

		private void loadConcepts(List<Path> rf2Files, final LoadingProfile loadingProfile, final String releaseVersion, ComponentFactory componentFactory) throws IOException, ReleaseImportException {
			readLines(rf2Files, (ValuesHandler) values -> {
				if (loadingProfile.isInactiveConcepts() || "1".equals(values[ConceptFieldIndexes.active])) {
					String conceptId = values[ComponentFieldIndexes.id];
					componentFactory.newConceptState(conceptId, values[ConceptFieldIndexes.effectiveTime], values[ConceptFieldIndexes.active],
							values[ConceptFieldIndexes.moduleId], values[ConceptFieldIndexes.definitionStatusId]);
				}
			}, "concepts", releaseVersion);
		}

		private Callable<String> loadRelationships(List<Path> rf2Files, final LoadingProfile loadingProfile, String releaseVersion, ComponentFactory componentFactory) {
			return readLinesCallable(rf2Files, (ValuesHandler) values -> {
				final boolean active = "1".equals(values[RelationshipFieldIndexes.active]);
				if (loadingProfile.isInactiveRelationships() || active) {
					final String sourceId = values[RelationshipFieldIndexes.sourceId];
					final String type = values[RelationshipFieldIndexes.typeId];
					final String characteristicType = values[RelationshipFieldIndexes.characteristicTypeId];
					final String destinationId = values[RelationshipFieldIndexes.destinationId];
					boolean inferred = ConceptConstants.INFERRED_RELATIONSHIP.equals(characteristicType);
					if (!inferred && loadingProfile.isStatedAttributeMapOnConcept()) {
						componentFactory.addStatedConceptAttribute(sourceId, type, destinationId);
					} else if (inferred && loadingProfile.isInferredAttributeMapOnConcept()) {
						componentFactory.addInferredConceptAttribute(sourceId, type, destinationId);
					}
					if (inferred || loadingProfile.isStatedRelationships()) {
						if (type.equals(ConceptConstants.isA)) {
							if (active) {
								if (inferred) {
									componentFactory.addInferredConceptParent(sourceId, destinationId);
									componentFactory.addInferredConceptChild(sourceId, destinationId);
								} else {
									componentFactory.addStatedConceptParent(sourceId, destinationId);
									componentFactory.addStatedConceptChild(sourceId, destinationId);
								}
							} else {
								if (inferred) {
									componentFactory.removeInferredConceptParent(sourceId, destinationId);
									componentFactory.removeInferredConceptChild(sourceId, destinationId);
								} else {
									componentFactory.removeStatedConceptParent(sourceId, destinationId);
									componentFactory.removeStatedConceptChild(sourceId, destinationId);
								}
							}
						}

						if (loadingProfile.isFullRelationshipObjects()) {
							componentFactory.newRelationshipState(
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
			}, "relationships", releaseVersion);
		}

		private Callable<String> loadConcreteRelationships(List<Path> rf2Files, final LoadingProfile loadingProfile, String releaseVersion, ComponentFactory componentFactory) {
			return readLinesCallable(rf2Files, (ValuesHandler) values -> {
				final boolean active = "1".equals(values[ConcreteRelationshipFieldIndexes.active]);
				if (loadingProfile.isInactiveRelationships() || active) {
					if (loadingProfile.isInferredAttributeMapOnConcept()) {
						final String sourceId = values[ConcreteRelationshipFieldIndexes.sourceId];
						final String type = values[ConcreteRelationshipFieldIndexes.typeId];
						final String value = values[ConcreteRelationshipFieldIndexes.value];
						componentFactory.addInferredConceptConcreteAttribute(sourceId, type, value);
					}
					if (loadingProfile.isFullConcreteRelationshipObjects()) {
						componentFactory.newConcreteRelationshipState(
								values[ConcreteRelationshipFieldIndexes.id],
								values[ConcreteRelationshipFieldIndexes.effectiveTime],
								values[ConcreteRelationshipFieldIndexes.active],
								values[ConcreteRelationshipFieldIndexes.moduleId],
								values[ConcreteRelationshipFieldIndexes.sourceId],
								values[ConcreteRelationshipFieldIndexes.value],
								values[ConcreteRelationshipFieldIndexes.relationshipGroup],
								values[ConcreteRelationshipFieldIndexes.typeId],
								values[ConcreteRelationshipFieldIndexes.characteristicTypeId],
								values[ConcreteRelationshipFieldIndexes.modifierId]
						);
					}
				}
			}, "concrete relationships", releaseVersion);
		}

		private Callable<String> loadDescriptions(List<Path> rf2File, final LoadingProfile loadingProfile, String releaseVersion, ComponentFactory componentFactory) {
			return readLinesCallable(rf2File, (ValuesHandler) values -> {
				if (loadingProfile.isInactiveDescriptions() || "1".equals(values[DescriptionFieldIndexes.active])) {
					final String conceptId = values[DescriptionFieldIndexes.conceptId];
					final String value = values[DescriptionFieldIndexes.typeId];
					if (ConceptConstants.FSN.equals(value)) {
						componentFactory.addConceptFSN(conceptId, values[DescriptionFieldIndexes.term]);
					}
					if (loadingProfile.isFullDescriptionObjects()) {
						componentFactory.newDescriptionState(
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
			}, "descriptions", releaseVersion);
		}

		private Callable<String> loadRefsets(Path rf2File, final LoadingProfile loadingProfile, String releaseVersion,
				ComponentFactory componentFactory, boolean filenamePatternMatch) {

			return readLinesCallable(Collections.singletonList(rf2File), (FieldNamesAndValuesHandler) (fieldNames, values) -> {
				if (loadingProfile.isInactiveRefsetMembers() || "1".equals(values[RefsetFieldIndexes.active])) {
					final String refsetId = values[RefsetFieldIndexes.refsetId];
					if (loadingProfile.isAllRefsets() || filenamePatternMatch || loadingProfile.isRefset(refsetId)) {
						final String referencedComponentId = values[RefsetFieldIndexes.referencedComponentId];
						if (FactoryUtils.isConceptId(referencedComponentId)) {
							componentFactory.addConceptReferencedInRefsetId(refsetId, referencedComponentId);
						}
						if (loadingProfile.isFullRefsetMemberObjects()) {
							componentFactory.newReferenceSetMemberState(
									fieldNames,
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
			}, "reference set members", releaseVersion);
		}

		private Callable<String> readLinesCallable(final List<Path> rf2FilePaths, final FileContentHandler contentHandler, final String componentType, final String releaseVersion) {
			return () -> {
				try {
					readLines(rf2FilePaths, contentHandler, componentType, releaseVersion);
				} catch (Exception e) {
					logger.error("Failed to read or process lines.", e);
					loadingExceptions.add(e);
				}
				return null;
			};
		}

		private Set<String> gatherVersions(ReleaseFiles releaseFiles) throws IOException {
			logger.info("Gathering list of release versions...");
			Set<String> versions = new TreeSet<>();
			gatherVersions(releaseFiles.getConceptPaths(), versions);
			gatherVersions(releaseFiles.getDescriptionPaths(), versions);
			gatherVersions(releaseFiles.getTextDefinitionPaths(), versions);
			gatherVersions(releaseFiles.getRelationshipPaths(), versions);
			gatherVersions(releaseFiles.getRefsetPaths(), versions);
			logger.info("Release versions found: {}", versions);
			return versions;
		}

		private void gatherVersions(List<Path> filePaths, Set<String> versions) throws IOException {
			for (Path filePath : filePaths) {
				try (final BufferedReader reader = Files.newBufferedReader(filePath, UTF_8)) {
					String line;
					reader.readLine(); // discard header line
					Matcher matcher;
					while ((line = reader.readLine()) != null) {
						matcher = DATE_EXTRACT_PATTERN.matcher(line);
						if (matcher.matches()) {
							versions.add(matcher.group(1));
						}
					}
				}
			}
		}

		private void readLines(List<Path> rf2FilePaths, FileContentHandler contentHandler, String componentType, String releaseVersion) throws IOException, ReleaseImportException {
			if (releaseVersion != null) {
				logger.info("Reading {} for release {}", componentType, releaseVersion);
			} else {
				logger.info("Reading {} ", componentType);
			}

			final ValuesHandler valuesHandler = contentHandler instanceof ValuesHandler ? ((ValuesHandler) contentHandler) : null;
			final FieldNamesAndValuesHandler fieldNamesAndValuesHandler = contentHandler instanceof FieldNamesAndValuesHandler ? ((FieldNamesAndValuesHandler) contentHandler) : null;

			for (Path rf2FilePath : rf2FilePaths) {
				long linesRead = 0L;
				Path fileName = rf2FilePath.getFileName();
				try (final BufferedReader reader = Files.newBufferedReader(rf2FilePath, UTF_8)) {
					String line;
					final String header = reader.readLine();
					final String[] fieldNames = header.split("\\t");
					final int columns = fieldNames.length;
					// Allow refsets to have empty values in last columns. "line.split" does not return the empty values so we need a minColumns.
					final Integer minColumns = fileName.toString().contains("Refset_") ? 6 : null;
					if (columns < 5) {
						throw new ReleaseImportException(format("Invalid RF2 content. Less than five tab separated columns found in first line of %s.", fileName));
					}
					if (!fieldNames[0].equals("id")) {
						throw new ReleaseImportException(format("Invalid RF2 content. 'id' not found as first value in tab separated first line of %s.", fileName));
					}
					String[] values;
					while ((line = reader.readLine()) != null) {
						linesRead++;
						if (line.isEmpty()) {
							logger.info("Skipping empty line {} in RF2 file {}.", linesRead + 1, fileName);
							continue;
						}
						values = line.split("\\t");
						if (values.length != columns) {
							if (minColumns == null) {
								throw new ReleaseImportException(format("Invalid RF2 content. Wrong number of columns in line %s of file %s. Expected %s columns, found %s.",
										linesRead + 1, fileName, columns, values.length));
							} else if (values.length < minColumns) {
								throw new ReleaseImportException(format("Invalid RF2 content. Less than minimum number of columns in line %s of file %s. Expected at least %s columns, found %s.",
										linesRead + 1, fileName, minColumns, values.length));
							} else {
								logger.warn(format("Wrong number of columns in line %s of file %s. Expected exactly %s columns, found %s. " +
												"This could be caused by empty values in last columns of the refset member, will attempt to load.",
										linesRead + 1, fileName, columns, values.length));
							}
						}
						if (releaseVersion == null || releaseVersion.equals(values[ComponentFieldIndexes.effectiveTime])) {
							if (valuesHandler != null) {
								valuesHandler.handle(values);
							} else if (fieldNamesAndValuesHandler != null) {
								fieldNamesAndValuesHandler.handle(fieldNames, values);
							}
						}
					}
				}
				logger.info("{} {} read from {}", linesRead - 1, componentType, fileName.toString());
			}
		}

		private String formatAsMB(long bytes) {
			return NumberFormat.getInstance().format((bytes / 1024) / 1024);
		}

		private interface FileContentHandler {
		}

		private interface ValuesHandler extends FileContentHandler {
			void handle(String[] values);
		}

		private interface FieldNamesAndValuesHandler extends FileContentHandler {
			void handle(String[] fieldNames, String[] values);
		}
	}

}

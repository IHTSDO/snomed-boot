package org.ihtsdo.otf.snomedboot;

import org.ihtsdo.otf.snomedboot.domain.ConceptConstants;
import org.ihtsdo.otf.snomedboot.domain.rf2.*;
import org.ihtsdo.otf.snomedboot.factory.ComponentFactory;
import org.ihtsdo.otf.snomedboot.factory.FactoryUtils;
import org.ihtsdo.otf.snomedboot.factory.HistoryAwareComponentFactory;
import org.ihtsdo.otf.snomedboot.factory.LoadingProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StreamUtils;

import java.io.*;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ReleaseImporter {

	public static final Charset UTF_8 = Charset.forName("UTF-8");

	public void loadFullReleaseFiles(String releaseDirPath, LoadingProfile loadingProfile, HistoryAwareComponentFactory componentFactory) throws ReleaseImportException {
		new ImportRun(componentFactory).doLoadReleaseFiles(releaseDirPath, loadingProfile, ImportType.FULL);
	}

	public void loadSnapshotReleaseFiles(String releaseDirPath, LoadingProfile loadingProfile, ComponentFactory componentFactory) throws ReleaseImportException {
		new ImportRun(componentFactory).doLoadReleaseFiles(releaseDirPath, loadingProfile, ImportType.SNAPSHOT);
	}

	public void loadDeltaReleaseFiles(String releaseDirPath, LoadingProfile loadingProfile, ComponentFactory componentFactory) throws ReleaseImportException {
		new ImportRun(componentFactory).doLoadReleaseFiles(releaseDirPath, loadingProfile, ImportType.DELTA);
	}

	public void loadFullReleaseFiles(InputStream releaseZip, LoadingProfile loadingProfile, HistoryAwareComponentFactory componentFactory) throws ReleaseImportException {
		File releaseDir = unzipRelease(releaseZip, ImportType.FULL);
		loadFullReleaseFiles(releaseDir.getAbsolutePath(), loadingProfile, componentFactory);
		FileSystemUtils.deleteRecursively(releaseDir);
	}

	public void loadSnapshotReleaseFiles(InputStream releaseZip, LoadingProfile loadingProfile, ComponentFactory componentFactory) throws ReleaseImportException {
		File releaseDir = unzipRelease(releaseZip, ImportType.SNAPSHOT);
		loadSnapshotReleaseFiles(releaseDir.getAbsolutePath(), loadingProfile, componentFactory);
		FileSystemUtils.deleteRecursively(releaseDir);
	}

	public void loadDeltaReleaseFiles(InputStream releaseZip, LoadingProfile loadingProfile, ComponentFactory componentFactory) throws ReleaseImportException {
		File releaseDir = unzipRelease(releaseZip, ImportType.DELTA);
		loadDeltaReleaseFiles(releaseDir.getAbsolutePath(), loadingProfile, componentFactory);
		FileSystemUtils.deleteRecursively(releaseDir);
	}

	private File unzipRelease(InputStream releaseZip, ImportType filenameFilter) throws ReleaseImportException {
		try {
			File tempDir = Files.createTempDirectory(null).toFile();
			try (InputStream snomedReleaseZipStream = releaseZip) {
				File zipFile = new File(tempDir, "release.zip");
				try (FileOutputStream out = new FileOutputStream(zipFile)) {
					StreamUtils.copy(snomedReleaseZipStream, out);
				}

				ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
				ZipEntry zipEntry;
				while ((zipEntry = zipInputStream.getNextEntry()) != null) {
					String zipEntryName = zipEntry.getName();
					if (zipEntryName.contains(filenameFilter.getFilenamePart())) {
						// Create file without directory nesting
						File file = new File(tempDir, new File(zipEntryName).getName());
						file.createNewFile();
						try (FileOutputStream entryOutputStream = new FileOutputStream(file)) {
							StreamUtils.copy(zipInputStream, entryOutputStream);
						}
					}
				}
			}
			return tempDir;
		} catch (IOException e) {
			throw new ReleaseImportException("Filed to unzip snomed release file.", e);
		}
	}

	private enum ImportType {

		DELTA("Delta"), SNAPSHOT("Snapshot"), FULL("Full");

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
				releaseFiles = findFiles(releaseDirPath, importType.getFilenamePart(), loadingProfile);
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
			List<Callable<String>> coreComponentTasks = new ArrayList<>();
			if (!loadingProfile.isJustRefsets()) {
				loadConcepts(releaseFiles.getConceptPath(), loadingProfile, releaseVersion);

				coreComponentTasks.add(loadRelationships(releaseFiles.getRelationshipPath(), loadingProfile, releaseVersion));
				if (loadingProfile.isStatedRelationships()) {
					coreComponentTasks.add(loadRelationships(releaseFiles.getStatedRelationshipPath(), loadingProfile, releaseVersion));
				}
				coreComponentTasks.add(loadDescriptions(releaseFiles.getDescriptionPath(), loadingProfile, releaseVersion));
				coreComponentTasks.add(loadDescriptions(releaseFiles.getTextDefinitionPath(), loadingProfile, releaseVersion));
			}

			List<Callable<String>> refsetTasks = new ArrayList<>();
			if (loadingProfile.isAllRefsets() || !loadingProfile.getRefsetIds().isEmpty()) {
				Set<String> includedReferenceSetFilenamePatterns = loadingProfile.getIncludedReferenceSetFilenamePatterns();
				logger.info("includedReferenceSetPathPatterns: {}", includedReferenceSetFilenamePatterns);
				final List<Path> refsetSnapshots = releaseFiles.getRefsetPaths();
				for (Path refsetSnapshot : refsetSnapshots) {
					if (includedReferenceSetFilenamePatterns.isEmpty()) {
						refsetTasks.add(loadRefsets(refsetSnapshot, loadingProfile, releaseVersion));
					} else {
						for (String pattern : includedReferenceSetFilenamePatterns) {
							String filename = refsetSnapshot.getFileName().toString();
							if (filename.matches(pattern)) {
								logger.info("refset '{}' matches pattern '{}'", filename, pattern);
								refsetTasks.add(loadRefsets(refsetSnapshot, loadingProfile, releaseVersion));
								break;
							}
							logger.info("refset '{}' does not match any patterns", filename);
						}
					}
				}
			}

			executorService.invokeAll(coreComponentTasks);
			executorService.invokeAll(refsetTasks);
		}

		private ReleaseFiles findFiles(String releaseDirPath, final String fileType, LoadingProfile loadingProfile) throws IOException {
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
						if (fileName.startsWith("sct2_Concept_" + fileType) || fileName.startsWith("xsct2_Concept_" + fileType)) {
							releaseFiles.setConceptPath(file);
						} else if (fileName.startsWith("sct2_Description_" + fileType) || fileName.startsWith("xsct2_Description_" + fileType)) {
							releaseFiles.setDescriptionPath(file);
						} else if (fileName.startsWith("sct2_TextDefinition_" + fileType) || fileName.startsWith("xsct2_TextDefinition_" + fileType)) {
							releaseFiles.setTextDefinitionPath(file);
						} else if (fileName.startsWith("sct2_Relationship_" + fileType) || fileName.startsWith("xsct2_Relationship_" + fileType)) {
							releaseFiles.setRelationshipPath(file);
						} else if (fileName.startsWith("sct2_StatedRelationship_" + fileType) || fileName.startsWith("xsct2_StatedRelationship_" + fileType)) {
							releaseFiles.setStatedRelationshipPath(file);
						} else if (fileName.startsWith("der2_") && fileName.contains(fileType) || fileName.startsWith("xder2_") && fileName.contains(fileType)) {
							releaseFiles.getRefsetPaths().add(file);
						}
					}
					return FileVisitResult.CONTINUE;
				}
			});

			if (!loadingProfile.isJustRefsets()) {
				releaseFiles.assertFullSet();
			}

			return releaseFiles;
		}

		private void loadConcepts(Path rf2File, final LoadingProfile loadingProfile, final String releaseVersion) throws IOException {
			readLines(rf2File, new ValuesHandler() {
				@Override
				public void handle(String[] values) {
					if (loadingProfile.isInactiveConcepts() || "1".equals(values[ConceptFieldIndexes.active])) {
						String conceptId = values[ComponentFieldIndexes.id];
						componentFactory.newConceptState(conceptId, values[ConceptFieldIndexes.effectiveTime], values[ConceptFieldIndexes.active],
								values[ConceptFieldIndexes.moduleId], values[ConceptFieldIndexes.definitionStatusId]);
					}
				}
			}, "concepts", releaseVersion);
		}

		private Callable<String> loadRelationships(Path rf2File, final LoadingProfile loadingProfile, String releaseVersion) {
			return readLinesCallable(rf2File, new ValuesHandler() {
				@Override
				public void handle(String[] values) {
					final boolean active = "1".equals(values[RelationshipFieldIndexes.active]);
					if (loadingProfile.isInactiveRelationships() || active) {
						final String sourceId = values[RelationshipFieldIndexes.sourceId];
						final String type = values[RelationshipFieldIndexes.typeId];
						final String characteristicType = values[RelationshipFieldIndexes.characteristicTypeId];
						final String value = values[RelationshipFieldIndexes.destinationId];
						boolean inferred = ConceptConstants.INFERRED_RELATIONSHIP.equals(characteristicType);
						if (!inferred && loadingProfile.isStatedAttributeMapOnConcept()) {
							componentFactory.addStatedConceptAttribute(sourceId, type, value);
						} else if (inferred && loadingProfile.isInferredAttributeMapOnConcept()) {
							componentFactory.addInferredConceptAttribute(sourceId, type, value);
						}
						if (inferred || loadingProfile.isStatedRelationships()) {
							if (type.equals(ConceptConstants.isA)) {
								if (active) {
									if (inferred) {
										componentFactory.addInferredConceptParent(sourceId, value);
									} else {
										componentFactory.addStatedConceptParent(sourceId, value);
									}
								} else {
									if (inferred) {
										componentFactory.removeInferredConceptParent(sourceId, value);
									} else {
										componentFactory.removeStatedConceptParent(sourceId, value);
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
				}
			}, "descriptions", releaseVersion);
		}

		private Callable<String> loadRefsets(Path rf2File, final LoadingProfile loadingProfile, String releaseVersion) {
			return readLinesCallable(rf2File, new FieldNamesAndValuesHandler() {
				@Override
				public void handle(String[] fieldNames, String[] values) {
					if (loadingProfile.isInactiveRefsetMembers() || "1".equals(values[RefsetFieldIndexes.active])) {
						final String refsetId = values[RefsetFieldIndexes.refsetId];
						if (loadingProfile.isAllRefsets() || loadingProfile.isRefset(refsetId)) {
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
				}
			}, "reference set members", releaseVersion);
		}

		private Callable<String> readLinesCallable(final Path rf2FilePath, final FileContentHandler contentHandler, final String componentType, final String releaseVersion) {
			return new Callable<String>() {
				@Override
				public String call() throws Exception {
					try {
						readLines(rf2FilePath, contentHandler, componentType, releaseVersion);
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
			if (filePath != null) {
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
		}

		private void readLines(Path rf2FilePath, FileContentHandler contentHandler, String componentType, String releaseVersion) throws IOException {
			if (releaseVersion != null) {
				logger.info("Reading {} for release {}", componentType, releaseVersion);
			} else {
				logger.info("Reading {} ", componentType);
			}
			long linesRead = 0L;

			final ValuesHandler valuesHandler = contentHandler instanceof ValuesHandler ? ((ValuesHandler) contentHandler) : null;
			final FieldNamesAndValuesHandler fieldNamesAndValuesHandler = contentHandler instanceof FieldNamesAndValuesHandler ? ((FieldNamesAndValuesHandler) contentHandler) : null;

			try (final BufferedReader reader = Files.newBufferedReader(rf2FilePath, UTF_8)) {
				String line;
				final String header = reader.readLine();
				final String[] fieldNames = header.split("\\t");
				String[] values;
				while ((line = reader.readLine()) != null) {
					linesRead++;
					values = line.split("\\t");
					if (releaseVersion == null || releaseVersion.equals(values[ComponentFieldIndexes.effectiveTime])) {
						if (valuesHandler != null) {
							valuesHandler.handle(values);
						} else if (fieldNamesAndValuesHandler != null) {
							fieldNamesAndValuesHandler.handle(fieldNames, values);
						}
					}
				}
			}
			logger.info("{} {} read from {}", linesRead, componentType, rf2FilePath.getFileName().toString());
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

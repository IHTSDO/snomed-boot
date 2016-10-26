# Snomed Boot
A Java framework for loading SNOMED CT components and reference set members from RF2 into memory, a database (or anything) via factory methods.

## Key Features
- Highly extensible
- Loading Profiles - only get the components or refsets you are interested in.
- Multithreaded - concepts load first, then relationships and descriptions in parallel, then all reference set memebers in parallel.

## Component Factories
This project is oriented around the ComponentFactory and HistoryAwareComponentFactory. These interfaces allow a factory implementation to recieve the properties of every component and member. The HistoryAwareComponentFactory is useful when loading full files containing more than one release.

### Memory Factory Implementation
The default factory implementation targets memory. It builds a map of concepts with their descriptions and relationships connected. This is an extremely fast way to get hold the transitive closure for every concept.
```java
// Create release importer
ReleaseImporter releaseImporter = new ReleaseImporter();

// Load SNOMED CT components into memory
ComponentStore componentStore = new ComponentStore();
releaseImporter.loadSnapshotReleaseFiles("release/SnomedCT_RF2Release_INT_20160731", LoadingProfile.light, new ComponentFactoryImpl(componentStore));
Map<Long, ? extends Concept> conceptMap = componentStore.getConcepts();

// Get tranitive closure for concept 285355007 | Blood blister (disorder) |
Set<Long> transitiveClosure = conceptMap.get("285355007").getAncestorIds();
```

## Contribute
Feel free to fork and improve this project.

If you create a factory implementation based on this project please name the repo snomed-boot-X and share it here. e.g. snomed-boot-mysql.

# Snomed Boot
[![Build Status](https://travis-ci.org/IHTSDO/snomed-boot.svg?branch=master)](https://travis-ci.org/IHTSDO/snomed-boot) [![codecov](https://codecov.io/gh/IHTSDO/snomed-boot/branch/master/graph/badge.svg)](https://codecov.io/gh/IHTSDO/snomed-boot)

An extensible Java framework for loading SNOMED CT RF2 content into any implementation.

## Key Features
- Extensible factory based design
- Supports component streaming for better overall performance and less pressure on memory
- Ability to load only the latest version of each component when using a collection of snapshot archives
- Multithreaded
  - Concepts load first
  - Then Relationships and Descriptions load in parallel
  - Then all reference set memebers in parallel.
- Loading Profiles allow content to be filtered
  - by content type 
  - by reference set filename pattern
  - by reference set identifier 

## Component Factories
This project is oriented around the ComponentFactory and HistoryAwareComponentFactory. These interfaces allow a factory implementation to recieve the properties of every component and member. The HistoryAwareComponentFactory is useful when loading full files containing more than one release.

## In-Memory Implementation
The default factory implementation loads content into memory. It builds a map of concepts with their descriptions and relationships connected. This is an extremely fast way to get hold the transitive closure for every concept.
```java
// Create release importer
ReleaseImporter releaseImporter = new ReleaseImporter();

// Load SNOMED CT components into memory
ComponentStore componentStore = new ComponentStore();
releaseImporter.loadSnapshotReleaseFiles("release/SnomedCT_RF2Release_INT_20170131", LoadingProfile.light, new ComponentFactoryImpl(componentStore));
Map<Long, ? extends Concept> conceptMap = componentStore.getConcepts();

// Get transitive closure for concept 285355007 | Blood blister (disorder) |
Set<Long> transitiveClosure = conceptMap.get("285355007").getInferredAncestorIds();
```

## Contribute
We welcome your suggestions and contributions. Feel free to fork this project and submit a pull request of your changes.

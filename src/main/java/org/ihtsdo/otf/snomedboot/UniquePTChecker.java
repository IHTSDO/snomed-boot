package org.ihtsdo.otf.snomedboot;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.lang.Long.parseLong;

public class UniquePTChecker {

	public static final String US_EN_LANG_REFSET = "900000000000509007";
	public static final String PREFERRED = "900000000000548007";
	public static final String FSN = "900000000000003001";

	public static void main(String[] args) throws ReleaseImportException, FileNotFoundException {
		String effectiveTimeFilter = "20180731";
		String dir = "/Users/kai/Downloads/dailybuild-20180508/";
		String conceptFile = dir + "sct2_Concept_Snapshot_INT_20180731.txt";
		String langRefsetFile = dir + "der2_cRefset_LanguageSnapshot-en_INT_20180731.txt";
		String descriptionFile = dir + "sct2_Description_Snapshot-en_INT_20180731.txt";

		Set<Long> activeConcepts = getActiveConcepts(conceptFile);
		Map<Long, String> conceptToTag = getConceptToTagMap(descriptionFile, activeConcepts);
		Set<Long> preferredDescriptions = getPreferredDescriptionIds(langRefsetFile);

		System.out.println("Tag\tTerm\tPT ID\tConcept");
		int total = 0;
		Map<String, Set<String>> tagsToPtSet = new HashMap<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(descriptionFile))) {
			String line;
			String[] split;
			while ((line = reader.readLine()) != null) {
				// id	effectiveTime	active	moduleId	conceptId	languageCode	typeId	term	caseSignificanceId
				// 0	1				2		3			4			5				6		7		8
				split = line.split("\\t");
				if ("1".equals(split[2])) {
					// active
					if (activeConcepts.contains(parseLong(split[4])) // concept active
							&& effectiveTimeFilter.equals(split[1]) // effectiveDate
							&& !FSN.equals(split[6]) // Syn
							&& preferredDescriptions.contains(parseLong(split[0])) // US Preferred
							) {
						// Syn
						String term = split[7];
						long conceptId = parseLong(split[4]);
						String tag = conceptToTag.get(conceptId);

						Set<String> tagPtSet = tagsToPtSet.computeIfAbsent(tag, s -> new HashSet<>());
						if (!tagPtSet.add(term)) {
							System.out.println(tag + "\t" + term + "\t" + split[0] + "\t" + conceptId);
						}
					}
				}
			}
			System.out.println("Total: " + total);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Set<Long> getActiveConcepts(String conceptFile) {
		Set<Long> activeConcepts = new LongOpenHashSet();
		try (BufferedReader reader = new BufferedReader(new FileReader(conceptFile))) {
			String line;
			String[] split;
			while ((line = reader.readLine()) != null) {
				// id	effectiveTime	active	moduleId	definitionStatusId
				// 0	1				2		3			4
				split = line.split("\\t");
				if ("1".equals(split[2])) {
					// active
					activeConcepts.add(parseLong(split[0]));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return activeConcepts;
	}

	private static Map<Long, String> getConceptToTagMap(String descriptionFile, Set<Long> activeConcepts) {
		Map<Long, String> conceptToTag = new HashMap<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(descriptionFile))) {
			String line;
			String[] split;
			while ((line = reader.readLine()) != null) {
				// id	effectiveTime	active	moduleId	conceptId	languageCode	typeId	term	caseSignificanceId
				// 0	1				2		3			4			5				6		7		8
				split = line.split("\\t");
				if ("1".equals(split[2])) {
					// active
					if (activeConcepts.contains(parseLong(split[4])) &&
							FSN.equals(split[6])) {
						// FSN of active concept
						String term = split[7];
						conceptToTag.put(parseLong(split[4]), getTag(term));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return conceptToTag;
	}

	private static Map<Long, Set<String>> getConceptToTermsMap(String descriptionFile, Set<Long> activeConcepts) {
		Map<Long, Set<String>> conceptToTermsMap = new Long2ObjectLinkedOpenHashMap<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(descriptionFile))) {
			String line;
			String[] split;
			while ((line = reader.readLine()) != null) {
				// id	effectiveTime	active	moduleId	conceptId	languageCode	typeId	term	caseSignificanceId
				// 0	1				2		3			4			5				6		7		8
				split = line.split("\\t");
				if ("1".equals(split[2])) {
					// active
					if (activeConcepts.contains(parseLong(split[4]))) {
						// term of active concept
						String term = split[7];
						conceptToTermsMap.computeIfAbsent(parseLong(split[4]), conceptId -> new HashSet<>()).add(term);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return conceptToTermsMap;
	}

	private static Set<Long> getPreferredDescriptionIds(String langRefsetFile) {
		Set<Long> preferredDescriptions = new LongOpenHashSet();
		try (BufferedReader reader = new BufferedReader(new FileReader(langRefsetFile))) {
			String line;
			String[] split;
			while ((line = reader.readLine()) != null) {
				// id	effectiveTime	active	moduleId	refsetId	referencedComponentId	acceptabilityId
				// 0	1				2		3			4			5						6
				split = line.split("\\t");
				if ("1".equals(split[2]) && US_EN_LANG_REFSET.equals(split[4]) && PREFERRED.equals(split[6])) {
					// active
					preferredDescriptions.add(parseLong(split[5]));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return preferredDescriptions;
	}

	private static Map<Long, Set<Long>> getConceptToRelationshipTargetsMap(String relationshipFile, Set<Long> activeConcepts) {
		Map<Long, Set<Long>> conceptToRelationshipTargetsMap = new Long2ObjectLinkedOpenHashMap<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(relationshipFile))) {
			String line;
			String[] split;
			while ((line = reader.readLine()) != null) {
				// id	effectiveTime	active	moduleId	sourceId	destinationId	relationshipGroup	typeId	characteristicTypeId	modifierId
				// 0	1				2		3			4			5				6					7		8						9
				split = line.split("\\t");
				long conceptId = parseLong(split[4]);
				if ("1".equals(split[2]) && activeConcepts.contains(conceptId)) {
					conceptToRelationshipTargetsMap.computeIfAbsent(conceptId, aLong -> new HashSet<>()).add(parseLong(split[5]));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return conceptToRelationshipTargetsMap;
	}

	private static String getTag(String fsn) {
		int openIndex = fsn.lastIndexOf("(");
		int closeIndex = fsn.lastIndexOf(")");
		if (openIndex != -1 && openIndex < closeIndex) {
			return fsn.substring(openIndex + 1, closeIndex);
		}
		return "";
	}
}

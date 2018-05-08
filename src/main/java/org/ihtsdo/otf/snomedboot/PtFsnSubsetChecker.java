package org.ihtsdo.otf.snomedboot;

import com.google.common.collect.Sets;
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

/**
 * Quick ad-hoc report to check that:
 * For each new Snomed International Edition Fully Specified Name
 * - the Preferred Term on the same concept contains only the same words
 * - taking into account a list of acronyms and insignificant words
 * - if a word is not found we also check related concepts
 *   - if a related concept description matches we check that the related concept's FSN is contained within the validated concept's FSN
 */
public class PtFsnSubsetChecker {

	public static final String US_EN_LANG_REFSET = "900000000000509007";
	public static final String PREFERRED = "900000000000548007";
	public static final String FSN = "900000000000003001";

	public static void main(String[] args) throws ReleaseImportException, FileNotFoundException {
		String effectiveTimeFilter = "20180731";
		String dir = "/Users/kai/Downloads/dailybuild-20180508/";
		String conceptFile = dir + "sct2_Concept_Snapshot_INT_20180731.txt";
		String langRefsetFile = dir + "der2_cRefset_LanguageSnapshot-en_INT_20180731.txt";
		String descriptionFile = dir + "sct2_Description_Snapshot-en_INT_20180731.txt";
		String relationshipFile = dir + "sct2_Relationship_Snapshot_INT_20180731.txt";

		Set<Long> activeConcepts = getActiveConcepts(conceptFile);
		Map<Long, String> conceptToFSN = getConceptToFsn(descriptionFile, activeConcepts);
		Map<Long, Set<String>> conceptToTermsMap = getConceptToTermWordsMap(descriptionFile, activeConcepts);
		Map<Long, Set<Long>> conceptToRelationshipTargetsMap = getConceptToRelationshipTargetsMap(relationshipFile, activeConcepts);
		Set<Long> preferredDescriptions = getPreferredDescriptionIds(langRefsetFile);

		System.out.println("Not subset:\n");

		Set<String> insignificant = Sets.newHashSet(
				"with",
				"for",
				"as",
				"of",
				"and",
				"using",
				"containing precisely",
				"containing"
		);

		System.out.println(insignificant);

		Map<String, String> canConvert = new HashMap<>();
		canConvert.put("Ethinyl estradiol", "ethinylestradiol");
		canConvert.put("ultrasonography", "ultrasonographic");
		canConvert.put("fluoroscopy", "fluoroscopic");
		canConvert.put("guided", "guidance");
		canConvert.put("facial", "face");
		canConvert.put("ct", "computed tomography");
		canConvert.put("perineurioma", "perineurial");
		canConvert.put("umod", "Uromodulin");
		canConvert.put("complication", "disorder");
		canConvert.put("gouty", "gout");
		canConvert.put("renal", "kidney");
		canConvert.put("mri", "Magnetic resonance imaging");
		canConvert.put("muc1", "Mucin 1");
		canConvert.put("umod", "Uromodulin");
		canConvert.put("edqm", "European Directorate Quality of Medicines");
		canConvert.put("poss", "Palliative care Outcome Scale symptom");
		canConvert.put("pass", "Postural Assessment Scale for Stroke");
		canConvert.put("iciq", "International Consultation on Incontinence Questionnaire Urinary");
		canConvert.put("ucum", "Unified Code for Units of Measure");
		canConvert.put("ham", "Hamilton");
		canConvert.put("kps", "Karnofsky Performance Status");
		canConvert.put("dpyd", "Dihydropyrimidine dehydrogenase");
		canConvert.put("tpmt", "Thiopurine methyltransferase");
		canConvert.put("hla", "Human leukocyte antigen");
		canConvert.put("post", "following");
		canConvert.put("heart", "cardiac");
		canConvert.put("frax", "fracture");
		canConvert.put("risk", "probability");
		canConvert.put("var", "variant");
		canConvert.put("ethinyl", "ethinylestradiol");
		canConvert.put("cpax", "Chelsea Critical Care Physical Assessment tool");
		canConvert.put("neuropathy", "nerve disorder");
		canConvert.put("elongated", "Elongation");
		canConvert.put("ultrasound", "ultrasonography");
		canConvert.put("extremity", "limb");
		canConvert.put("ab", "Antibody");
		canConvert.put("sequela", "following disorder");
		canConvert.put("needed", "required");
		canConvert.put("palsy", "Paralysis");
		canConvert.put("niacin", "nicotinic");
		canConvert.put("nmol", "Nanomoles");
		canConvert.put("hb", "hemoglobin");
		canConvert.put("sec", "second");
		canConvert.put("fsiq", "Full Scale Intelligence Quotient");
		canConvert.put("oral", "mouth");
		canConvert.put("procedural", "procedure");
		canConvert.put("foot", "feet");
		canConvert.put("webbed", "syndactyly");
		canConvert.put("trauma", "injury");
		canConvert.put("ehec", "Enterohemorrhagic Escherichia");
		canConvert.put("vasc", "vascular");
		canConvert.put("pmv", "Platelet mean volume");
		canConvert.put("leg", "lower limb");
		canConvert.put("arm", "upper limb");
		canConvert.put("ige", "Immunoglobulin");
		canConvert.put("sprouts", "Brassica");
		canConvert.put("munit", "Milliunits");
		canConvert.put("min", "minute");
		canConvert.put("kg", "kilogram");
		canConvert.put("kg", "kilogram");
		canConvert.put("mmol", "Millimole");
		canConvert.put("mg", "Milligrams");
		canConvert.put("dl", "deciliter");
		canConvert.put("mcmol", "Micromoles");
		canConvert.put("gm", "gram");
		canConvert.put("yr", "year");
		canConvert.put("mcg", "Micrograms");
		canConvert.put("hr", "hour");
		canConvert.put("m2", "meter2");
		canConvert.put("m3", "cubic meter");
		canConvert.put("m", "meter");
		canConvert.put("ml", "milliliter");
		canConvert.put("gm", "Gram");
		canConvert.put("ng", "Nanogram");
		canConvert.put("cg", "Centigram");
		canConvert.put("dg", "Decigram");
		canConvert.put("w", "Weight");
		canConvert.put("v", "volume");
		canConvert.put("in", "Inch");
		canConvert.put("s", "second");
		canConvert.put("l", "liter");
		canConvert.put("n", "nitrogen");
		canConvert.put("g", "gram");

		canConvert.put("SPECT", "Single photon emission computed tomography");
		canConvert.put("IV", "intravenous");
		canConvert.put("ag", "Antigen");
		canConvert.put("IgG", "Immunoglobulin G");
		canConvert.put("mesalamine", "mesalazine");
		canConvert.put("femoral", "femur");
		canConvert.put("vascular", "vessel");
		canConvert.put("acetaminophen", "paracetamol");
		canConvert.put("propoxyphene", "dextropropoxyphene");
		canConvert.put("acyclovir", "aciclovir");
		canConvert.put("salbutamol", "albuterol");
		canConvert.put("anthralin", "dithranol");
		canConvert.put("Beclomethasone", "dipropionate beclometasone dipropionate");
		canConvert.put("ebv", "epstein barr virus");
		canConvert.put("moxfq", "manchester oxford foot questionnaire");

		System.out.println(canConvert);


		System.out.println("FSN\tTag\tWords missing\tTerm\tPT ID\tConcept");
		int total = 0;
		try (BufferedReader reader = new BufferedReader(new FileReader(descriptionFile))) {
			String line;
			String[] split;
			while ((line = reader.readLine()) != null) {
				// id	effectiveTime	active	moduleId	conceptId	languageCode	typeId	term	caseSignificanceId
				// 0	1				2		3			4			5				6		7		8
				split = line.split("\\t");
				if ("1".equals(split[2])) {
					// active
					if (activeConcepts.contains(Long.parseLong(split[4])) // concept active
							&& effectiveTimeFilter.equals(split[1]) // effectiveDate
							&& !"900000000000003001".equals(split[6]) // Syn
							&& preferredDescriptions.contains(Long.parseLong(split[0])) // US Preferred
							) {
						// Syn
						long conceptId = Long.parseLong(split[4]);
						String ptToValidate = split[7].toLowerCase();
						String fsnToValidate = conceptToFSN.get(conceptId).toLowerCase();
						Set<String> fsnWords = termToWords(fsnToValidate);
						Set<String> ptWords = termToWords(ptToValidate);
						ptWords.removeAll(fsnWords);
						ptWords.removeAll(insignificant);
						Set<String> missingWords = new HashSet<>();
						boolean ptValid = false;
						if (ptWords.isEmpty()) {
							ptValid = true;
						} else {
							for (String ptWord : ptWords) {
								if (ptWord.isEmpty() || ptWord.matches(".*[0-9].*")) {
									ptValid = true;
								} else {
									String convert = canConvert.get(ptWord);
									if (convert != null) {
										missingWords.addAll(Sets.newHashSet(convert.toLowerCase().split(" ")));
									} else {
										missingWords.add(ptWord);
									}
									missingWords.removeAll(fsnWords);
									if (missingWords.isEmpty()) {
										ptValid = true;
									} else {
										Set<String> thirdCheck = new HashSet<>();
										for (String word : missingWords) {
											if (word.charAt(word.length() - 1) == 's') {
												word = word.substring(0, word.length() -1);
											}
											thirdCheck.add(word);
										}
										thirdCheck.removeAll(fsnWords);
										missingWords = thirdCheck;
										if (missingWords.isEmpty()) {
											ptValid = true;
										} else {
											Set<String> fsnSingles = new HashSet<>();
											for (String fsnWord : fsnWords) {
												if (fsnWord.length() > 1 && fsnWord.charAt(fsnWord.length() - 1) == 's') {
													fsnWord = fsnWord.substring(0, fsnWord.length() -1);
													fsnSingles.add(fsnWord);
												}
											}
											missingWords.removeAll(fsnSingles);
											if (missingWords.isEmpty()) {
												ptValid = true;
											} else {
												// Fourth check looks up synonyms from concept's relationship targets
												Set<String> relatedConceptHasSynonym = new HashSet<>();
												for (Long relationshipTarget : conceptToRelationshipTargetsMap.get(conceptId)) {
													Set<String> terms = conceptToTermsMap.get(relationshipTarget);
													for (String missingWord : thirdCheck) {
														if (terms.contains(missingWord)) {
															relatedConceptHasSynonym.add(missingWord);
															String relatedFSN = conceptToFSN.get(relationshipTarget);
															String relatedFSNWithoutTag = relatedFSN.toLowerCase().replace(" (" + getTag(relatedFSN) + ")", "");
															relatedFSNWithoutTag = relatedFSNWithoutTag.replace(" - action", "");
															if (fsnToValidate.contains(relatedFSNWithoutTag)) {
																relatedConceptHasSynonym.add(missingWord);
															}
														}
													}
												}
												missingWords.removeAll(relatedConceptHasSynonym);

												if (missingWords.isEmpty()) {
													ptValid = true;
												}
											}
										}
									}
								}
							}
						}
						if (!ptValid) {
							System.out.println(fsnToValidate + "\t" + getTag(fsnToValidate) + "\t" + missingWords.toString().replace("[", "").replace("]", "") + "\t" + ptToValidate + "\t" + split[0] + "\t" + conceptId);
							total++;
						}
					}
				}
			}
			System.out.println("Total: " + total);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static HashSet<String> termToWords(String term) {
		String regex = "[,(){}\\.]";
		return Sets.newHashSet(term.replaceAll("[-/]", " ").replaceAll(regex, "").split(" "));
	}

	private static Map<Long, String> getConceptToFsn(String descriptionFile, Set<Long> activeConcepts) {
		Map<Long, String> conceptToFSN = new HashMap<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(descriptionFile))) {
			String line;
			String[] split;
			while ((line = reader.readLine()) != null) {
				// id	effectiveTime	active	moduleId	conceptId	languageCode	typeId	term	caseSignificanceId
				// 0	1				2		3			4			5				6		7		8
				split = line.split("\\t");
				if ("1".equals(split[2])) {
					// active
					if (activeConcepts.contains(Long.parseLong(split[4])) &&
							"900000000000003001".equals(split[6])) {
						// FSN of active concept
						String term = split[7];
						conceptToFSN.put(Long.parseLong(split[4]), term);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return conceptToFSN;
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

	private static Map<Long, Set<String>> getConceptToTermWordsMap(String descriptionFile, Set<Long> activeConcepts) {
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
						conceptToTermsMap.computeIfAbsent(parseLong(split[4]), conceptId -> new HashSet<>()).addAll(termToWords(term.toLowerCase()));
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
			reader.readLine(); // discard first line
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

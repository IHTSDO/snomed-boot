package org.ihtsdo.otf.snomedboot.factory.filter;

import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import org.ihtsdo.otf.snomedboot.factory.ImpotentComponentFactory;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.lang.Long.parseLong;

public class LatestEffectiveDateComponentFactory extends ImpotentComponentFactory {

	public static final int FAR_FUTURE = 30000101;
	private final Map<Long, Integer> latestCoreComponentEffectiveDates = new Long2IntOpenHashMap();
	private final Map<String, Integer> latestRefsetMemberEffectiveDates = new HashMap<>();
	private final Set<String> refsetMembersWithMoreThanOneRow = new HashSet<>();
	private final Map<String, Integer> latestIdentifierEffectiveDates = new HashMap<>();

	@Override
	public void newConceptState(String conceptId, String effectiveTime, String active, String moduleId, String definitionStatusId) {
		storeLatestDate(conceptId, effectiveTime);
	}

	@Override
	public void newDescriptionState(String id, String effectiveTime, String active, String moduleId, String conceptId, String languageCode, String typeId, String term, String caseSignificanceId) {
		storeLatestDate(id, effectiveTime);
	}

	@Override
	public void newRelationshipState(String id, String effectiveTime, String active, String moduleId, String sourceId, String destinationId, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId) {
		storeLatestDate(id, effectiveTime);
	}

	@Override
	public void newConcreteRelationshipState(String id, String effectiveTime, String active, String moduleId, String sourceId, String value, String relationshipGroup, String typeId, String characteristicTypeId, String modifierId) {
		storeLatestDate(id, effectiveTime);
	}

	@Override
	public void newReferenceSetMemberState(String filename, String[] fieldNames, String id, String effectiveTime, String active, String moduleId, String refsetId, String referencedComponentId, String... otherValues) {
		storeLatestDateMember(id, effectiveTime);
	}

	@Override
	public void newIdentifierState(String alternateIdentifier, String effectiveTime, String active, String moduleId, String identifierSchemeId, String referencedComponentId) {
		storeLatestDateIdentifier(alternateIdentifier + "-" + identifierSchemeId, effectiveTime);
	}

	@Override
	public void loadingComponentsCompleted() {
		// Remove redundant entries to save memory (millions of strings)
		List<String> refsetMembersWithOnlyOneRow = latestRefsetMemberEffectiveDates.keySet().stream()
				.filter(id -> !refsetMembersWithMoreThanOneRow.contains(id))
				.toList();
		for (String id : refsetMembersWithOnlyOneRow) {
			latestRefsetMemberEffectiveDates.remove(id);
		}
		LoggerFactory.getLogger(getClass()).info("{} refset members require effective time filtering", latestRefsetMemberEffectiveDates.size());
	}

	private synchronized void storeLatestDate(String componentId, String effectiveTime) {
		long id = parseLong(componentId);
		int newDate = parseInt(effectiveTime);
		if (newDateGreater(newDate, latestCoreComponentEffectiveDates.get(id))) {
			latestCoreComponentEffectiveDates.put(id, newDate);
		}
	}

	private synchronized void storeLatestDateMember(String id, String effectiveTime) {
		int newDate = parseInt(effectiveTime);
		if (latestRefsetMemberEffectiveDates.get(id) != null) {
			refsetMembersWithMoreThanOneRow.add(id);
		}
		if (newDateGreater(newDate, latestRefsetMemberEffectiveDates.get(id))) {
			latestRefsetMemberEffectiveDates.put(id, newDate);
		}
	}

	private synchronized void storeLatestDateIdentifier(String id, String effectiveTime) {
		int newDate = parseInt(effectiveTime);
		if (newDateGreater(newDate, latestIdentifierEffectiveDates.get(id))) {
			latestIdentifierEffectiveDates.put(id, newDate);
		}
	}

	private int parseInt(String effectiveTime) {
		return Strings.isNullOrEmpty(effectiveTime) ? FAR_FUTURE : Integer.parseInt(effectiveTime);
	}

	private boolean newDateGreater(int newDate, Integer existingDate) {
		return existingDate == null || newDate > existingDate;
	}

	public synchronized boolean isCoreComponentVersionInEffect(String componentId, String effectiveTime) {
		return latestCoreComponentEffectiveDates.getOrDefault(parseLong(componentId), Integer.MIN_VALUE).equals(parseInt(effectiveTime));
	}

	public synchronized boolean isIdentifierVersionInEffect(String componentId, String effectiveTime) {
		return latestIdentifierEffectiveDates.getOrDefault(componentId, Integer.MIN_VALUE).equals(parseInt(effectiveTime));
	}

	public synchronized boolean isReferenceSetMemberVersionInEffect(String memberId, String effectiveTime) {
		return !latestRefsetMemberEffectiveDates.containsKey(memberId) ||
				latestRefsetMemberEffectiveDates.getOrDefault(memberId, Integer.MIN_VALUE).equals(parseInt(effectiveTime));
	}
}

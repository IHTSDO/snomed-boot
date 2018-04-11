package org.ihtsdo.otf.snomedboot.factory.filter;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import org.ihtsdo.otf.snomedboot.factory.ImpotentComponentFactory;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

public class LatestEffectiveDateComponentFactory extends ImpotentComponentFactory {

	private Map<Long, Integer> latestCoreComponentEffectiveDates = new Long2IntOpenHashMap();
	private Map<String, Integer> latestRefsetMemberEffectiveDates = new HashMap<>();

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
	public void newReferenceSetMemberState(String[] fieldNames, String id, String effectiveTime, String active, String moduleId, String refsetId, String referencedComponentId, String... otherValues) {
		storeLatestDateMember(id, effectiveTime);
	}

	private void storeLatestDate(String componentId, String effectiveTime) {
		long id = parseLong(componentId);
		int newDate = parseInt(effectiveTime);
		if (newDateGreater(newDate, latestCoreComponentEffectiveDates.get(id))) {
			latestCoreComponentEffectiveDates.put(id, newDate);
		}
	}

	private void storeLatestDateMember(String id, String effectiveTime) {
		int newDate = parseInt(effectiveTime);
		if (newDateGreater(newDate, latestRefsetMemberEffectiveDates.get(id))) {
			latestRefsetMemberEffectiveDates.put(id, newDate);
		}
	}

	private boolean newDateGreater(int newDate, Integer existingDate) {
		return existingDate == null || newDate > existingDate;
	}

	public boolean isCoreComponentVersionInEffect(String componentId, String effectiveTime) {
		return latestCoreComponentEffectiveDates.getOrDefault(parseLong(componentId), Integer.MIN_VALUE).equals(parseInt(effectiveTime));
	}

	public boolean isReferenceSetMemberVersionInEffect(String memberId, String effectiveTime) {
		return latestRefsetMemberEffectiveDates.getOrDefault(memberId, Integer.MIN_VALUE).equals(parseInt(effectiveTime));
	}
}

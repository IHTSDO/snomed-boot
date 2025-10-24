package org.ihtsdo.otf.snomedboot.factory.implementation.standard;

import org.ihtsdo.otf.snomedboot.domain.Description;

public class DescriptionImpl implements Description {

	private final Long id;
	private final boolean active;
	private final String term;
	private final Long conceptId;
	private String languageCode;
	private String typeId;
	private String caseSignificanceId;

	public DescriptionImpl(String id, boolean active, String term, String conceptId, String languageCode, String typeId, String caseSignificanceId) {
		this.id = Long.parseLong(id);
		this.active = active;
		this.term = term;
		this.conceptId =  Long.parseLong(conceptId);
		this.languageCode = languageCode;
		this.typeId = typeId;
		this.caseSignificanceId = caseSignificanceId;
	}

	public DescriptionImpl(String term, boolean active, Long conceptId) {
		this.id = null;
		this.active = active;
		this.term = term;
		this.conceptId = conceptId;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public String getTerm() {
		return term;
	}

	@Override
	public Long getConceptId() {
		return conceptId;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getCaseSignificanceId() {
		return caseSignificanceId;
	}

	public void setCaseSignificanceId(String caseSignificanceId) {
		this.caseSignificanceId = caseSignificanceId;
	}
}

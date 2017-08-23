package org.ihtsdo.otf.snomedboot.factory.implementation.standard;

import org.ihtsdo.otf.snomedboot.domain.Description;

public class DescriptionImpl implements Description {

	private final Long id;
	private final boolean active;
	private final String term;
	private final Long conceptId;

	public DescriptionImpl(String id, boolean active, String term, String conceptId) {
		this.id = Long.parseLong(id);
		this.active = active;
		this.term = term;
		this.conceptId =  Long.parseLong(conceptId);
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
}

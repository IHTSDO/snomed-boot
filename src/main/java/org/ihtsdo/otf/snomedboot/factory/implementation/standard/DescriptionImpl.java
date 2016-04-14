package org.ihtsdo.otf.snomedboot.factory.implementation.standard;

import org.ihtsdo.otf.snomedboot.domain.Description;

public class DescriptionImpl implements Description {

	private final String id;
	private final boolean active;
	private final String term;
	private final String conceptId;

	public DescriptionImpl(String id, boolean active, String term, String conceptId) {
		this.id = id;
		this.active = active;
		this.term = term;
		this.conceptId = conceptId;
	}

	public DescriptionImpl(String term, boolean active, String conceptId) {
		this.id = "";
		this.active = active;
		this.term = term;
		this.conceptId = conceptId;
	}

	@Override
	public String getId() {
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
	public String getConceptId() {
		return conceptId;
	}
}

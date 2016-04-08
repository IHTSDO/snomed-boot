package org.ihtsdo.snomed.boot.domain;

public interface Description {
	String getId();

	boolean isActive();

	String getTerm();

	String getConceptId();
}

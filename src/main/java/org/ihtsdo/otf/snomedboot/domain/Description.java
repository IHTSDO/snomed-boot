package org.ihtsdo.otf.snomedboot.domain;

public interface Description {
	Long getId();

	boolean isActive();

	String getTerm();

	Long getConceptId();
}

package org.ihtsdo.otf.snomedboot.domain;

public interface Description {
	String getId();

	boolean isActive();

	String getTerm();

	String getConceptId();
}

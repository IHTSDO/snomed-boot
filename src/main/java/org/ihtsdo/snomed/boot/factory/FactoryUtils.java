package org.ihtsdo.snomed.boot.factory;

public class FactoryUtils {

	public static final String ACTIVE = "1";

	public static boolean parseActive(String active) {
		return ACTIVE.equals(active);
	}

}

package org.ihtsdo.otf.snomedboot.factory;

public class FactoryUtils {

	public static final String ACTIVE = "1";

	public static boolean parseActive(String active) {
		return ACTIVE.equals(active);
	}

	public static boolean isConceptId(String componentId) {
		if (componentId != null) {
			final int length = componentId.length();
			return length > 3 && componentId.substring(length - 2, length - 1).equals("0");
		}
		return false;
	}

	public static boolean isDescriptionId(String componentId) {
		if (componentId != null) {
			final int length = componentId.length();
			return length > 3 && componentId.substring(length - 2, length - 1).equals("1");
		}
		return false;
	}
}

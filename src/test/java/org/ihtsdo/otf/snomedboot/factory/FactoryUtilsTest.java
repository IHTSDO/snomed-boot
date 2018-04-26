package org.ihtsdo.otf.snomedboot.factory;

import org.junit.Test;

import static org.junit.Assert.*;

public class FactoryUtilsTest {
	@Test
	public void isConceptId() throws Exception {
		assertTrue(FactoryUtils.isConceptId("138875005"));
		assertTrue(FactoryUtils.isConceptId("900000000000441003"));
		assertTrue(FactoryUtils.isConceptId("32570731000036101"));
		assertFalse(FactoryUtils.isConceptId("517382016"));
		assertFalse(FactoryUtils.isConceptId("108645021000036118"));
	}

	@Test
	public void isDescriptionId() throws Exception {
		assertTrue(FactoryUtils.isDescriptionId("517382016"));
		assertTrue(FactoryUtils.isDescriptionId("108645021000036118"));
		assertFalse(FactoryUtils.isDescriptionId("138875005"));
		assertFalse(FactoryUtils.isDescriptionId("900000000000441003"));
		assertFalse(FactoryUtils.isDescriptionId("32570731000036101"));
	}

}
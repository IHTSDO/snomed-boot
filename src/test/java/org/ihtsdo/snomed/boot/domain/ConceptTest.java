package org.ihtsdo.snomed.boot.domain;

import org.ihtsdo.snomed.boot.factory.implementation.standard.ConceptImpl;
import org.junit.Assert;
import org.junit.Test;

public class ConceptTest {

	@Test
	public void testIsConceptId() throws Exception {
		Assert.assertEquals(true, ConceptImpl.isConceptId("123004"));
		Assert.assertEquals(false, ConceptImpl.isConceptId("123014"));
		Assert.assertEquals(false, ConceptImpl.isConceptId("123024"));
		Assert.assertEquals(true, ConceptImpl.isConceptId("123104"));
		Assert.assertEquals(false, ConceptImpl.isConceptId("123114"));
		Assert.assertEquals(false, ConceptImpl.isConceptId("123124"));
	}
}

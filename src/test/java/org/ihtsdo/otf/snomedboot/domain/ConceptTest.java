package org.ihtsdo.otf.snomedboot.domain;

import org.ihtsdo.otf.snomedboot.factory.FactoryUtils;
import org.junit.Assert;
import org.junit.Test;

public class ConceptTest {

	@Test
	public void testIsConceptId() throws Exception {
		Assert.assertEquals(true, FactoryUtils.isConceptId("123004"));
		Assert.assertEquals(false, FactoryUtils.isConceptId("123014"));
		Assert.assertEquals(false, FactoryUtils.isConceptId("123024"));
		Assert.assertEquals(true, FactoryUtils.isConceptId("123104"));
		Assert.assertEquals(false, FactoryUtils.isConceptId("123114"));
		Assert.assertEquals(false, FactoryUtils.isConceptId("123124"));
	}
}

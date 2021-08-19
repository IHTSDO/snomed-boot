package org.ihtsdo.otf.snomedboot.factory;

public interface HighLevelComponentFactory extends ComponentFactory {

	void addConceptFSN(String conceptId, String term);

	void addInferredConceptParent(String sourceId, String parentId);

	void addStatedConceptParent(String sourceId, String parentId);

	void removeInferredConceptParent(String sourceId, String destinationId);

	void removeStatedConceptParent(String sourceId, String destinationId);

	void addInferredConceptAttribute(String sourceId, String typeId, String valueId);

	void addInferredConceptConcreteAttribute(String sourceId, String typeId, String value);

	void addStatedConceptAttribute(String sourceId, String typeId, String valueId);

	void addConceptReferencedInRefsetId(String refsetId, String conceptId);
	
	void addInferredConceptChild(String sourceId, String destinationId);
	
	void addStatedConceptChild(String sourceId, String destinationId);
	
	void removeInferredConceptChild(String sourceId, String destinationId);

	void removeStatedConceptChild(String sourceId, String destinationId);
}

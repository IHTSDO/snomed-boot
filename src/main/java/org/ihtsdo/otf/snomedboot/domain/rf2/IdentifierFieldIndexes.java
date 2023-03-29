package org.ihtsdo.otf.snomedboot.domain.rf2;

public interface IdentifierFieldIndexes {

	// alternateIdentifier	effectiveTime	active	moduleId	identiferSchemeId	referencedComponentId
	// 0					1					2				3		4			5

	int alternateIdentifier = 0;
	int effectiveTime = 1;
	int active = 2;
	int moduleId = 3;
	int identifierSchemeId = 4;
	int referencedComponentId = 5;

	// For legacy Identifier file
	// identifierSchemeId	alternateIdentifier	effectiveTime	active	moduleId	referencedComponentId
	// 0					1					2				3		4			5

	int legacyIdentifierSchemeId = 0;
	int legacyAlternateIdentifier = 1;
	int legacyEffectiveTime = 2;
	int legacyActive = 3;
	int legacyModuleId = 4;
	int legacyReferencedComponentId = 5;

}

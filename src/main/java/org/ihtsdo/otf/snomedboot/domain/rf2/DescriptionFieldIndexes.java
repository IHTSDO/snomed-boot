package org.ihtsdo.otf.snomedboot.domain.rf2;

public interface DescriptionFieldIndexes extends ComponentFieldIndexes {

	// id	effectiveTime	active	moduleId	conceptId	languageCode	typeId	term	caseSignificanceId
	// 0	1				2		3			4			5				6		7		8

	int conceptId = 4;
	int languageCode = 5;
	int typeId = 6;
	int term = 7;
	int caseSignificanceId = 8;

}

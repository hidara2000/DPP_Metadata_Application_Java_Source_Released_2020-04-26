package dpp.valuemaps;

import org.apache.log4j.Logger;

import dpp.schema.OpencaptionstypeEnum;

public class UkDppOpenCaptionTypeMap extends BaseTypeMap {
	private static final Logger LOGGER = Logger.getLogger(UkDppOpenCaptionTypeMap.class);

	public UkDppOpenCaptionTypeMap() {
		super();
		mapOfValuesToAmwaNum.put(0, "Hard of Hearing");
		mapOfValuesToAmwaNum.put(1, "Translation");
	}

	public OpencaptionstypeEnum getOpencaptionstypeEnumFromValue(final String opencaptionstype) {
		OpencaptionstypeEnum opencaptionstypeEnum = OpencaptionstypeEnum.TRANSLATION;

		try {
			opencaptionstypeEnum = OpencaptionstypeEnum.fromValue(opencaptionstype);
		} catch (IllegalArgumentException iae) {
			LOGGER.error("Unexpected open captions type: " + opencaptionstype + ", defaulting to Translation");
		}

		return opencaptionstypeEnum;
	}
}

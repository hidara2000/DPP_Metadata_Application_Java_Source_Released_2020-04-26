package dpp.valuemaps;

import org.apache.log4j.Logger;

import dpp.schema.ClosedcaptionstypeEnum;

public class As11ClosedCaptionTypeMap extends BaseTypeMap {

	private static final Logger LOGGER = Logger.getLogger(As11ClosedCaptionTypeMap.class);

	public As11ClosedCaptionTypeMap() {
		super();

		mapOfValuesToAmwaNum.put(0, "Hard of Hearing");
		mapOfValuesToAmwaNum.put(1, "Translation");
	}

	public ClosedcaptionstypeEnum getClosedcaptionstypeEnum(final String closedcaptionstype) {
		ClosedcaptionstypeEnum closedcaptionstypeEnum = ClosedcaptionstypeEnum.TRANSLATION;

		try {
			closedcaptionstypeEnum = ClosedcaptionstypeEnum.fromValue(closedcaptionstype);
		} catch (IllegalArgumentException iae) {
			LOGGER.error("Unexpected closed captions type: " + closedcaptionstype + ", defaulting to Translation");
		}

		return closedcaptionstypeEnum;
	}

}

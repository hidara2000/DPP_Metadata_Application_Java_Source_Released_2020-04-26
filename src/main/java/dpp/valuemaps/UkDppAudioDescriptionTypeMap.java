package dpp.valuemaps;

import org.apache.log4j.Logger;

import dpp.schema.AudiodescriptiontypeEnum;

public class UkDppAudioDescriptionTypeMap extends BaseTypeMap {
	private static final Logger LOGGER = Logger.getLogger(UkDppAudioDescriptionTypeMap.class);

	public UkDppAudioDescriptionTypeMap() {
		super();
		mapOfValuesToAmwaNum.put(0, "Control data / Narration");
		mapOfValuesToAmwaNum.put(1, "AD Mix");
	}

	public AudiodescriptiontypeEnum getAudiodescriptiontypeEnumFromValue(final String audioDescriptionType) {
		AudiodescriptiontypeEnum audiodescriptiontypeEnum = AudiodescriptiontypeEnum.AD_MIX;

		try {
			audiodescriptiontypeEnum = AudiodescriptiontypeEnum.fromValue(audioDescriptionType);
		} catch (IllegalArgumentException iae) {
			LOGGER.error("Unexpected Audio Description Type: " + audioDescriptionType + ", defaulting to Ad Mix");
		}

		return audiodescriptiontypeEnum;
	}

}

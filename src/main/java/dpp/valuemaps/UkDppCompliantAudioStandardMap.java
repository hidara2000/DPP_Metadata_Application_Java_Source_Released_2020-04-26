package dpp.valuemaps;

import org.apache.log4j.Logger;

import dpp.schema.AudioloudnessstandardEnum;

public class UkDppCompliantAudioStandardMap extends BaseTypeMap {

	private static final Logger LOGGER = Logger.getLogger(UkDppCompliantAudioStandardMap.class);

	public AudioloudnessstandardEnum getAudioloudnessEnumFromValue(final String compliantAudioStandard) {
		AudioloudnessstandardEnum audioLoudnessEnum = AudioloudnessstandardEnum.NONE;

		try {
			audioLoudnessEnum = AudioloudnessstandardEnum.fromValue(compliantAudioStandard);
		} catch (IllegalArgumentException iae) {
			LOGGER.error("Unexpected Audio Loudness: " + compliantAudioStandard + ", defaulting to None");
		}

		return audioLoudnessEnum;
	}

}

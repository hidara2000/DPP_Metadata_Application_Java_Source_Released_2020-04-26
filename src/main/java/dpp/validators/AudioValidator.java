package dpp.validators;

import org.apache.log4j.Logger;

import dpp.bmx.CommandLineConstants;
import dpp.reporting.StatusReport;
import dpp.schema.AudiosamplingfrequencyDt;
import dpp.schema.AudiotracklayoutEnum;
import dpp.schema.Programme;
import dpp.valuemaps.Iso_639_2_Map;

public class AudioValidator {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(AudioValidator.class);
	private final Iso_639_2_Map iso_639_2_Map = new Iso_639_2_Map();

	public boolean validateAudio(final Programme programme, NonProgrammeData nonProgrammeData, StatusReport statusReport, boolean validateOnlyStructuralData) {
		// Structural metadata is that which we can populate using mfx2raw -i, ie we should always be able to populate it.
		boolean validAudio = false;

		boolean validAudioStructuralMetadata = validateAudioStructuralMetadata(programme, nonProgrammeData, statusReport);
		if (validateOnlyStructuralData) {
			validAudio = validAudioStructuralMetadata;
		} else {
			boolean validAudioNonStructuralMetadata = validateAudioNonStructuralMetadata(programme, nonProgrammeData, statusReport);
			validAudio = validAudioStructuralMetadata && validAudioNonStructuralMetadata;
		}

		return validAudio;
	}

	protected boolean validateAudioNonStructuralMetadata(Programme programme, NonProgrammeData nonProgrammeData, StatusReport statusReport) {
		boolean validAudioTrackLayout = validateAudioTrackLayout(programme, nonProgrammeData, statusReport);
		boolean validPrimaryLanguage = validatePrimaryLanguage(programme, statusReport);
		boolean validSecondaryLanguage = validateSecondaryLanguage(programme, statusReport);
		boolean validTertiaryLanguage = validateTertiaryLanguage(programme, statusReport);

		boolean validAudioNonStructuralMetadata = validAudioTrackLayout && validPrimaryLanguage && validSecondaryLanguage && validTertiaryLanguage;
		return validAudioNonStructuralMetadata;
	}

	protected boolean validateAudioStructuralMetadata(Programme programme, NonProgrammeData nonProgrammeData, StatusReport statusReport) {
		boolean validAudioCodecParamsAndBitDepth = validateAudioCodecParamsAndBitDepth(programme, nonProgrammeData, statusReport);
		boolean validNumberOfAudioChannels = validateNumberOfAudioChannels(nonProgrammeData, statusReport);
		return validAudioCodecParamsAndBitDepth && validNumberOfAudioChannels;
	}

	// NON STRUCTURAL METADATA VALIDATION

	protected boolean validatePrimaryLanguage(Programme programme, StatusReport statusReport) {
		// Must be set

		String primaryAudioLanguage = programme.getTechnical().getAudio().getPrimaryAudioLanguage();
		if (primaryAudioLanguage == null || primaryAudioLanguage.length() == 0) {
			statusReport.reportValidationError("Primary Audio Language is missing.");
			return false;
		}
		if (!iso_639_2_Map.isValidLanguageCode(primaryAudioLanguage)) {
			statusReport.reportValidationError("Unknown Primary Audio Language specified.");
			return false;
		}

		return true;
	}

	protected boolean validateSecondaryLanguage(Programme programme, StatusReport statusReport) {
		// Must be set when "AudioTrackLayout" = "EBU R 123: 16d" or "EBU R 123: 16f";
		boolean validSecondaryLanguage = true;

		AudiotracklayoutEnum audioTrackLayout = programme.getTechnical().getAudio().getAudioTrackLayout();
		boolean requireSecondaryLanguage = audioTrackLayout == AudiotracklayoutEnum.EBU_R_123_16_D || audioTrackLayout == AudiotracklayoutEnum.EBU_R_123_16_F;

		if (!requireSecondaryLanguage) {
			return true;
		}

		String secondaryAudioLanguage = programme.getTechnical().getAudio().getSecondaryAudioLanguage();
		if (secondaryAudioLanguage == null || secondaryAudioLanguage.isEmpty()) {
			statusReport.reportValidationError("Secondary Audio Language is missing.");
			return false;
		}

		if (!iso_639_2_Map.isValidLanguageCode(secondaryAudioLanguage)) {
			statusReport.reportValidationError("Unknown Secondary Audio Language specified.");
			validSecondaryLanguage = false;
		}
		String primaryAudioLanguage = programme.getTechnical().getAudio().getPrimaryAudioLanguage();
		if (secondaryAudioLanguage.equalsIgnoreCase(primaryAudioLanguage)) {
			statusReport.reportValidationError("Secondary Audio Language is same as Primary Audio Language.");
			validSecondaryLanguage = false;
		}

		return validSecondaryLanguage;
	}

	protected boolean validateTertiaryLanguage(Programme programme, StatusReport statusReport) {
		// Must be set when "AudioTrackLayout" ="EBU R 123: 16f"
		boolean validTertiaryLanguage = true;

		AudiotracklayoutEnum audioTrackLayout = programme.getTechnical().getAudio().getAudioTrackLayout();
		boolean requireTertiaryLanguage = audioTrackLayout == AudiotracklayoutEnum.EBU_R_123_16_F;
		if (!requireTertiaryLanguage) {
			return true;
		}

		String tertiaryAudioLanguage = programme.getTechnical().getAudio().getTertiaryAudioLanguage();
		if (tertiaryAudioLanguage == null || tertiaryAudioLanguage.isEmpty()) {
			statusReport.reportValidationError("Tertiary Audio Language is missing.");
			return false;
		}

		if (!iso_639_2_Map.isValidLanguageCode(tertiaryAudioLanguage)) {
			statusReport.reportValidationError("Unknown Tertiary Audio Language specified.");
			return false;
		}
		String primaryAudioLanguage = programme.getTechnical().getAudio().getPrimaryAudioLanguage();
		String secondaryAudioLanguage = programme.getTechnical().getAudio().getSecondaryAudioLanguage();
		if (tertiaryAudioLanguage.equalsIgnoreCase(primaryAudioLanguage)) {
			statusReport.reportValidationError("Tertiary Audio Language is same as Primary Audio Language.");
			return false;
		}
		if (tertiaryAudioLanguage.equalsIgnoreCase(secondaryAudioLanguage)) {
			statusReport.reportValidationError("Tertiary Audio Language is same as Secondary Audio Language.");
			return false;
		}

		return validTertiaryLanguage;
	}

	protected boolean validateAudioTrackLayout(Programme programme, NonProgrammeData nonProgrammeData, StatusReport statusReport) {
		boolean validAudioTrackLayout = false;
		// Must be "EBU R 123: 16c", "EBU R 123: 16d" or "EBU R 123: 16f", when the sum of "Channel count" = 16.
		// Must be "EBU R 48: 2a", "EBU R 123: 4b" or "EBU R 123: 4c", when the sum of "Channel count" = 4.
		AudiotracklayoutEnum audioTrackLayout = programme.getTechnical().getAudio().getAudioTrackLayout();

		if (nonProgrammeData.getTotalNumberOfAudioChannels() == 16) {
			validAudioTrackLayout = audioTrackLayout == AudiotracklayoutEnum.EBU_R_123_16_C || audioTrackLayout == AudiotracklayoutEnum.EBU_R_123_16_D
					|| audioTrackLayout == AudiotracklayoutEnum.EBU_R_123_16_F;
		} else if (nonProgrammeData.getTotalNumberOfAudioChannels() == 4) {
			validAudioTrackLayout = audioTrackLayout == AudiotracklayoutEnum.EBU_R_48_2_A || audioTrackLayout == AudiotracklayoutEnum.EBU_R_123_4_B
					|| audioTrackLayout == AudiotracklayoutEnum.EBU_R_123_4_C;
		} else {
			// should never happen as only supporting 4 or 16 channels - drop through as invalid
			validAudioTrackLayout = false;
		}
		if (!validAudioTrackLayout) {
			statusReport.reportValidationError("Invalid audio track layout");
		}

		return validAudioTrackLayout;
	}

	// END OF NON STRUCTURAL METADATA VALIDATION

	// STRUCTURAL METADATA VALIDATION
	protected boolean validateAudioCodecParamsAndBitDepth(final Programme programme, NonProgrammeData nonProgrammeData, StatusReport statusReport) {
		boolean validAudioCodecParamsAndBitDepth = true;
		boolean codecParamsOK = false;
		String audioCodecParameters = programme.getTechnical().getAudio().getAudioCodecParameters();
		AudiosamplingfrequencyDt audioSamplingFrequency = programme.getTechnical().getAudio().getAudioSamplingFrequency();
		long audioBitDepth = programme.getTechnical().getAudio().getAudioBitDepth();
		if (audioCodecParameters == null) {
			statusReport.reportValidationError("Audio codec parameters not found.");
			validAudioCodecParamsAndBitDepth = false;
		} else {
			if (nonProgrammeData.isHighDefinition()) {
				codecParamsOK = audioCodecParameters.equalsIgnoreCase(CommandLineConstants.PCM);
			} else {
				codecParamsOK = audioCodecParameters.equalsIgnoreCase(CommandLineConstants.DPP_D10_AES3_PCM);
			}
			if (!codecParamsOK) {
				statusReport.reportValidationError("Invalid audio codec parameters (" + audioCodecParameters + ").");
				validAudioCodecParamsAndBitDepth = false;
			}
		}
		if (!String.valueOf(audioSamplingFrequency.getValue()).equals(CommandLineConstants.AUDIO_SAMPLING_RATE_KHZ)) {
			statusReport.reportValidationError("Invalid audio sampling frequency (" + audioSamplingFrequency.getValue() + "), expected ("
					+ CommandLineConstants.AUDIO_SAMPLING_RATE_KHZ + "KHz).");
			validAudioCodecParamsAndBitDepth = false;
		}

		if (audioBitDepth != CommandLineConstants.AUDIO_BIT_DEPTH_VALUE) {
			statusReport.reportValidationError("Invalid audio bit depth (" + audioBitDepth + ").");
			validAudioCodecParamsAndBitDepth = false;
		}

		return validAudioCodecParamsAndBitDepth;
	}

	protected boolean validateNumberOfAudioChannels(NonProgrammeData nonProgrammeData, StatusReport statusReport) {
		boolean numberOfAudioTracksOk = false;
		int numberOfAudioChannels = nonProgrammeData.getTotalNumberOfAudioChannels();

		if (nonProgrammeData.isHighDefinition()) {
			numberOfAudioTracksOk = numberOfAudioChannels == 4 || numberOfAudioChannels == 16;
			if (!numberOfAudioTracksOk) {
				statusReport.reportValidationError("Incorrect number of audio tracks found (" + numberOfAudioChannels
						+ "), acceptable values are 4 or 16 for HD content.");
			}
		} else {
			numberOfAudioTracksOk = numberOfAudioChannels == 4;
			if (!numberOfAudioTracksOk) {
				statusReport.reportValidationError("Incorrect number of audio tracks found (" + numberOfAudioChannels
						+ "), acceptable values are 4 for SD content.");
			}
		}
		return numberOfAudioTracksOk;

	}

	// END OF STRUCTURAL METADATA VALIDATION
}

package dpp.validators;

import org.apache.log4j.Logger;

import dpp.reporting.StatusReport;
import dpp.schema.AudiodescriptiontypeEnum;
import dpp.schema.AudiotracklayoutEnum;
import dpp.schema.ClosedcaptionstypeEnum;
import dpp.schema.OpencaptionstypeEnum;
import dpp.schema.Programme;
import dpp.schema.SigningEnum;
import dpp.schema.SignlanguageEnum;
import dpp.valuemaps.Iso_639_2_Map;

public class AccessServicesValidator {

	private static final Logger LOGGER = Logger.getLogger(AccessServicesValidator.class);
	private final Iso_639_2_Map iso_639_2_Map = new Iso_639_2_Map();

	public boolean validateAccessServices(final Programme programme, StatusReport statusReport) {
		boolean validAccessServices = true;

		try {
			boolean validAudioDescriptionPresent = validateAudioDescriptionPresent(programme, statusReport);
			boolean validAudioDescriptionType = validateAudioDescriptionType(programme, statusReport);
			boolean validClosedCaptions = validateClosedCaptions(programme, statusReport);
			boolean validOpenCaptions = validateOpenCaptions(programme, statusReport);
			boolean validSignLanguage = validateSignLanguage(programme, statusReport);

			validAccessServices = validAudioDescriptionPresent && validAudioDescriptionType && validClosedCaptions && validOpenCaptions && validSignLanguage;
		} catch (Exception e) {
			validAccessServices = false;
			LOGGER.error("Caught exception when validating AccessServices : " + e);
			statusReport.reportValidationError("Invalid AccessServices.");
		}
		return validAccessServices;
	}

	protected boolean validateSignLanguage(Programme programme, StatusReport statusReport) {
		// Must be set when "SigningPresent" = "Yes" or "Signer only"; must not when "SigningPresent" = "No".

		boolean validSignLanguage = true;
		SigningEnum signingPresent = programme.getTechnical().getAccessServices().getSigningPresent();
		SignlanguageEnum signLanuage = programme.getTechnical().getAccessServices().getSignLanguage();
		boolean signLanguagePresent = signLanuage != null;

		switch (signingPresent) {
		case YES:
		case SIGNER_ONLY:
			if (!signLanguagePresent) {
				statusReport.reportValidationError("Sign language not specified when flagged as present");
				validSignLanguage = false;
			}
			break;
		case NO:
			if (signLanguagePresent) {
				statusReport.reportValidationError("Sign language specified when flagged as not present");
				validSignLanguage = false;
			}
			break;
		}

		return validSignLanguage;
	}

	protected boolean validateClosedCaptions(Programme programme, StatusReport statusReport) {
		// Must be set when "ClosedCaptionsPresent" = true; must not when "ClosedCaptionsPresent" = false
		// If set, ClosedCaptionsLanguage must be one of the alpha-3 codes in "ISO 639-2".
		boolean validClosedCaptions = true;
		boolean closedCaptionsPresent = programme.getTechnical().getAccessServices().isClosedCaptionsPresent();
		String closedCaptionsLanguage = programme.getTechnical().getAccessServices().getClosedCaptionsLanguage();
		ClosedcaptionstypeEnum closedCaptionsType = programme.getTechnical().getAccessServices().getClosedCaptionsType();

		boolean closedCaptionsTypePresent = closedCaptionsType != null;
		boolean closedCaptionsLanguagePresent = closedCaptionsLanguage != null;

		if (closedCaptionsPresent) {
			if (!closedCaptionsTypePresent) {
				validClosedCaptions = false;
				statusReport.reportValidationError("Closed captions type must be specified when flagged as present");
			}
			if (!closedCaptionsLanguagePresent) {
				validClosedCaptions = false;
				statusReport.reportValidationError("Closed captions language must be specified when flagged as present");
			} else {
				if (!iso_639_2_Map.isValidLanguageCode(closedCaptionsLanguage)) {
					validClosedCaptions = false;
					statusReport.reportValidationError("Closed captions language is not a valid ISO 639_2 code");
				}
			}

		} else {
			if (closedCaptionsTypePresent) {
				validClosedCaptions = false;
				statusReport.reportValidationError("Closed captions type specified when flagged as not present");
			}
			if (closedCaptionsLanguagePresent) {
				validClosedCaptions = false;
				statusReport.reportValidationError("Closed captions language specified when flagged as not present");
			}

		}

		return validClosedCaptions;
	}

	protected boolean validateOpenCaptions(Programme programme, StatusReport statusReport) {
		// Must be set when "OpenCaptionsPresent" = true; must not when "OpenCaptionsPresent" = false
		// If set, OpenCaptionsLanguage must be one of the alpha-3 codes in "ISO 639-2".
		boolean validOpenCaptions = true;
		boolean openCaptionsPresent = programme.getTechnical().getAccessServices().isOpenCaptionsPresent();
		String openCaptionsLanguage = programme.getTechnical().getAccessServices().getOpenCaptionsLanguage();
		OpencaptionstypeEnum openCaptionsType = programme.getTechnical().getAccessServices().getOpenCaptionsType();

		boolean openCaptionsTypePresent = openCaptionsType != null;
		boolean openCaptionsLanguagePresent = openCaptionsLanguage != null;

		if (openCaptionsPresent) {
			if (!openCaptionsTypePresent) {
				validOpenCaptions = false;
				statusReport.reportValidationError("Open captions type must be specified when flagged as present");
			}
			if (!openCaptionsLanguagePresent) {
				validOpenCaptions = false;
				statusReport.reportValidationError("Open captions language must be specified when flagged as present");
			} else {
				if (!iso_639_2_Map.isValidLanguageCode(openCaptionsLanguage)) {
					validOpenCaptions = false;
					statusReport.reportValidationError("Open captions language is not a valid ISO 639_2 code");
				}
			}

		} else {
			if (openCaptionsTypePresent) {
				validOpenCaptions = false;
				statusReport.reportValidationError("Open captions type specified when flagged as not present");
			}
			if (openCaptionsLanguagePresent) {
				validOpenCaptions = false;
				statusReport.reportValidationError("Open captions language specified when flagged as not present");
			}

		}

		return validOpenCaptions;
	}

	protected boolean validateAudioDescriptionType(Programme programme, StatusReport statusReport) {
		// Must be set when "AudioDescriptionPresent" = true; must not when "AudioDescriptionPresent" = false.
		boolean validAudioDescriptionType = true;
		AudiodescriptiontypeEnum audioDescriptionType = programme.getTechnical().getAccessServices().getAudioDescriptionType();
		boolean audioDescriptionPresent = programme.getTechnical().getAccessServices().isAudioDescriptionPresent();
		if (audioDescriptionPresent) {
			if (audioDescriptionType == null) {
				validAudioDescriptionType = false;
				statusReport.reportValidationError("AudioDescriptionType not specified when flagged as present");
			}
		} else {
			if (audioDescriptionType != null) {
				validAudioDescriptionType = false;
				statusReport.reportValidationError("AudioDescriptionType specified when flagged as not present");
			}
		}
		return validAudioDescriptionType;
	}

	protected boolean validateAudioDescriptionPresent(Programme programme, StatusReport statusReport) {
		// Must be set when "AudioTrackLayout" = "EBU R 123: 16c";
		// otherwise if set, must be "true" when "AudioTrackLayout" = "EBU R 123: 4c"; must be "false" when "AudioTrackLayout" = any other value except
		// "EBU R 123: 16c".
		// In other words:
		// if "AudioTrackLayout" = "EBU R 123: 16c" then true or false, else if "AudioTrackLayout" = "EBU R 123: 4c" then must be true else false
		boolean validAudioDescriptionPresent = true;
		boolean audioDescriptionPresent = programme.getTechnical().getAccessServices().isAudioDescriptionPresent();
		AudiotracklayoutEnum audioTrackLayout = programme.getTechnical().getAudio().getAudioTrackLayout();

		switch (audioTrackLayout) {
		case EBU_R_123_16_C:
			// happy either way
			break;
		case EBU_R_123_4_C:
			if (!audioDescriptionPresent) {
				statusReport.reportValidationError("AudioDescriptionPresent set to false for Audiotracklayout EBU R 123: 4c");
				validAudioDescriptionPresent = false;
			}
			break;
		default:
			if (audioDescriptionPresent) {
				statusReport.reportValidationError("AudioDescriptionPresent set to true for Audiotracklayout which does not allow this");
				validAudioDescriptionPresent = false;
			}
			break;

		}

		return validAudioDescriptionPresent;
	}

}
// protected boolean validateProgrammeTextLanguage(Programme programme, StatusReport statusReport) {
// // Must be set when "ProgrammeHasText" = true; must not when "ProgrammeHasText" = false;
// // If set, must be one of the alpha-3 codes in "ISO 639-2".
// boolean validProgrammeTextLanguage = true;
// Boolean programmeHasText = programme.getTechnical().getAdditional().isProgrammeHasText();
// String programmeTextLanguage = programme.getTechnical().getAdditional().getProgrammeTextLanguage();
//
// if (programmeHasText) {
// if (programmeTextLanguage == null || programmeTextLanguage.isEmpty()) {
// statusReport.reportValidationError("No Programme Text Language specified when flagged as having one.");
// validProgrammeTextLanguage = false;
// } else if (!iso_639_2_Map.isValidLanguageCode(programmeTextLanguage)) {
// statusReport.reportValidationError("Unknown Programme Text Language specified.");
// validProgrammeTextLanguage = false;
// }
// } else {
// if (programmeTextLanguage != null && !programmeTextLanguage.isEmpty()) {
// statusReport.reportValidationError("Programme Text Language specified when flagged as not having one.");
// validProgrammeTextLanguage = false;
// }
// }
//
// return validProgrammeTextLanguage;
// }

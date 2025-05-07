package dpp.validators;

import org.apache.log4j.Logger;

import dpp.reporting.StatusReport;
import dpp.schema.Programme;
import dpp.valuemaps.Iso_639_2_Map;

public class AdditionalValidator {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(AdditionalValidator.class);
	private final Iso_639_2_Map iso_639_2_Map = new Iso_639_2_Map();

	public boolean validateAdditional(final Programme programme, StatusReport statusReport) {

		return validateProgrammeTextLanguage(programme, statusReport);
	}

	private boolean validateProgrammeTextLanguage(Programme programme, StatusReport statusReport) {
		// Must be set when "ProgrammeHasText" = true; must not when "ProgrammeHasText" = false;
		// If set, must be one of the alpha-3 codes in "ISO 639-2".
		boolean validProgrammeTextLanguage = true;
		Boolean programmeHasText = programme.getTechnical().getAdditional().isProgrammeHasText();
		String programmeTextLanguage = programme.getTechnical().getAdditional().getProgrammeTextLanguage();

		if (programmeHasText == null) {
			return true; // optional field.
		}

		if (programmeHasText) {
			if (programmeTextLanguage == null || programmeTextLanguage.isEmpty()) {
				statusReport.reportValidationError("No Programme Text Language specified when flagged as having one.");
				validProgrammeTextLanguage = false;
			} else if (!iso_639_2_Map.isValidLanguageCode(programmeTextLanguage)) {
				statusReport.reportValidationError("Unknown Programme Text Language specified.");
				validProgrammeTextLanguage = false;
			}
		} else {
			if (programmeTextLanguage != null && !programmeTextLanguage.isEmpty()) {
				statusReport.reportValidationError("Programme Text Language specified when flagged as not having one.");
				validProgrammeTextLanguage = false;
			}
		}

		return validProgrammeTextLanguage;
	}

}

package dpp.validators;

import org.apache.log4j.Logger;

import dpp.reporting.StatusReport;
import dpp.schema.Programme;

/**
 * Validate structural information, eg, duration.
 * 
 * @author Darren Greaves
 * 
 */
public class StructuralValidator {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(StructuralValidator.class);

	/**
	 * @param programme
	 * @param nonProgrammeData
	 * @param statusReport
	 * @return
	 */
	public boolean validate(final Programme programme, NonProgrammeData nonProgrammeData, StatusReport statusReport) {

		return validateDuration(nonProgrammeData, statusReport);
	}

	/**
	 * Duration of asset must be at least 2 minutes to allow for line-up - ensure this is the case.
	 * 
	 * @param nonProgrammeData
	 * @param statusReport
	 * @return
	 */
	private boolean validateDuration(NonProgrammeData nonProgrammeData, StatusReport statusReport) {

		Long duration = nonProgrammeData.getDuration();
		boolean validDuration;

		if (duration > 2 * 60 * 25) {
			validDuration = true;
		} else {
			statusReport.reportValidationError("File should be at least 2 minutes in duration.");
			validDuration = false;
		}

		return validDuration;
	}

}

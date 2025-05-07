package dpp.validators;

import org.apache.log4j.Logger;

import dpp.reporting.StatusReport;
import dpp.schema.Programme;

public class EditorialValidator {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(EditorialValidator.class);

	public boolean validateEditorial(final Programme programme, StatusReport statusReport) {
		// OtherIdentifier != null ? (Other Identifier Type != null ? OK : Error) : (Other Identifier Type == null ? OK : Error)
		boolean validEditorialSection = true;

		boolean validOtherIdentifier = true;
		String otherIdentifier = programme.getEditorial().getOtherIdentifier();
		String otherIdentifierType = programme.getEditorial().getOtherIdentifierType();
		boolean haveOtherId = (otherIdentifier != null && otherIdentifier != "");
		boolean haveOtherIdType = (otherIdentifierType != null && otherIdentifierType != "");
		if (haveOtherId && !haveOtherIdType) {
			statusReport.reportValidationError("Missing other identifier type");
			validOtherIdentifier = false;
		}
		if (!haveOtherId && haveOtherIdType) {
			statusReport.reportValidationError("Other identifier type specified but not Other Identifier");
			validOtherIdentifier = false;
		}

		validEditorialSection = validOtherIdentifier;

		return validEditorialSection;
	}
}

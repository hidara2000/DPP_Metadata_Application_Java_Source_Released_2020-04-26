package dpp.validators;

import java.util.regex.Pattern;

import dpp.reporting.StatusReport;

public class ShimNameValidator {

	// Test for AS with no letters either side.
	private final static String AS_NO_LETTERS = "[^a-z]+as[^a-z]+";

	// Test for AS followed by 11 with no numbers either side of the 11.
	private final static String AS_NO_NUMBERS = "as.*\\D+11\\D+|as11\\D+";

	// Test for HD with no letters either side.
	private final static String HD_NO_LETTERS = "[^a-z]+hd[^a-z]+";

	// Test for SD with no letters either side.
	private final static String SD_NO_LETTERS = "[^a-z]+sd[^a-z]+";

	// Test for UK and DPP with no letters either side but allowing for no space between them.
	private final static String UK_DPP_NO_LETTERS = "[^a-z]+uk[^a-z]+dpp[^a-z]+|[^a-z]+dpp[^a-z]+uk[^a-z]+|[^a-z]+ukdpp[^a-z]+";

	public boolean validateShimName(String shimName, String shimVersion, boolean isHd, StatusReport statusReport) {

		String padded = String.format(" %s ", shimName.toLowerCase());

		boolean asNoLetters = Pattern.compile(AS_NO_LETTERS).matcher(padded).find();
		if (!asNoLetters) {
			statusReport.reportValidationError("'AS' must have no letters before or after");
		}

		boolean asNoNumbers = Pattern.compile(AS_NO_NUMBERS).matcher(padded).find();
		if (!asNoNumbers) {
			statusReport.reportValidationError("'11' must have no numbers before or after");
		}

		boolean ukDppNoLetters = Pattern.compile(UK_DPP_NO_LETTERS).matcher(padded).find();
		if (!ukDppNoLetters) {
			statusReport.reportValidationError("'UKDPP' must have no letters before or after");
		}

		boolean hdOrSdNoLetters;
		if (isHd) {
			hdOrSdNoLetters = Pattern.compile(HD_NO_LETTERS).matcher(padded).find();
		} else {
			hdOrSdNoLetters = Pattern.compile(SD_NO_LETTERS).matcher(padded).find();
		}

		if (!hdOrSdNoLetters) {
			statusReport.reportValidationError("Shim name must contain either 'HD' or 'SD' as appropriate for the content");
		}

		String revPattern = String.format("[^a-z0-9]rev\\.?%s\\.", shimVersion);
		boolean rev = Pattern.compile(revPattern).matcher(padded).find();

		if (!rev) {
			statusReport.reportValidationError("'Rev' must have dot after version number and/or version number doesn't match");
		}

		return asNoLetters && asNoNumbers && hdOrSdNoLetters && ukDppNoLetters && rev;
	}
}

package dpp.validators;

import java.util.List;

import org.apache.log4j.Logger;

import dpp.reporting.StatusReport;
import dpp.schema.Part;
import dpp.schema.Parts;
import dpp.schema.Programme;
import dpp.schema.Timecodes;
import dpp.util.Timecode;

public class TimecodeValidator {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(TimecodeValidator.class);
	private static final String EXPECTED_FIRST_PART_SOM = "10:00:00:00";
	private static final float TWO_MINUTES = 2 * 60;
	private static final float THIRTY_SECONDS = 30;

	public boolean validateTimecode(final Programme programme, NonProgrammeData nonProgrammeData, StatusReport statusReport) {
		boolean validTimecode = false;

		boolean validLineUpStart = validateLineUpStart(programme, nonProgrammeData, statusReport);
		boolean validIdentClockStart = validateIdentClockStart(programme, statusReport);
		boolean validParts = validateParts(programme, statusReport);
		boolean validTotalNumberOfParts = validateTotalNumberOfParts(programme, statusReport);
		boolean validDuration = validateDuration(programme, nonProgrammeData, statusReport);
		boolean validTotalProgrammeDuration = validateTotalProgrammeDuration(programme, statusReport);

		validTimecode = validLineUpStart && validIdentClockStart && validParts && validTotalNumberOfParts && validDuration && validTotalProgrammeDuration;
		return validTimecode;
	}

	protected boolean validateParts(Programme programme, StatusReport statusReport) {
		// PartNumber Must not exceed the value of "PartTotal".
		// PartTotal Must be greater or equal to the value of "TotalNumberOfParts".
		// PartSOM Any subsequent part must start after the end of previous part. Must be "10:00:00:00" when "PartNumber" = 1. A gap of at last 1 second must
		// exist between parts (Warn).
		// PartDuration Must be greater than 0.
		boolean validParts = true;
		int totalNumberOfParts = programme.getTechnical().getTimecodes().getTotalNumberOfParts();

		Timecode timeCodeEndOfLastTrack = null;
		List<Part> parts = programme.getTechnical().getTimecodes().getParts().getPart();
		for (Part part : parts) {
			int partNumber = part.getPartNumber();
			String partDuration = part.getPartDuration();
			Timecode timeCodePartDuration = Timecode.getInstance(partDuration);
			String partSOM = part.getPartSOM();
			Timecode timeCodePartSom = Timecode.getInstance(partSOM);
			int partTotal = part.getPartTotal();

			if (partNumber > partTotal) {
				statusReport.reportValidationError("Invalid Part. partNumber cannot be greater than partTotal");
				validParts = false;
			}

			if (partTotal < totalNumberOfParts) {
				statusReport.reportValidationError("Invalid Part. partTotal cannot be less than totalNumberOfParts");
				validParts = false;
			}

			if (partNumber == 1) {
				if (partSOM == null || !partSOM.equalsIgnoreCase(EXPECTED_FIRST_PART_SOM)) {
					statusReport.reportValidationError("Invalid Part. First part SOM must be " + EXPECTED_FIRST_PART_SOM);
					validParts = false;
				}
			} else if (timeCodeEndOfLastTrack != null) {
				// Any subsequent part must start after the end of previous part. Must be "10:00:00:00" when "PartNumber" = 1. A gap of at last 1 second
				// must exist between parts. If the gap is less than 2 minutes then Warn.
				if (!timeCodePartSom.after(timeCodeEndOfLastTrack)) {
					statusReport.reportValidationError("Invalid Part. Part must start after end of previous part");
					validParts = false;
				} else {
					float differenceInSeconds = Timecode.getDifferenceInSeconds(timeCodeEndOfLastTrack, timeCodePartSom);
					if (differenceInSeconds < 1.0) {
						statusReport.reportValidationWarning("Invalid Part. Part must start at least a second after end of previous part");
						validParts = false;
					}
				}
			}

			if (timeCodePartDuration.getNumberOfFrames().longValue() <= 0) {
				statusReport.reportValidationError("Invalid Part. Duration must be positive");
				validParts = false;
			}

			timeCodeEndOfLastTrack = Timecode.sum(timeCodePartSom, timeCodePartDuration);

		}

		return validParts;
	}

	protected boolean validateLineUpStart(Programme programme, NonProgrammeData nonProgrammeData, StatusReport statusReport) {
		// Must be 2 minutes before the first "PartSOM".
		boolean validLineUpStart = true;

		String lineUpStart = programme.getTechnical().getTimecodes().getLineUpStart();
		Timecode lineUpStartTimecode = Timecode.getInstance(lineUpStart);
		Parts parts = programme.getTechnical().getTimecodes().getParts();
		Timecode mst = nonProgrammeData.getMaterialStartAsTimecode();
		List<Part> part = parts.getPart();

		if (part.size() > 0) {
			Part firstPart = part.get(0);

			String firstPartSOM = firstPart.getPartSOM();

			Timecode firstPartSomTimecode = Timecode.getInstance(firstPartSOM);

			float differenceInSeconds = Timecode.getDifferenceInSeconds(lineUpStartTimecode, firstPartSomTimecode);
			if (differenceInSeconds != TWO_MINUTES) {
				statusReport.reportValidationError("LineUpStart is invalid - must be two minutes before first part SOM.");
				validLineUpStart = false;
			}
		}

		if (mst != null && mst.after(lineUpStartTimecode)) {
			statusReport.reportValidationError("LineUpStart is invalid - must be after or equal to material start timecode.");
			validLineUpStart = false;
		}

		return validLineUpStart;
	}

	protected boolean validateIdentClockStart(Programme programme, StatusReport statusReport) {
		// Must be 30 seconds beforethe first "PartSOM".
		boolean validIdentClockStart = true;

		String identClockStart = programme.getTechnical().getTimecodes().getIdentClockStart();
		Parts parts = programme.getTechnical().getTimecodes().getParts();
		List<Part> part = parts.getPart();
		if (part.size() > 0) {
			Part firstPart = part.get(0);

			String firstPartSOM = firstPart.getPartSOM();

			Timecode identClockStartTimecode = Timecode.getInstance(identClockStart);
			Timecode firstPartSomTimecode = Timecode.getInstance(firstPartSOM);

			float differenceInSeconds = Timecode.getDifferenceInSeconds(identClockStartTimecode, firstPartSomTimecode);
			if (differenceInSeconds != THIRTY_SECONDS) {
				statusReport.reportValidationError("IdentClockStart is invalid - must be thirty seconds before first part SOM.");
				validIdentClockStart = false;
			}
		}
		return validIdentClockStart;
	}

	protected boolean validateTotalNumberOfParts(Programme programme, StatusReport statusReport) {
		// Must equal to the number of members in the "Timecode" repeating group.

		int totalNumberOfParts = programme.getTechnical().getTimecodes().getTotalNumberOfParts();
		List<Part> parts = programme.getTechnical().getTimecodes().getParts().getPart();
		int numberOfParts = parts.size();

		boolean valid = totalNumberOfParts == numberOfParts;
		if (!valid) {
			statusReport.reportValidationError("TotalNumberOfParts does not match actual number of parts.");
		}
		return valid;
	}

	protected boolean validateDuration(Programme programme, NonProgrammeData nonProgrammeData, StatusReport statusReport) {
		// "Duration" is the value for file duration; must be larger than the sum of all "Part Duration" in the file.
		// The sum of all "PartDuration" in a file must be smaller than the value of "Duration" by more than 2 minutes, which is the total length of the line
		// up, clock/ident and gaps.
		boolean validDuration = true;

		// the total length of the line up, clock/ident and gaps
		final Timecode lineupTotal = Timecode.getInstance("00:02:00:00");

		Long assetDuration = nonProgrammeData.getDuration();
		Timecode assetDurationTimecode = Timecode.getInstance(assetDuration);

		List<Part> parts = programme.getTechnical().getTimecodes().getParts().getPart();
		Timecode durationOfParts = Timecode.getInstance(0L);
		for (Part part : parts) {
			String partDuration = part.getPartDuration();
			Timecode partDurationTimecode = Timecode.getInstance(partDuration);
			durationOfParts = Timecode.sum(durationOfParts, partDurationTimecode);
		}

		Timecode minimumValidDuration = Timecode.sum(durationOfParts, lineupTotal);

		if (assetDuration < minimumValidDuration.getNumberOfFrames()) {
			validDuration = false;
			statusReport.reportValidationError(String.format("The duration of parts (%s) is greater than duration of the media (%s).", minimumValidDuration,
					assetDurationTimecode));
		}

		// DMIDPP-236 validation rules
		if (parts.size() > 0) {
			Timecode mst = nonProgrammeData.getMaterialStartAsTimecode();
			Part lastPart = parts.get(parts.size() - 1);
			Timecode partDuration = Timecode.getInstance(lastPart.getPartDuration());
			Timecode partSOM = Timecode.getInstance(lastPart.getPartSOM());

			Timecode lastTimecode = Timecode.sum(partDuration, partSOM);
			float difference = Timecode.getDifferenceInSeconds(mst, lastTimecode);

			if (difference > assetDuration) {
				validDuration = false;
				statusReport
						.reportValidationError(String
								.format("The difference between 'Material Start Timecode' (%s) and end timecode of the last programme part (%s) must be smaller or equal to file Duration (%s).",
										mst, lastTimecode, minimumValidDuration, assetDurationTimecode));
			}

		}

		return validDuration;
	}

	protected boolean validateTotalProgrammeDuration(Programme programme, StatusReport statusReport) {
		// Must be equal to the sum of all "PartDuration" if programme delivered in single file;
		// must be greater than the sum of all "PartDuration" if programme delivered in multiple files.
		boolean validateTotalProgrammeDuration = true;
		Timecodes timecodes = programme.getTechnical().getTimecodes();
		String totalProgrammeDuration = timecodes.getTotalProgrammeDuration();
		Timecode totalProgrammeDurationTimecode = Timecode.getInstance(totalProgrammeDuration);

		int totalNumberOfParts = timecodes.getTotalNumberOfParts();
		int partTotal = 0;

		List<Part> parts = timecodes.getParts().getPart();
		Timecode durationOfParts = Timecode.getInstance(0L);
		for (Part part : parts) {
			String partDuration = part.getPartDuration();
			Timecode partDurationTimecode = Timecode.getInstance(partDuration);
			durationOfParts = Timecode.sum(durationOfParts, partDurationTimecode);

			partTotal = part.getPartTotal();
		}

		boolean programmeDeliveredInSingleFile = partTotal == totalNumberOfParts;
		if (programmeDeliveredInSingleFile) {
			if (totalProgrammeDurationTimecode.getNumberOfFrames().longValue() != durationOfParts.getNumberOfFrames().longValue()) {
				validateTotalProgrammeDuration = false;
				statusReport.reportValidationError("Total Programme Duration does not match duration of parts for programme delivered in a single file");
			}
		} else {
			if (totalProgrammeDurationTimecode.getNumberOfFrames().longValue() < durationOfParts.getNumberOfFrames().longValue()) {
				validateTotalProgrammeDuration = false;
				statusReport.reportValidationError("Total Programme Duration does not exceed duration of parts for programme delivered in multiple files");
			}
		}

		return validateTotalProgrammeDuration;
	}

}

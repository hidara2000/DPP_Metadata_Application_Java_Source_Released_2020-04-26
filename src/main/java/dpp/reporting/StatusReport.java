package dpp.reporting;

import java.util.ArrayList;
import java.util.List;

import dpp.bmx.CommandLineResult;
import dpp.enums.CommandResultEnum;
import dpp.enums.CommandTypeEnum;
import dpp.enums.MxfTypeEnum;
import dpp.validators.NonProgrammeData;

/**
 * Class to hold results of processing commands. The MxfFileType can be determined during extraction since if mxf2raw -i works at all we know we have at least
 * an OP1A file, if --as11 works with the dpp info then it's a dpp file but if it works without the dpp info then it's an as11 file. System errors such as file
 * not found are stored in systemErrors while any validation failures against the DPP specification and schema are reported in validationErrors
 * 
 */
public class StatusReport {
	private MxfTypeEnum mxfFileType = MxfTypeEnum.UNKNOWN;
	private CommandResultEnum commandResult = CommandResultEnum.UNKNOWN;
	private CommandTypeEnum commandType = CommandTypeEnum.UNKNOWN;

	// unexpeced messages like not being able to create a file or caught exceptions
	private final List<String> systemErrors = new ArrayList<String>();
	// validation errors either against the xsd or against the DPP spec
	private final List<String> validationErrors = new ArrayList<String>();
	// some validations only result in warnings
	private final List<String> validationWarnings = new ArrayList<String>();
	// "ERROR", "Debug", "Info", or "Warning" messages from the underlying bmx library.
	private final List<String> bmxMessages = new ArrayList<String>();
	// Data useful to the front end which is not stored in the xml file.
	private final List<String> nonProgrammeDataItems = new ArrayList<String>();

	public MxfTypeEnum getMxfFileType() {
		return mxfFileType;
	}

	public void setMxfFileType(final MxfTypeEnum mxfFileType) {
		this.mxfFileType = mxfFileType;
	}

	public void setCommandResult(final CommandResultEnum commandResult) {
		this.commandResult = commandResult;
	}

	public void reportSystemError(final String error) {
		systemErrors.add(error);
	}

	public List<String> getSystemErrors() {
		return systemErrors;
	}

	public void reportValidationError(final String error) {
		validationErrors.add(error);
	}

	public List<String> getValidationErrors() {
		return validationErrors;
	}

	public void reportValidationWarning(final String warning) {
		validationWarnings.add(warning);
	}

	public List<String> getValidationWarnings() {
		return validationWarnings;
	}

	public int getNumberOfValidationErrors() {
		return validationErrors.size();
	}

	public int getNumberOfValidationWarnings() {
		return validationWarnings.size();
	}

	public int getTotalNumberOfErrors() {
		return validationErrors.size() + systemErrors.size();
	}

	private void reportBmxMessage(final String message) {
		bmxMessages.add(message);
	}

	public List<String> getBmxMessages() {
		return bmxMessages;
	}

	/**
	 * Gets this Status Report as a list of lines to be output to either file or standard out
	 * 
	 */
	public List<String> getReportAsListOfLines() {
		List<String> report = new ArrayList<String>();
		report.add("Command type: " + commandType.value());
		if (shouldReportMxfFileType()) {
			report.add("Mxf file type: " + mxfFileType.value());
		}
		report.add("Command result: " + commandResult.value());

		if (!systemErrors.isEmpty()) {
			report.add("System errors:");
			for (String systemError : systemErrors) {
				report.add("    " + systemError);
			}
		}

		if (!nonProgrammeDataItems.isEmpty()) {
			report.add("Additional mxf file information:");
			for (String data : nonProgrammeDataItems) {
				report.add("    " + data);
			}
		}

		if (!validationErrors.isEmpty()) {
			report.add("Validation errors:");
			for (String validationError : validationErrors) {
				report.add("    " + validationError);
			}
		}

		if (!validationWarnings.isEmpty()) {
			report.add("Validation warnings:");
			for (String validationWarning : validationWarnings) {
				report.add("    " + validationWarning);
			}
		}

		if (!bmxMessages.isEmpty()) {
			report.add("BMX Messages:");
			for (String bmxMessage : bmxMessages) {
				report.add("    " + bmxMessage);
			}
		}

		return report;
	}

	private boolean shouldReportMxfFileType() {
		return (commandType == CommandTypeEnum.EXTRACT || commandType == CommandTypeEnum.GENERATE_MXF_AND_XML || commandType == CommandTypeEnum.GENERATE_SIDECAR);
	}

	/**
	 * Make a note of messages from BMX - only interested in errors or warnings so ignore debug and info messages and any genuine output which we will have
	 * processed elsewhere
	 * 
	 * We could later change this so it orders or filters by severity.
	 * 
	 */
	public void reportBmxErrorsOrWarnings(final CommandLineResult commandLineResult) {
		final List<String> stdError = commandLineResult.getStdError();
		if (stdError != null) {
			for (String message : stdError) {
				if (messageIsErrorOrWarning(message)) {
					reportBmxMessage(message);
				}
			}
		}

		final List<String> stdOut = commandLineResult.getStdOutput();
		if (stdOut != null) {
			for (String message : stdOut) {
				if (messageIsErrorOrWarning(message)) {
					reportBmxMessage(message);
				}
			}
		}

		if (!bmxMessages.isEmpty()) {
			bmxMessages.add(0, "An error has occurred while loading the file - Please make sure the media file is compliant and try again. "
					+ "If the problem persists, reinstall the application or install it on another machine.");

		}

	}

	private boolean messageIsErrorOrWarning(final String message) {
		final String trimmedUpperCaseMessage = message.trim().toUpperCase();
		return (trimmedUpperCaseMessage.startsWith("WARNING") || trimmedUpperCaseMessage.startsWith("ERROR"));
	}

	public void reportNonProgrammeData(final NonProgrammeData nonProgrammeData) {
		nonProgrammeDataItems.add("video essence type: " + nonProgrammeData.getVideoEssenceType());
		nonProgrammeDataItems.add("edit rate: " + nonProgrammeData.getEditRate());
		nonProgrammeDataItems.add("duration: " + nonProgrammeData.getDuration());
		nonProgrammeDataItems.add("total number of audio channels: " + nonProgrammeData.getTotalNumberOfAudioChannels());
		nonProgrammeDataItems.add("HD: " + nonProgrammeData.isHighDefinition());
		nonProgrammeDataItems.add("Material start timecode: " + nonProgrammeData.getMaterialStartTimecode());
	}

	public void setCommandType(CommandTypeEnum commandType) {
		this.commandType = commandType;
	}

	public boolean isSystemError() {
		return !getSystemErrors().isEmpty();
	}

}

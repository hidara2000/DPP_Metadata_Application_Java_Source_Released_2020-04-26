package dpp;

import java.io.File;

import dpp.bmx.CommandLine;
import dpp.enums.CommandTypeEnum;
import dpp.reporting.StatusReport;
import dpp.util.FileUtils;

public class FilesChecker {

	public static boolean checkHaveAllRequiredFiles(CommandLine commandLine, StatusReport statusReport) {

		String xmlFileName = commandLine.getXmlFileName();
		String mxfFileName = commandLine.getMxfFileName();
		String binPath = commandLine.getBinPath();
		CommandTypeEnum commandType = commandLine.getCommandType();

		boolean haveAllRequiredFiles;
		switch (commandType) {
		case EXTRACT:
			haveAllRequiredFiles = checkHaveRequiredFiles(xmlFileName, mxfFileName, false, true, statusReport);
			break;
		case GENERATE_MXF_AND_XML:
			haveAllRequiredFiles = checkHaveRequiredFiles(xmlFileName, mxfFileName, true, true, statusReport);
			break;
		case VALIDATE_XML:
			haveAllRequiredFiles = checkHaveRequiredFiles(xmlFileName, mxfFileName, true, false, statusReport);
			break;
		case VALIDATE_DPP:
			haveAllRequiredFiles = checkHaveRequiredFiles(xmlFileName, mxfFileName, true, true, statusReport);
			break;
		case VALIDATE_EDITORIAL:
			haveAllRequiredFiles = checkHaveRequiredFiles(xmlFileName, mxfFileName, true, false, statusReport);
			break;
		case VALIDATE_VIDEO:
			haveAllRequiredFiles = checkHaveRequiredFiles(xmlFileName, mxfFileName, true, true, statusReport);
			break;
		case VALIDATE_TIMECODE:
			haveAllRequiredFiles = checkHaveRequiredFiles(xmlFileName, mxfFileName, true, true, statusReport);
			break;
		case VALIDATE_AUDIO:
			haveAllRequiredFiles = checkHaveRequiredFiles(xmlFileName, mxfFileName, true, true, statusReport);
			break;
		case VALIDATE_OTHERS:
			haveAllRequiredFiles = checkHaveRequiredFiles(xmlFileName, mxfFileName, true, false, statusReport);
			break;
		case GENERATE_SIDECAR:
			haveAllRequiredFiles = checkHaveRequiredFiles(xmlFileName, mxfFileName, false, true, statusReport);
			break;

		case UNKNOWN:
		default:
			haveAllRequiredFiles = false;
			break;
		}

		if (binPath != null && binPath.length() > 0) {
			File binDir = new File(binPath);
			if (!binDir.isDirectory()) {
				statusReport.reportValidationError(String.format("'%s' is not accessible or is not a directory", binPath));
				haveAllRequiredFiles = false;
			}
		}

		return haveAllRequiredFiles;
	}

	private static boolean checkHaveRequiredFiles(String xmlFileName, String mxfFileName, boolean requireXmlFile, boolean requireMxfFile,
			StatusReport statusReport) {
		boolean haveRequiredFiles = true;
		boolean haveXmlFile = FileUtils.doesFileExist(xmlFileName);
		boolean haveMxfFile = FileUtils.doesFileExist(mxfFileName);

		if (requireXmlFile && !haveXmlFile) {
			statusReport.reportValidationError("Missing xml file " + xmlFileName);
			haveRequiredFiles = false;
		}
		if (requireMxfFile && !haveMxfFile) {
			statusReport.reportValidationError("Missing mxf file " + mxfFileName);
			haveRequiredFiles = false;
		}
		return haveRequiredFiles;
	}

}

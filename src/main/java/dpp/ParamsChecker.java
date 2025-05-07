package dpp;

import dpp.bmx.CommandLine;
import dpp.enums.CommandTypeEnum;
import dpp.reporting.StatusReport;

public class ParamsChecker {
	public static boolean checkHaveAllRequiredParams(CommandLine commandLine, StatusReport statusReport) {

		String xmlFileName = commandLine.getXmlFileName();
		String mxfFileName = commandLine.getMxfFileName();
		String transwrappedMxfFileName = commandLine.getTranswrappedMxfFileName();
		String sidecarXmlFileName = commandLine.getSidecarXmlFileName();
		CommandTypeEnum commandType = commandLine.getCommandType();

		boolean haveXmlFileName = xmlFileName != null && !xmlFileName.isEmpty();
		boolean haveMxfFileName = mxfFileName != null && !mxfFileName.isEmpty();
		boolean haveTranswrappedMxfFileName = transwrappedMxfFileName != null && !transwrappedMxfFileName.isEmpty();
		boolean haveSidecarXmlFileName = sidecarXmlFileName != null && !sidecarXmlFileName.isEmpty();

		ParamsRequirement paramsRequirement = new ParamsRequirement(haveXmlFileName, haveMxfFileName, haveTranswrappedMxfFileName, haveSidecarXmlFileName);

		boolean haveAllRequiredParams = false;
		switch (commandType) {
		case EXTRACT:
			paramsRequirement.setRequireXmlFileName(true).setRequireMxfFileName(true);
			haveAllRequiredParams = checkParams(paramsRequirement, statusReport);
			break;
		case GENERATE_MXF_AND_XML:
			paramsRequirement.setRequireXmlFileName(true).setRequireMxfFileName(true).setRequireSidecarXmlFileName(true)
					.setRequireTranswrappedMxfFileName(true);
			haveAllRequiredParams = checkParams(paramsRequirement, statusReport);
			break;
		case VALIDATE_XML:
			paramsRequirement.setRequireXmlFileName(true);
			haveAllRequiredParams = checkParams(paramsRequirement, statusReport);
			break;
		case VALIDATE_DPP:
			paramsRequirement.setRequireXmlFileName(true).setRequireMxfFileName(true);
			haveAllRequiredParams = checkParams(paramsRequirement, statusReport);
			break;
		case VALIDATE_EDITORIAL:
			paramsRequirement.setRequireXmlFileName(true);
			haveAllRequiredParams = checkParams(paramsRequirement, statusReport);
			break;
		case VALIDATE_VIDEO:
			paramsRequirement.setRequireXmlFileName(true).setRequireMxfFileName(true);
			haveAllRequiredParams = checkParams(paramsRequirement, statusReport);
			break;
		case VALIDATE_TIMECODE:
			paramsRequirement.setRequireXmlFileName(true).setRequireMxfFileName(true);
			haveAllRequiredParams = checkParams(paramsRequirement, statusReport);
			break;
		case VALIDATE_AUDIO:
			paramsRequirement.setRequireXmlFileName(true).setRequireMxfFileName(true);
			haveAllRequiredParams = checkParams(paramsRequirement, statusReport);
			break;
		case VALIDATE_OTHERS:
			paramsRequirement.setRequireXmlFileName(true);
			haveAllRequiredParams = checkParams(paramsRequirement, statusReport);
			break;
		case GENERATE_SIDECAR:
			paramsRequirement.setRequireSidecarXmlFileName(true).setRequireMxfFileName(true);
			haveAllRequiredParams = checkParams(paramsRequirement, statusReport);
			break;

		case UNKNOWN:
		default:
			haveAllRequiredParams = false;
			break;

		}

		return haveAllRequiredParams;
	}

	private static boolean checkParams(ParamsRequirement params, StatusReport statusReport) {
		boolean paramsOk = true;

		if (params.isRequireMxfFileName() && !params.isHaveMxfFileName()) {
			paramsOk = false;
			statusReport.reportValidationError("Missing parameter : mxf filename ");
		}

		if (params.isRequireXmlFileName() && !params.isHaveXmlFileName()) {
			paramsOk = false;
			statusReport.reportValidationError("Missing parameter : xml filename ");
		}

		if (params.isRequireSidecarXmlFileName() && !params.isHaveSidecarXmlFileName()) {
			paramsOk = false;
			statusReport.reportValidationError("Missing parameter : sidecar filename ");
		}

		if (params.isRequireTranswrappedMxfFileName() && !params.isHaveTranswrappedMxfFileName()) {
			paramsOk = false;
			statusReport.reportValidationError("Missing parameter : transwrapped filename ");
		}

		return paramsOk;
	}

	private static class ParamsRequirement {
		private final boolean haveXmlFileName;
		private final boolean haveMxfFileName;
		private final boolean haveTranswrappedMxfFileName;
		private final boolean haveSidecarXmlFileName;

		private boolean requireXmlFileName = false;
		private boolean requireMxfFileName = false;
		private boolean requireTranswrappedMxfFileName = false;
		private boolean requireSidecarXmlFileName = false;

		public ParamsRequirement(boolean haveXmlFileName, boolean haveMxfFileName, boolean haveTranswrappedMxfFileName, boolean haveSidecarXmlFileName) {
			super();
			this.haveXmlFileName = haveXmlFileName;
			this.haveMxfFileName = haveMxfFileName;
			this.haveTranswrappedMxfFileName = haveTranswrappedMxfFileName;
			this.haveSidecarXmlFileName = haveSidecarXmlFileName;
		}

		public boolean isHaveXmlFileName() {
			return haveXmlFileName;
		}

		public boolean isHaveMxfFileName() {
			return haveMxfFileName;
		}

		public boolean isHaveTranswrappedMxfFileName() {
			return haveTranswrappedMxfFileName;
		}

		public boolean isHaveSidecarXmlFileName() {
			return haveSidecarXmlFileName;
		}

		public boolean isRequireXmlFileName() {
			return requireXmlFileName;
		}

		public ParamsRequirement setRequireXmlFileName(boolean requireXmlFileName) {
			this.requireXmlFileName = requireXmlFileName;
			return this;
		}

		public boolean isRequireMxfFileName() {
			return requireMxfFileName;
		}

		public ParamsRequirement setRequireMxfFileName(boolean requireMxfFileName) {
			this.requireMxfFileName = requireMxfFileName;
			return this;
		}

		public boolean isRequireTranswrappedMxfFileName() {
			return requireTranswrappedMxfFileName;
		}

		public ParamsRequirement setRequireTranswrappedMxfFileName(boolean requireTranswrappedMxfFileName) {
			this.requireTranswrappedMxfFileName = requireTranswrappedMxfFileName;
			return this;
		}

		public boolean isRequireSidecarXmlFileName() {
			return requireSidecarXmlFileName;
		}

		public ParamsRequirement setRequireSidecarXmlFileName(boolean requireSidecarXmlFileName) {
			this.requireSidecarXmlFileName = requireSidecarXmlFileName;
			return this;
		}

	}

}

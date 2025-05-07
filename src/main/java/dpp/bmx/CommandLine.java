package dpp.bmx;

import java.io.File;

import dpp.enums.CommandTypeEnum;

public class CommandLine {

	private final CommandTypeEnum commandType;
	private final String xmlFileName;
	private final String mxfFileName;
	private final String statusReportFileName;
	private final String transwrappedMxfFileName;
	private final String sidecarXmlFileName;
	private final String binPath;
	private final boolean overwrite;
	private final boolean debug;

	public CommandLine(CommandTypeEnum commandType, String xmlFileName, String mxfFileName, String statusReportFileName, String transwrappedMxfFileName,
			String sidecarXmlFileName, String binPath, boolean overwrite, boolean debug) {
		super();
		this.commandType = commandType;
		this.xmlFileName = xmlFileName;
		this.mxfFileName = mxfFileName;
		this.statusReportFileName = statusReportFileName;
		this.transwrappedMxfFileName = transwrappedMxfFileName;
		this.sidecarXmlFileName = sidecarXmlFileName;
		this.binPath = binPath;
		this.overwrite = overwrite;
		this.debug = debug;
	}

	public CommandTypeEnum getCommandType() {
		return commandType;
	}

	public String getXmlFileName() {
		return xmlFileName;
	}

	public String getMxfFileName() {
		return mxfFileName;
	}

	public String getStatusReportFileName() {
		return statusReportFileName;
	}

	public File getStatusReportDirectory() {
		return new File(statusReportFileName).getParentFile();
	}

	public String getTranswrappedMxfFileName() {
		return transwrappedMxfFileName;
	}

	public String getSidecarXmlFileName() {
		return sidecarXmlFileName;
	}

	public boolean isOverwrite() {
		return overwrite;
	}

	public String getBinPath() {
		return binPath;
	}

	public boolean isDebug() {
		return debug;
	}

	@Override
	public String toString() {
		return commandType + ", xml file name = " + xmlFileName + ", mxfFileName = " + mxfFileName + ", statusReportFileName = " + statusReportFileName
				+ ", overwrite = " + overwrite;
	}
}

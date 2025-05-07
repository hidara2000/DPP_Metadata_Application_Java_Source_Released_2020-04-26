package dpp.bmx.filebuilders;

import java.util.List;

import dpp.bmx.CommandLineResult;

public class CommandLineResultParser {
	private static final String INFO_OUTPUT_FILE_MD5 = "Info: Output file MD5:";
	private static final String INFO_FILE_MD5 = "Info: File MD5:";
	private static final String MD5 = "MD5:";

	private CommandLineResult commandLineResult = null;

	public CommandLineResultParser(final CommandLineResult commandLineResult) {
		this.commandLineResult = commandLineResult;
	}

	// This is when we are doing the full transwrap and the md5 is output as part of that.
	public String getMd5() {
		String md5 = "";
		final List<String> stdOutput = commandLineResult.getStdOutput();
		for (String outputLine : stdOutput) {
			if (outputLine.trim().startsWith(INFO_OUTPUT_FILE_MD5)) {
				final int stripOffStartOfLineIndex = outputLine.trim().indexOf(MD5) + MD5.length();
				md5 = outputLine.trim().substring(stripOffStartOfLineIndex).trim();
			}
		}
		return md5;
	}

	// This is when we are just running the --file-md5 option and get an ouput similar to the following
	// Info: File MD5: AndyTestWinScripts\prev_as11_d10_stereo_pcm.mxf 2038643e30632776ef5cad3f8ce64c26
	public String getMd5ForFile(final String mxfFileName) {
		String md5 = "";
		final List<String> stdOutput = commandLineResult.getStdOutput();
		for (String outputLine : stdOutput) {
			if (outputLine.trim().startsWith(INFO_FILE_MD5)) {
				final int stripOffStartOfLineIndex = outputLine.trim().indexOf(mxfFileName) + mxfFileName.length();
				md5 = outputLine.trim().substring(stripOffStartOfLineIndex).trim();
			}
		}
		return md5;
	}
}

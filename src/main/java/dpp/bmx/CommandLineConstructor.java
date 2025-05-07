package dpp.bmx;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import dpp.schema.Programme;
import dpp.util.FileUtils;

public class CommandLineConstructor {

	private final String binPath;

	public CommandLineConstructor(String binPath) {

		this.binPath = binPath;
	}

	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(CommandLineConstructor.class);

	public List<String> createTranswrapCommandLine(Programme programme, String xmlFileName, String mxfFileName, String transwrappedMxfFileName) {
		// e.g. bmxtranswrap -t as11op1a -o mxfFileNametranswrappedfile.mxf --dm-file as11 as11_core_framework_sd.txt --dm-file dpp ukdpp_framework_sd.txt --seg
		// as11_segmentation_framework.txt mxfNotDpp.mxf
		String as11SegmentationFileName = FileUtils.getAs11SegmentationFileNameFromXmlFileName(xmlFileName);
		String as11CoreFileName = FileUtils.getAs11CoreFileNameFromXmlFileName(xmlFileName);
		String ukDppFileName = FileUtils.getUkDppFileNameFromXmlFileName(xmlFileName);

		int afd = getAFD(programme);

		List<String> commands = new ArrayList<String>();
		commands.add(addPathIfPresent("bmxtranswrap"));
		commands.add("-t");
		commands.add("as11op1a");
		commands.add("--file-md5");
		commands.add("-o");
		commands.add(transwrappedMxfFileName);

		commands.add("--dm-file");
		commands.add("as11");
		commands.add(as11CoreFileName);
		commands.add("--dm-file");
		commands.add("dpp");
		commands.add(ukDppFileName);
		commands.add("--seg");
		commands.add(as11SegmentationFileName);
		if (afd > 0) {
			commands.add("--afd");
			commands.add(String.valueOf(afd));
		}
		commands.add(mxfFileName);

		return commands;
	}

	public List<String> createGeneralInfoCommandLine(String mxfFileName) {
		List<String> commands = new ArrayList<String>();
		commands.add(addPathIfPresent("mxf2raw"));
		commands.add("-i");
		commands.add(mxfFileName);
		return commands;
	}

	public List<String> createAs11InfoCommandLine(String mxfFileName) {
		List<String> commands = new ArrayList<String>();
		commands.add(addPathIfPresent("mxf2raw"));
		commands.add("--as11");
		commands.add(mxfFileName);
		return commands;
	}

	public List<String> createMd5InfoCommandLine(String mxfFileName) {
		List<String> commands = new ArrayList<String>();
		commands.add(addPathIfPresent("mxf2raw"));
		commands.add("--file-md5-only");
		commands.add(mxfFileName);
		return commands;
	}

	private int getAFD(Programme programme) {
		BigInteger afd = null;
		if (programme != null && programme.getTechnical() != null && programme.getTechnical().getVideo() != null) {
			afd = programme.getTechnical().getVideo().getAFD();
		}
		return afd != null ? afd.intValue() : 0;
	}

	/**
	 * Prepend a path to the binaries if it has been passed in on the command line.
	 * 
	 * @param commandLine
	 * @return
	 */
	private String addPathIfPresent(String command) {

		StringBuilder commandLine = new StringBuilder();
		if (binPath != null && binPath.length() > 0) {
			commandLine.append(binPath);

			if (!binPath.endsWith(File.separator)) {
				commandLine.append(File.separator);
			}
		}

		commandLine.append(command);

		return commandLine.toString();
	}
}

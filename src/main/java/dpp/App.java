package dpp;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

import dpp.bmx.BmxLibraryWrapper;
import dpp.bmx.CommandLine;
import dpp.bmx.CommandLineConstructor;
import dpp.bmx.CommandLineExec;
import dpp.bmx.CommandLineResult;
import dpp.bmx.filebuilders.CommandLineResultParser;
import dpp.enums.CommandResultEnum;
import dpp.enums.CommandTypeEnum;
import dpp.reporting.AppDppVersion;
import dpp.reporting.AppEula;
import dpp.reporting.AppHelp;
import dpp.reporting.FileSizeProgress;
import dpp.reporting.Md5Progress;
import dpp.reporting.Progress;
import dpp.reporting.StatusReport;
import dpp.schema.Programme;
import dpp.util.FileUtils;
import dpp.validators.NonProgrammeData;
import dpp.validators.ValidatorFacade;
import dpp.xmlmarshalling.XmlMarshalling;
import model.Config;

/**
 * Main entry to DPP java application. Usage will be - see @Code displayHelpIfRequested This is the only class which should have any system.out calls, every
 * other class should be writing to log files, error files or generating xml and mxf transwrapped files.
 * 
 */
public class App {
	private static final Logger LOGGER = Logger.getLogger(App.class);
	private static final String EULA_FILENAME = "eula.txt";
	private static final String CONFIG_JAVA_FILENAME = "java-config.xml";
	private final static String COMMAND_PREFIX = "-c";
	private final static String MXF_FILE_PREFIX = "-f";
	private final static String XML_FILE_PREFIX = "-x";
	private final static String TRANSWRAPPED_MXF_FILE_PREFIX = "-t";
	private final static String SIDECAR_XML_FILE_PREFIX = "-s";
	private final static String REPORT_FILE_PREFIX = "-r";
	private final static String BIN_PATH_PREFIX = "-b";
	private final static String OVERWRITE_FLAG = "-o";
	private final static String DEBUG_FLAG = "-d";

	public static final String UTF_8_ENCODING_NAME = "UTF-8";

	private static final BmxLibraryWrapper BMX_LIBRARY_WRAPPER = new BmxLibraryWrapper();
	private static final XmlMarshalling XML_MARSHALLING = new XmlMarshalling();
	private static final ValidatorFacade VALIDATOR_FACADE = new ValidatorFacade();
	private static final ConfigFactory CONFIG_FACTORY = new ConfigFactory();

	public static void main(String[] args) {
		List<String> listOfParams = Arrays.asList(args);

		logCommandLine(listOfParams);
		if (displayHelpIfRequested(listOfParams)) {
			return;
		}

		if (displayEulaIfRequested(listOfParams)) {
			return;
		}

		if (displayDppVersionIfRequested(listOfParams)) {
			return;
		}

		if (displayDefaultShimIfRequested(listOfParams)) {
			return;
		}

		CommandTypeEnum commandType = getCommandType(listOfParams);
		String xmlFileName = getFileName(listOfParams, XML_FILE_PREFIX);
		String mxfFileName = getFileName(listOfParams, MXF_FILE_PREFIX);
		String transwrappedMxfFileName = getFileName(listOfParams, TRANSWRAPPED_MXF_FILE_PREFIX);
		String sidecarXmlFileName = getFileName(listOfParams, SIDECAR_XML_FILE_PREFIX);
		String statusReportFileName = getFileName(listOfParams, REPORT_FILE_PREFIX);
		String binPath = getFileName(listOfParams, BIN_PATH_PREFIX);

		boolean overwrite = getFlagSpecified(listOfParams, OVERWRITE_FLAG);
		boolean debug = getFlagSpecified(listOfParams, DEBUG_FLAG);

		CommandLine commandLine = new CommandLine(commandType, xmlFileName, mxfFileName, statusReportFileName, transwrappedMxfFileName, sidecarXmlFileName,
				binPath, overwrite, debug);
		processCommand(commandLine);

	}

	protected static boolean processCommand(CommandLine commandLine) {

		boolean commandProcessedOK = false;
		boolean allRequiredFilesAndParamsSupplied = true;
		LOGGER.debug(String.format("Command is %s", commandLine));
		StatusReport statusReport = new StatusReport();
		CommandTypeEnum commandType = commandLine.getCommandType();
		statusReport.setCommandType(commandType);

		if (!ParamsChecker.checkHaveAllRequiredParams(commandLine, statusReport)) {
			allRequiredFilesAndParamsSupplied = false;
		}

		if (!FilesChecker.checkHaveAllRequiredFiles(commandLine, statusReport)) {
			allRequiredFilesAndParamsSupplied = false;
		}

		if (allRequiredFilesAndParamsSupplied) {
			switch (commandType) {
			case EXTRACT:
				commandProcessedOK = processExtract(commandLine, statusReport);
				break;

			case GENERATE_MXF_AND_XML:
				commandProcessedOK = processGenerateTranswrappedMxfAndSidecar(commandLine, statusReport);
				break;

			case GENERATE_SIDECAR:
				commandProcessedOK = processGenerateSidecar(commandLine, statusReport);
				break;

			case VALIDATE_XML:
				commandProcessedOK = processValidateXml(commandLine, statusReport);
				break;

			case VALIDATE_DPP:
				commandProcessedOK = processValidateDpp(commandLine, statusReport);
				break;

			case VALIDATE_EDITORIAL:
				commandProcessedOK = processValidateEditorial(commandLine, statusReport);
				break;

			case VALIDATE_VIDEO:
				commandProcessedOK = processValidateVideo(commandLine, statusReport);
				break;

			case VALIDATE_TIMECODE:
				commandProcessedOK = processValidateTimecode(commandLine, statusReport);
				break;

			case VALIDATE_AUDIO:
				commandProcessedOK = processValidateAudio(commandLine, statusReport);
				break;

			case VALIDATE_OTHERS:
				commandProcessedOK = processValidateOthers(commandLine, statusReport);
				break;

			case UNKNOWN:
			default:
				System.out.println("Unknown command -c");
				AppHelp.displayHelp();
				break;
			}
		}

		if (commandProcessedOK) {
			statusReport.setCommandResult(CommandResultEnum.SUCCESS);
		} else {
			statusReport.setCommandResult(CommandResultEnum.FAILURE);
		}

		String statusReportFileName = commandLine.getStatusReportFileName();
		outputStatusReport(statusReportFileName, statusReport);

		return commandProcessedOK;

	}

	private static void outputStatusReport(String statusReportFileName, StatusReport statusReport) {
		if (statusReportFileName == null || statusReportFileName.isEmpty()) {
			FileUtils.logToStandardOutFromListOfLine(statusReport.getReportAsListOfLines());
		} else {
			boolean createdStatusReportFile = FileUtils.createTextFileFromListOfLines(statusReportFileName, statusReport.getReportAsListOfLines());
			if (!createdStatusReportFile) {
				System.out.println("Could not create status report file, outputting to standard out.");
				FileUtils.logToStandardOutFromListOfLine(statusReport.getReportAsListOfLines());
			}
		}
	}

	private static boolean processGenerateSidecar(CommandLine commandLine, StatusReport statusReport) {

		String mxfFileName = commandLine.getMxfFileName();
		String sidecarXmlFileName = commandLine.getSidecarXmlFileName();
		String binPath = commandLine.getBinPath();
		File workingDirectory = commandLine.getStatusReportDirectory();

		boolean commandProcessedOK = false;
		CommandLineResult commandLineResultGeneralInfo = extractGeneralInfo(mxfFileName, binPath, workingDirectory);
		statusReport.reportBmxErrorsOrWarnings(commandLineResultGeneralInfo);
		boolean generalInfoSuccess = commandLineResultGeneralInfo.isSuccess();

		if (generalInfoSuccess) {
			CommandLineResult commandLineResultAS11Info = extractAS11Info(mxfFileName, binPath, workingDirectory);
			statusReport.reportBmxErrorsOrWarnings(commandLineResultAS11Info);
			boolean as11success = commandLineResultAS11Info.isSuccess();

			if (as11success) {
				Programme programme = BMX_LIBRARY_WRAPPER.createProgrammeFromExtractInfo(commandLineResultGeneralInfo, commandLineResultAS11Info, statusReport);
				NonProgrammeData nonProgrammeData = BMX_LIBRARY_WRAPPER.createNonProgrammeDataFromExtractInfo(commandLineResultGeneralInfo,
						commandLineResultAS11Info, statusReport);

				statusReport.reportNonProgrammeData(nonProgrammeData);
				Config config = getConfig(statusReport);
				if (config != null) {
					boolean validShimName = validateShimName(programme, nonProgrammeData, statusReport, config);

					if (!validShimName) {
						String error = "This file's Shim Name version is not supported by this application; a Sidecar XML file cannot be generated for it.";
						LOGGER.error(error);
						statusReport.reportSystemError(error);
					} else {

						boolean validDppFile = VALIDATOR_FACADE.validateProgrammeAgainstDppSpecification(programme, nonProgrammeData, statusReport);
						if (validDppFile) {
							Thread md5ProgressThread = startMd5ProgressThread(mxfFileName, binPath);
							CommandLineResult processExtractMd5CommandLineResult = extractMd5(mxfFileName, binPath, workingDirectory);
							statusReport.reportBmxErrorsOrWarnings(processExtractMd5CommandLineResult);

							CommandLineResultParser commandLineResultParser = new CommandLineResultParser(processExtractMd5CommandLineResult);
							String md5 = commandLineResultParser.getMd5ForFile(mxfFileName);
							commandProcessedOK = generateXmlSidecar(programme, mxfFileName, sidecarXmlFileName, md5, statusReport);
							md5ProgressThread.interrupt();
							allowProgressThreadToCleanup();
						}
					}
				}
			}
		}
		return commandProcessedOK;
	}

	private static void allowProgressThreadToCleanup() {
		try {
			// Allow the other thread time to clean up before we move on.
			Thread.sleep(1);
		} catch (InterruptedException e) {
			// we don't care, just drop out.
		}
	}

	private static boolean processExtract(CommandLine commandLine, StatusReport statusReport) {
		boolean commandProcessedOK = false;

		String xmlFileName = commandLine.getXmlFileName();
		String mxfFileName = commandLine.getMxfFileName();
		String binPath = commandLine.getBinPath();
		File workingDirectory = commandLine.getStatusReportDirectory();

		// Get general file info
		CommandLineResult commandLineResultGeneralInfo = extractGeneralInfo(mxfFileName, binPath, workingDirectory);
		statusReport.reportBmxErrorsOrWarnings(commandLineResultGeneralInfo);
		boolean generalInfoSuccess = commandLineResultGeneralInfo.isSuccess();
		if (generalInfoSuccess) {
			// Get as11 specific data - only works on as11 compliant files.
			CommandLineResult commandLineResultAS11Info = extractAS11Info(mxfFileName, binPath, workingDirectory);
			statusReport.reportBmxErrorsOrWarnings(commandLineResultAS11Info);

			Programme programme = BMX_LIBRARY_WRAPPER.createProgrammeFromExtractInfo(commandLineResultGeneralInfo, commandLineResultAS11Info, statusReport);
			if (!statusReport.isSystemError()) {
				NonProgrammeData nonProgrammeData = BMX_LIBRARY_WRAPPER.createNonProgrammeDataFromExtractInfo(commandLineResultGeneralInfo,
						commandLineResultAS11Info, statusReport);

				statusReport.reportNonProgrammeData(nonProgrammeData);

				// validate the generated programme here though we won't have user supplied fields.
				boolean passedAllRelevantValidation = VALIDATOR_FACADE.validateStructuralMetadata(programme, nonProgrammeData, statusReport);
				LOGGER.debug("Extract, passedAllRelevantValidation = " + passedAllRelevantValidation);
				commandProcessedOK = XML_MARSHALLING.generateXmlFileFromProgramme(programme, xmlFileName, statusReport);
			}
		}
		return commandProcessedOK;
	}

	protected static boolean processGenerateTranswrappedMxfAndSidecar(CommandLine commandLine, StatusReport statusReport) {

		String xmlFileName = commandLine.getXmlFileName();
		String mxfFileName = commandLine.getMxfFileName();
		String transwrappedMxfFileName = commandLine.getTranswrappedMxfFileName();
		String sidecarXmlFileName = commandLine.getSidecarXmlFileName();
		String binPath = commandLine.getBinPath();
		boolean overwrite = commandLine.isOverwrite();

		boolean processedGenerateTranswrappedMxfAndSidecar = false;
		if (!canCreateFileOnFileSystem(mxfFileName, transwrappedMxfFileName, statusReport, overwrite)) {
			return false;
		}
		Programme programmeFromXmlFile = XML_MARSHALLING.getProgrammeFromXmlFile(xmlFileName, statusReport);
		NonProgrammeData nonProgrammeData = getNonProgrammeDataForMxf(commandLine);

		statusReport.reportNonProgrammeData(nonProgrammeData);

		Config config = getConfig(statusReport);
		if (config != null) {
			String appShimName = getAppShimName(nonProgrammeData, config);

			boolean createdAllTextFiles = BMX_LIBRARY_WRAPPER.createBmxTextInputFiles(programmeFromXmlFile, xmlFileName, statusReport, appShimName);

			if (createdAllTextFiles) {
				// validate the programme here including user supplied fields.

				boolean validShimName = validateShimName(programmeFromXmlFile, nonProgrammeData, statusReport, config);

				if (!validShimName) {
					String error = "This file's Shim Name version is not supported by this application; transwrap cannot continue.";
					LOGGER.error(error);
					statusReport.reportSystemError(error);
				} else {
					boolean validFull = VALIDATOR_FACADE.validateFull(xmlFileName, programmeFromXmlFile, nonProgrammeData, statusReport);
					if (validFull) {
						Thread fileSizeProgressThread = startFileSizeProgressThread(mxfFileName, transwrappedMxfFileName);

						CommandLineResult processGenerateMxfCommandLineResult = generateMxf(programmeFromXmlFile, xmlFileName, mxfFileName,
								transwrappedMxfFileName, sidecarXmlFileName, binPath, commandLine.getStatusReportDirectory());
						statusReport.reportBmxErrorsOrWarnings(processGenerateMxfCommandLineResult);

						if (processGenerateMxfCommandLineResult.isSuccess()) {
							CommandLineResultParser commandLineResultParser = new CommandLineResultParser(processGenerateMxfCommandLineResult);
							String md5 = commandLineResultParser.getMd5();

							boolean generatedXmlSidecarOk = generateXmlSidecar(programmeFromXmlFile, transwrappedMxfFileName, sidecarXmlFileName, md5,
									statusReport);
							if (generatedXmlSidecarOk) {
								processedGenerateTranswrappedMxfAndSidecar = true;
							} else {
								statusReport.reportValidationError("Failed to generate xml sidecar file.");
							}
						} else {
							LOGGER.error(String.format("Failed to transwrap %s; received status code %d from bmxtranswrap", mxfFileName,
									processGenerateMxfCommandLineResult.getExitVal()));
						}

						fileSizeProgressThread.interrupt();
						allowProgressThreadToCleanup();
					}
				}

				if (commandLine.isDebug()) {
					LOGGER.debug("Running in debug mode ('-d'); not cleaning up text files");
				} else {
					LOGGER.debug("Cleaning up text files; specify '-d' on command line to skip this step");
					BMX_LIBRARY_WRAPPER.removeBmxTextInputFiles(xmlFileName, statusReport);
				}

			} else {
				LOGGER.error("Failed to create input files for transwrapping, aborting generate command. ");
				statusReport.reportSystemError("Failed to create input files for transwrapping, aborting command.");
			}
		}
		return processedGenerateTranswrappedMxfAndSidecar;
	}

	private static String getAppShimName(NonProgrammeData nonProgrammeData, Config config) {
		String appShimName;
		boolean hd = nonProgrammeData.isHighDefinition();
		if (hd) {
			appShimName = config.getHdShimName();
		} else {
			appShimName = config.getSdShimName();
		}
		return appShimName;
	}

	private static boolean validateShimName(Programme programme, NonProgrammeData nonProgrammeData, StatusReport statusReport, Config config) {

		String shimVersion = config.getShimVersion();
		return VALIDATOR_FACADE.validateShimName(programme, nonProgrammeData, shimVersion, statusReport);
	}

	private static Config getConfig(StatusReport statusReport) {

		Config config = null;
		try {
			config = CONFIG_FACTORY.getConfig(CONFIG_JAVA_FILENAME);
		} catch (DocumentException e) {
			String error = String.format("Exception trying to load %s", CONFIG_JAVA_FILENAME);
			LOGGER.error(error, e);
			statusReport.reportSystemError(error);
		}

		return config;
	}

	private static boolean canCreateFileOnFileSystem(String mxfFileName, String transwrappedMxfFileName, StatusReport statusReport, boolean overwrite) {
		boolean okToCreateFile = true;
		if (!checkTranswrappedFileDoesNotExistOrOverwriteSpecified(transwrappedMxfFileName, overwrite)) {
			statusReport.reportSystemError("Transwrapped file already exists and overwrite option was not specified");
			okToCreateFile = false;
		}
		if (!FileUtils.checkHaveWritePermission(transwrappedMxfFileName)) {
			statusReport.reportSystemError("Do not have write permissions to create this file.");
			okToCreateFile = false;
		}
		if (!FileUtils.checkSufficientDiskSpace(mxfFileName, transwrappedMxfFileName)) {
			statusReport.reportSystemError("Insufficient disk space to transwrap this file.");
			okToCreateFile = false;
		}

		return okToCreateFile;

	}

	private static Thread startFileSizeProgressThread(String mxfFileName, String transwrappedMxfFileName) {
		long approximateExpectedTranswrappedFileSize = FileUtils.getFileSize(mxfFileName);
		Thread progressThread = new Thread(new FileSizeProgress(transwrappedMxfFileName, approximateExpectedTranswrappedFileSize));
		progressThread.start();
		return progressThread;
	}

	private static Thread startMd5ProgressThread(String mxfFileName, String binPath) {
		long approximateExpectedTranswrappedFileSize = FileUtils.getFileSize(mxfFileName);
		Thread progressThread = new Thread(new Md5Progress(approximateExpectedTranswrappedFileSize, binPath));
		progressThread.start();
		return progressThread;
	}

	private static boolean processValidateOthers(CommandLine commandLine, StatusReport statusReport) {

		String xmlFileName = commandLine.getXmlFileName();

		Programme programmeFromXmlFile = XML_MARSHALLING.getProgrammeFromXmlFile(xmlFileName, statusReport);

		return VALIDATOR_FACADE.validateOthers(programmeFromXmlFile, statusReport);
	}

	private static boolean processValidateTimecode(CommandLine commandLine, StatusReport statusReport) {

		String xmlFileName = commandLine.getXmlFileName();

		Programme programme = XML_MARSHALLING.getProgrammeFromXmlFile(xmlFileName, statusReport);
		NonProgrammeData nonProgrammeData = getNonProgrammeDataForMxf(commandLine);
		statusReport.reportNonProgrammeData(nonProgrammeData);
		return VALIDATOR_FACADE.validateTimecode(programme, nonProgrammeData, statusReport);
	}

	private static boolean processValidateVideo(CommandLine commandLine, StatusReport statusReport) {

		String xmlFileName = commandLine.getXmlFileName();

		Programme programme = XML_MARSHALLING.getProgrammeFromXmlFile(xmlFileName, statusReport);
		NonProgrammeData nonProgrammeData = getNonProgrammeDataForMxf(commandLine);
		statusReport.reportNonProgrammeData(nonProgrammeData);
		return VALIDATOR_FACADE.validateVideo(programme, nonProgrammeData, statusReport);
	}

	private static boolean processValidateEditorial(CommandLine commandLine, StatusReport statusReport) {

		String xmlFileName = commandLine.getXmlFileName();

		Programme programme = XML_MARSHALLING.getProgrammeFromXmlFile(xmlFileName, statusReport);
		return VALIDATOR_FACADE.validateEditorial(programme, statusReport);
	}

	private static boolean processValidateDpp(CommandLine commandLine, StatusReport statusReport) {

		String xmlFileName = commandLine.getXmlFileName();

		Progress.reportProgress(1, false);
		Programme programme = XML_MARSHALLING.getProgrammeFromXmlFile(xmlFileName, statusReport);
		Progress.reportProgress(10, false);
		NonProgrammeData nonProgrammeData = getNonProgrammeDataForMxf(commandLine);
		Progress.reportProgress(20, false);
		statusReport.reportNonProgrammeData(nonProgrammeData);
		boolean validDpp = VALIDATOR_FACADE.validateFull(xmlFileName, programme, nonProgrammeData, statusReport);
		Progress.reportProgress(100, true);
		return validDpp;
	}

	private static boolean processValidateAudio(CommandLine commandLine, StatusReport statusReport) {

		String xmlFileName = commandLine.getXmlFileName();

		Programme programme = XML_MARSHALLING.getProgrammeFromXmlFile(xmlFileName, statusReport);
		NonProgrammeData nonProgrammeData = getNonProgrammeDataForMxf(commandLine);
		statusReport.reportNonProgrammeData(nonProgrammeData);
		return VALIDATOR_FACADE.validateAudio(programme, nonProgrammeData, statusReport);
	}

	private static boolean processValidateXml(CommandLine commandLine, StatusReport statusReport) {

		String xmlFileName = commandLine.getXmlFileName();

		Progress.reportProgress(1, false);
		boolean validXml = VALIDATOR_FACADE.validateAgainstSchema(xmlFileName, statusReport);
		Progress.reportProgress(100, true);
		return validXml;
	}

	private static boolean generateXmlSidecar(Programme programme, String transwrappedMxfFileName, String sidecarXmlFileName, String md5,
			StatusReport statusReport) {
		programme.getTechnical().getAdditional().setMediaChecksumType("MD5");
		programme.getTechnical().getAdditional().setMediaChecksumValue(md5);
		// DMIDPP-249 Ensure we strip path of output file
		programme.getTechnical().getAdditional().setAssociatedMediaFilename(new File(transwrappedMxfFileName).getName());

		return XML_MARSHALLING.generateXmlFileFromProgramme(programme, sidecarXmlFileName, statusReport);
	}

	private static CommandLineResult generateMxf(Programme programme, String xmlFileName, String mxfFileName, String transwrappedMxfFileName,
			String sidecarXmlFileName, String binPath, File workingDirectory) {
		CommandLineConstructor commandLineConstructor = new CommandLineConstructor(binPath);
		List<String> commands = commandLineConstructor.createTranswrapCommandLine(programme, xmlFileName, mxfFileName, transwrappedMxfFileName);
		CommandLineResult commandResult = CommandLineExec.runCommand(commands, workingDirectory, CommandLineExec.DEFAULT_TRANSWRAP_TIMEOUT);

		if (commandResult.isStopFileAborted()) {
			cleanupOnAbort(transwrappedMxfFileName, sidecarXmlFileName);
		}
		return commandResult;
	}

	private static void cleanupOnAbort(String transwrappedMxfFileName, String sidecarXmlFileName) {

		LOGGER.debug(String.format("Cleaning up %s and %s as process was aborted by stop file", transwrappedMxfFileName, sidecarXmlFileName));
		File transwrappedMxfFile = new File(transwrappedMxfFileName);
		File sidecarXmlFile = new File(sidecarXmlFileName);

		transwrappedMxfFile.deleteOnExit();
		sidecarXmlFile.deleteOnExit();
	}

	private static CommandLineResult extractGeneralInfo(String mxfFileName, String binPath, File workingDirectory) {
		CommandLineConstructor commandLineConstructor = new CommandLineConstructor(binPath);
		List<String> commands = commandLineConstructor.createGeneralInfoCommandLine(mxfFileName);
		return CommandLineExec.runCommand(commands, workingDirectory, CommandLineExec.DEFAULT_INFO_TIMEOUT);
	}

	private static CommandLineResult extractAS11Info(String mxfFileName, String binPath, File workingDirectory) {
		CommandLineConstructor commandLineConstructor = new CommandLineConstructor(binPath);
		List<String> commands = commandLineConstructor.createAs11InfoCommandLine(mxfFileName);
		return CommandLineExec.runCommand(commands, workingDirectory, CommandLineExec.DEFAULT_INFO_TIMEOUT);
	}

	private static CommandLineResult extractMd5(String mxfFileName, String binPath, File workingDirectory) {
		CommandLineConstructor commandLineConstructor = new CommandLineConstructor(binPath);
		List<String> commands = commandLineConstructor.createMd5InfoCommandLine(mxfFileName);
		return CommandLineExec.runCommand(commands, workingDirectory, CommandLineExec.DEFAULT_INFO_TIMEOUT);
	}

	protected static String getFileName(List<String> listOfParams, String prefix) {
		String fileName = null;
		boolean fileNameIsNextParam = false;
		for (String param : listOfParams) {
			if (fileNameIsNextParam) {
				fileName = param.trim();
				break;
			}
			if (param.startsWith(prefix)) {
				fileName = param.substring(prefix.length()).trim();
				if (fileName.isEmpty()) {
					// user has specified prefix<SPACE>filename
					fileNameIsNextParam = true;
				} else {
					break;
				}
			}
		}

		fileName = removeQuotesFromFileName(fileName);
		fileName = substituteTildeInFileName(fileName);
		return fileName;
	}

	protected static String substituteTildeInFileName(String filename) {

		String ret = filename;

		if (filename.startsWith("~" + File.separator)) {
			ret = System.getProperty("user.home") + filename.substring(1);
		}

		return ret;
	}

	protected static String removeQuotesFromFileName(String fileName) {
		if (fileName == null || fileName.length() == 0) {
			return "";
		}

		int startIndex = 0;
		int endIndex = fileName.length();

		if (fileName.startsWith("\"")) {
			startIndex++;
		}

		if (fileName.endsWith("\"")) {
			endIndex--;
		}

		return fileName.substring(startIndex, endIndex);
	}

	/**
	 * Has the user specified a flag, e.g. -o for overwrite
	 */
	protected static boolean getFlagSpecified(List<String> listOfParams, String flag) {
		boolean flagSpecified = false;
		for (String param : listOfParams) {
			if (param.startsWith(flag)) {
				flagSpecified = true;
				break;
			}
		}
		return flagSpecified;
	}

	protected static CommandTypeEnum getCommandType(List<String> listOfParams) {
		CommandTypeEnum commandTypeEnum = CommandTypeEnum.UNKNOWN;
		String strippedCommand = "";
		boolean commandIsNextParam = false;
		for (String param : listOfParams) {
			if (commandIsNextParam) {
				strippedCommand = param.trim();
				break;
			}
			if (param.startsWith(COMMAND_PREFIX)) {
				strippedCommand = param.substring(2).trim();
				if (strippedCommand.isEmpty()) {
					// it will be if user has specified -c<SPACE>command
					commandIsNextParam = true;
				} else {
					break;
				}
			}
		}
		commandTypeEnum = CommandTypeEnum.fromValue(strippedCommand);
		return commandTypeEnum;

	}

	/**
	 * Just for debugging
	 */
	private static void logCommandLine(List<String> listOfParams) {
		StringBuilder params = new StringBuilder();
		for (String param : listOfParams) {
			params.append(param);
			params.append(" ");
		}

		LOGGER.debug(String.format("DPP Java App called, parameters: %s", params));
	}

	private static boolean displayHelpIfRequested(List<String> listOfParams) {

		boolean helpRequested = listOfParams.contains("-help") || listOfParams.contains("--help") || listOfParams.contains("-?")
				|| listOfParams.contains("help");

		if (helpRequested) {
			AppHelp.displayHelp();
		}

		return helpRequested;
	}

	private static boolean displayEulaIfRequested(List<String> listOfParams) {

		boolean eulaRequested = listOfParams.contains("-version") || listOfParams.contains("--version") || listOfParams.contains("-about")
				|| listOfParams.contains("--about");

		if (eulaRequested) {
			AppEula appEula = new AppEula();
			appEula.displayEula(EULA_FILENAME);
		}

		return eulaRequested;
	}

	private static boolean displayDppVersionIfRequested(List<String> listOfParams) {
		boolean dppVersionRequested = listOfParams.contains("-dpp-version") || listOfParams.contains("--dpp-version");

		if (dppVersionRequested) {
			AppDppVersion appDppVersion = new AppDppVersion();
			appDppVersion.displayDppVersion(CONFIG_JAVA_FILENAME);
		}

		return dppVersionRequested;
	}

	private static boolean displayDefaultShimIfRequested(List<String> listOfParams) {
		boolean dppVersionRequested = listOfParams.contains("-shimname") || listOfParams.contains("--shimname");

		if (dppVersionRequested) {
			AppDppVersion appDppVersion = new AppDppVersion();
			appDppVersion.displayShimNames(CONFIG_JAVA_FILENAME);
		}

		return dppVersionRequested;
	}

	private static NonProgrammeData getNonProgrammeDataForMxf(CommandLine commandLine) {

		String mxfFileName = commandLine.getMxfFileName();
		String binPath = commandLine.getBinPath();
		File workingDirectory = commandLine.getStatusReportDirectory();

		NonProgrammeData nonProgrammeData = new NonProgrammeData();
		StatusReport statusReport = new StatusReport();
		CommandLineResult commandLineResultGeneralInfo = extractGeneralInfo(mxfFileName, binPath, workingDirectory);
		boolean generalInfoSuccess = commandLineResultGeneralInfo.isSuccess();
		if (generalInfoSuccess) {
			CommandLineResult commandLineResultAS11Info = extractAS11Info(mxfFileName, binPath, workingDirectory);
			nonProgrammeData = BMX_LIBRARY_WRAPPER.createNonProgrammeDataFromExtractInfo(commandLineResultGeneralInfo, commandLineResultAS11Info, statusReport);
		}
		return nonProgrammeData;

	}

	protected static boolean checkTranswrappedFileDoesNotExistOrOverwriteSpecified(String transwrappedMxfFileName, boolean overwrite) {
		return overwrite || !FileUtils.doesFileExist(transwrappedMxfFileName);
	}

}

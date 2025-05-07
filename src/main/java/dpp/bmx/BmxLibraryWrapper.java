package dpp.bmx;

//import dpp.schema.ObjectFactory;

import org.apache.log4j.Logger;

import dpp.bmx.filebuilders.As11CoreFrameworkTextFileBuilder;
import dpp.bmx.filebuilders.As11SegmentationFrameworkTextFileBuilder;
import dpp.bmx.filebuilders.UkDppFrameworkTextFileBuilder;
import dpp.enums.MxfTypeEnum;
import dpp.parsers.As11InfoParser;
import dpp.parsers.GeneralInfoParser;
import dpp.reporting.StatusReport;
import dpp.schema.Programme;
import dpp.util.FileUtils;
import dpp.validators.NonProgrammeData;
import dpp.xmlmarshalling.ProgrammeBuilder;

public class BmxLibraryWrapper {

	private static final Logger LOGGER = Logger.getLogger(BmxLibraryWrapper.class);
	private final As11InfoParser as11InfoParser = new As11InfoParser();
	private final GeneralInfoParser generalInfoParser = new GeneralInfoParser();
	private final As11CoreFrameworkTextFileBuilder as11CoreFrameworkTextFileBuilder = new As11CoreFrameworkTextFileBuilder();
	private final UkDppFrameworkTextFileBuilder ukDppFrameworkTextFileBuilder = new UkDppFrameworkTextFileBuilder();
	private final As11SegmentationFrameworkTextFileBuilder as11SegmentationFrameworkTextFileBuilder = new As11SegmentationFrameworkTextFileBuilder();

	public Programme createProgrammeFromExtractInfo(CommandLineResult commandLineResultGeneralInfo, CommandLineResult commandLineResultAS11Info,
			StatusReport statusReport) {
		// Create xml document
		Programme programme = ProgrammeBuilder.createEmptyProgramme();

		// Populate with general info
		if (commandLineResultGeneralInfo.isSuccess()) {
			generalInfoParser.populateProgrammeWithGeneralInfo(programme, commandLineResultGeneralInfo, statusReport);
		}

		// Populate with specific AS11 info if the file contained any.
		if (commandLineResultAS11Info.isSuccess()) {
			as11InfoParser.populateProgrammeWithAs11Info(programme, commandLineResultAS11Info);

			int totalNumberOfParts = programme.getTechnical().getTimecodes().getTotalNumberOfParts();
			int partsFound = programme.getTechnical().getTimecodes().getParts().getPart().size();

			if (totalNumberOfParts != partsFound) {

				statusReport.reportValidationError(String.format("TotalNumberOfParts (%d) does not match number of parts found (%d)", totalNumberOfParts,
						partsFound));
			}
		}

		return programme;
	}

	public NonProgrammeData createNonProgrammeDataFromExtractInfo(CommandLineResult commandLineResultGeneralInfo, CommandLineResult commandLineResultAS11Info,
			StatusReport statusReport) {
		MxfTypeEnum mxfTypeEnum = MxfTypeEnum.UNKNOWN;
		NonProgrammeData nonProgrammeData = new NonProgrammeData();
		// Populate with general info
		if (commandLineResultGeneralInfo.isSuccess()) {
			mxfTypeEnum = MxfTypeEnum.OP1A; // at least OP1A
			nonProgrammeData = generalInfoParser.parseNonProgrammeData(commandLineResultGeneralInfo, statusReport);

			boolean haveAs11Fields = commandLineResultAS11Info.isSuccess();
			if (haveAs11Fields) {
				mxfTypeEnum = MxfTypeEnum.AS11; // at least AS11
				if (as11InfoParser.haveDppFields(commandLineResultAS11Info)) {
					mxfTypeEnum = MxfTypeEnum.DPP; // already a DPP file.
				}
			}
		}
		nonProgrammeData.setMxfType(mxfTypeEnum);
		statusReport.setMxfFileType(mxfTypeEnum);

		return nonProgrammeData;
	}

	public boolean createBmxTextInputFiles(Programme programme, String xmlFileName, StatusReport statusReport, String appShimName) {
		boolean createdAs11CoreFrameworkTextFile = as11CoreFrameworkTextFileBuilder.createAs11CoreFrameworkTextFile(programme, xmlFileName, statusReport,
				appShimName);
		boolean createdAs11SegmentationFrameworkTextFile = as11SegmentationFrameworkTextFileBuilder.createAs11SegmentationFrameworkTextFile(programme,
				xmlFileName, statusReport);
		boolean createdUkDppFrameworkTextFile = ukDppFrameworkTextFileBuilder.createUkDppFrameworkTextFile(programme, xmlFileName, statusReport);

		boolean createdAllTextFiles = createdAs11CoreFrameworkTextFile && createdAs11SegmentationFrameworkTextFile && createdUkDppFrameworkTextFile;
		return createdAllTextFiles;
	}

	public boolean removeBmxTextInputFiles(String xmlFileName, StatusReport statusReport) {
		boolean filesRemoved = false;
		String as11CoreFileName = FileUtils.getAs11CoreFileNameFromXmlFileName(xmlFileName);
		String ukDppFileName = FileUtils.getUkDppFileNameFromXmlFileName(xmlFileName);
		String as11SegmentationFileName = FileUtils.getAs11SegmentationFileNameFromXmlFileName(xmlFileName);

		try {
			boolean as11CoreFileDeleteOk = FileUtils.deleteFile(as11CoreFileName);

			boolean ukDppFilDeleteOk = FileUtils.deleteFile(ukDppFileName);

			boolean as11SegmentationFileDeleteOk = FileUtils.deleteFile(as11SegmentationFileName);

			filesRemoved = as11CoreFileDeleteOk && ukDppFilDeleteOk && as11SegmentationFileDeleteOk;
		} catch (Exception e) {
			statusReport.reportSystemError("Error encountered deleting temporary files.");
			LOGGER.error("Error encountered deleting temporary files.", e);
		}

		return filesRemoved;
	}

}

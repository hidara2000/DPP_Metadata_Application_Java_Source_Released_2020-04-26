package dpp.bmx.filebuilders;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import dpp.reporting.StatusReport;
import dpp.schema.Part;
import dpp.schema.Parts;
import dpp.schema.Programme;
import dpp.schema.Timecodes;
import dpp.util.FileUtils;

public class As11SegmentationFrameworkTextFileBuilder {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(As11SegmentationFrameworkTextFileBuilder.class);

	/**
	 * The format for the segmentation file is : part number/part total <space*> timecode <space*> duration e.g. 1/3 10:00:00:10 10:00:01:00
	 * 
	 * @param statusReport
	 * 
	 */
	public boolean createAs11SegmentationFrameworkTextFile(final Programme programme, final String xmlFileName, final StatusReport statusReport) {
		final String as11SegmentationFrameworkFileName = FileUtils.getAs11SegmentationFileNameFromXmlFileName(xmlFileName);

		final List<String> listOfSegmentationLines = new ArrayList<String>();

		final Timecodes timecodes = programme.getTechnical().getTimecodes();
		final Parts parts = timecodes.getParts();
		for (Part part : parts.getPart()) {
			String segmentationLine = String.format("%d/%d %s %s", part.getPartNumber(), part.getPartTotal(), part.getPartSOM(), part.getPartDuration());
			listOfSegmentationLines.add(segmentationLine);
		}

		return FileUtils.createTextFileFromListOfLines(as11SegmentationFrameworkFileName, listOfSegmentationLines);
	}

}

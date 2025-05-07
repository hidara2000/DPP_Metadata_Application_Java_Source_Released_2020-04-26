package dpp.bmx.filebuilders;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import dpp.bmx.CommandLineConstants;
import dpp.reporting.StatusReport;
import dpp.schema.Programme;
import dpp.util.FileUtils;
import dpp.valuemaps.As11AudioTrackLayoutMap;
import dpp.valuemaps.As11ClosedCaptionTypeMap;

public class As11CoreFrameworkTextFileBuilder {
	private static final Logger LOGGER = Logger.getLogger(As11CoreFrameworkTextFileBuilder.class);
	private final As11AudioTrackLayoutMap as11AudioTrackLayoutMap = new As11AudioTrackLayoutMap();
	private final As11ClosedCaptionTypeMap as11ClosedCaptionTypeMap = new As11ClosedCaptionTypeMap();

	/**
	 * Creates text file used as input to bmxtranswrap as specified in --dm-file as11 file.txt If xml file name is abcdefg.xml then this file will be
	 * abcdefg_as11core.txt. Each line takes the form key : value
	 * 
	 * e.g. SeriesTitle: Holby City Series 5
	 * <nl>
	 * ProgrammeTitle: Holby City Series 5, Episode 10: Depths of Devotion
	 * <nl>
	 * EpisodeTitleNumber: Depths of Devotion
	 * <nl>
	 * ShimName: UK DPP SD Rev.1.0
	 * <nl>
	 * AudioTrackLayout: 3
	 * <nl>
	 * PrimaryAudioLanguage: eng
	 * <nl>
	 * ClosedCaptionsPresent: false
	 * 
	 * @param statusReport
	 * @param appShimName
	 * 
	 */
	public boolean createAs11CoreFrameworkTextFile(final Programme programme, final String xmlFileName, final StatusReport statusReport, String appShimName) {
		boolean result = false;
		try {
			final String as11CoreFrameworkFileName = FileUtils.getAs11CoreFileNameFromXmlFileName(xmlFileName);

			final Map<String, String> mapOfEntries = new HashMap<String, String>();

			mapOfEntries.put(CommandLineConstants.SERIES_TITLE, programme.getEditorial().getSeriesTitle());
			mapOfEntries.put(CommandLineConstants.PROGRAMME_TITLE, programme.getEditorial().getProgrammeTitle());
			mapOfEntries.put(CommandLineConstants.EPISODE_TITLE_NUMBER, programme.getEditorial().getEpisodeTitleNumber());
			mapOfEntries.put(CommandLineConstants.SHIM_NAME, appShimName);

			final String audioTrackLayout = programme.getTechnical().getAudio().getAudioTrackLayout().value();
			final int audioTrackLayoutInt = as11AudioTrackLayoutMap.getIntValue(audioTrackLayout);
			mapOfEntries.put(CommandLineConstants.AUDIO_TRACK_LAYOUT, audioTrackLayoutInt + " (" + audioTrackLayout + ")");

			mapOfEntries.put(CommandLineConstants.PRIMARY_AUDIO_LANGUAGE, programme.getTechnical().getAudio().getPrimaryAudioLanguage());

			createClosedCaptionsSection(programme, mapOfEntries);

			result = FileUtils.createTextFileWithValues(as11CoreFrameworkFileName, mapOfEntries);
		} catch (Exception e) {
			statusReport.reportSystemError("Caught exception when creating As11CoreFramework Text File");
			LOGGER.error("Caught exception when creating As11CoreFramework Text File", e);
			result = false;
		}
		return result;
	}

	private void createClosedCaptionsSection(final Programme programmeFromXmlFile, final Map<String, String> mapOfEntries) {
		final boolean closedCaptionsPresent = programmeFromXmlFile.getTechnical().getAccessServices().isClosedCaptionsPresent();
		mapOfEntries.put(CommandLineConstants.CLOSED_CAPTIONS_PRESENT, "" + closedCaptionsPresent);
		if (closedCaptionsPresent) {
			mapOfEntries
					.put(CommandLineConstants.CLOSED_CAPTIONS_LANGUAGE, programmeFromXmlFile.getTechnical().getAccessServices().getClosedCaptionsLanguage());
			String closedCaptionsType = programmeFromXmlFile.getTechnical().getAccessServices().getClosedCaptionsType().value();
			int closedCaptionTypeInt = as11ClosedCaptionTypeMap.getIntValue(closedCaptionsType);
			mapOfEntries.put(CommandLineConstants.CLOSED_CAPTIONS_TYPE, "" + closedCaptionTypeInt + " (" + closedCaptionsType + ")");
		}
	}
}

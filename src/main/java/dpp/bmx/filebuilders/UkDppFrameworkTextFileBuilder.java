package dpp.bmx.filebuilders;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import dpp.bmx.CommandLineConstants;
import dpp.reporting.StatusReport;
import dpp.schema.Programme;
import dpp.schema.SigningEnum;
import dpp.util.DateUtils;
import dpp.util.FileUtils;
import dpp.util.Timecode;
import dpp.valuemaps.PictureRatioMapper;
import dpp.valuemaps.UkDpp3DTypeMap;
import dpp.valuemaps.UkDppAudioDescriptionTypeMap;
import dpp.valuemaps.UkDppAudioLoudnessStandardMap;
import dpp.valuemaps.UkDppFpaMap;
import dpp.valuemaps.UkDppOpenCaptionTypeMap;
import dpp.valuemaps.UkDppSignLanguageMap;
import dpp.valuemaps.UkDppSigningPresentMap;

public class UkDppFrameworkTextFileBuilder {
	private static final String DEFAULT_TEXT_LANGUAGE = "eng";
	private final UkDpp3DTypeMap ukDpp3DTypeMap = new UkDpp3DTypeMap();
	private final UkDppFpaMap ukDppFpaMap = new UkDppFpaMap();
	private final UkDppAudioLoudnessStandardMap ukDppAudioLoudnessStandardMap = new UkDppAudioLoudnessStandardMap();
	private final UkDppAudioDescriptionTypeMap ukDppAudioDescriptionTypeMap = new UkDppAudioDescriptionTypeMap();
	private final UkDppSigningPresentMap ukDppSigningPresentMap = new UkDppSigningPresentMap();
	private final UkDppSignLanguageMap ukDppSignLanguageMap = new UkDppSignLanguageMap();
	private final UkDppOpenCaptionTypeMap ukDppOpenCaptionTypeMap = new UkDppOpenCaptionTypeMap();

	private static final Logger LOGGER = Logger.getLogger(UkDppFrameworkTextFileBuilder.class);

	/**
	 * 
	 * Creates text file used as input to bmxtranswrap as specified in --dm-file ukdpp file.txt If xml file name is abcdefg.xml then this file will be
	 * abcdefg_ukdpp.txt. It's a series of lines in the format key : value
	 * 
	 * @param statusReport
	 */
	public boolean createUkDppFrameworkTextFile(final Programme programme, final String xmlFileName, final StatusReport statusReport) {
		try {
			final String ukdppFrameworkFileName = FileUtils.getUkDppFileNameFromXmlFileName(xmlFileName);

			final Map<String, String> mapOfEntries = new HashMap<String, String>();
			addEntryToMap(mapOfEntries, CommandLineConstants.PRODUCTION_NUMBER, programme.getEditorial().getProductionNumber());
			addEntryToMap(mapOfEntries, CommandLineConstants.SYNOPSIS, programme.getEditorial().getSynopsis());
			addEntryToMap(mapOfEntries, CommandLineConstants.ORIGINATOR, programme.getEditorial().getOriginator());
			addEntryToMap(mapOfEntries, CommandLineConstants.COPYRIGHT_YEAR, DateUtils.getDateAsYyyy(programme.getEditorial().getCopyrightYear()));

			createOtherIdentifierSection(programme, mapOfEntries);

			addEntryToMap(mapOfEntries, CommandLineConstants.GENRE, programme.getEditorial().getGenre());
			addEntryToMap(mapOfEntries, CommandLineConstants.DISTRIBUTOR, programme.getEditorial().getDistributor());

			final String pictureRatio = programme.getTechnical().getVideo().getPictureRatio();
			if (pictureRatio != null && !pictureRatio.isEmpty()) {
				final String rationalPictureRatio = PictureRatioMapper.getRationalPictureRatioFromXmlPictureRatio(pictureRatio);
				addEntryToMap(mapOfEntries, CommandLineConstants.PICTURE_RATIO, rationalPictureRatio);
			}

			create3DSection(programme, mapOfEntries);

			addEntryToMap(mapOfEntries, CommandLineConstants.PRODUCT_PLACEMENT, "" + programme.getTechnical().getVideo().isProductPlacement());

			createFPASection(programme, mapOfEntries);

			addEntryToMap(mapOfEntries, CommandLineConstants.SECONDARY_AUDIO_LANGUAGE, programme.getTechnical().getAudio().getSecondaryAudioLanguage());
			addEntryToMap(mapOfEntries, CommandLineConstants.TERTIARY_AUDIO_LANGUAGE, programme.getTechnical().getAudio().getTertiaryAudioLanguage());

			addEntryToMap(mapOfEntries, CommandLineConstants.VIDEO_COMMENTS, programme.getTechnical().getVideo().getVideoComments());
			final String compliantAudioStandard = programme.getTechnical().getAudio().getAudioLoudnessStandard().value();
			int audioLoudnessStandard = ukDppAudioLoudnessStandardMap.getIntValue(compliantAudioStandard);
			mapOfEntries.put(CommandLineConstants.AUDIO_LOUDNESS_STANDARD, audioLoudnessStandard + " (" + compliantAudioStandard + ")");
			addEntryToMap(mapOfEntries, CommandLineConstants.AUDIO_COMMENTS, programme.getTechnical().getAudio().getAudioComments());
			mapOfEntries.put(CommandLineConstants.LINE_UP_START, programme.getTechnical().getTimecodes().getLineUpStart());
			addEntryToMap(mapOfEntries, CommandLineConstants.IDENT_CLOCK_START, programme.getTechnical().getTimecodes().getIdentClockStart());

			addEntryToMap(mapOfEntries, CommandLineConstants.TOTAL_NUMBER_OF_PARTS, "" + programme.getTechnical().getTimecodes().getTotalNumberOfParts());

			String totalProgrammeDuration = programme.getTechnical().getTimecodes().getTotalProgrammeDuration();
			Timecode instanceTimeCode = Timecode.getInstance(totalProgrammeDuration);
			mapOfEntries.put(CommandLineConstants.TOTAL_PROGRAMME_DURATION, totalProgrammeDuration + " (" + instanceTimeCode.getNumberOfFrames() + ")");
			boolean audioDescriptionPresent = programme.getTechnical().getAccessServices().isAudioDescriptionPresent();

			createAudioDescriptionSection(programme, mapOfEntries, audioDescriptionPresent);

			createSigningSection(programme, mapOfEntries);

			addEntryToMap(mapOfEntries, CommandLineConstants.COMPLETION_DATE,
					DateUtils.getDateAsYyyyMmDd(programme.getTechnical().getAdditional().getCompletionDate()));
			addEntryToMap(mapOfEntries, CommandLineConstants.TEXTLESS_ELEMENTS_EXIST, "" + programme.getTechnical().getAdditional().isTextlessElementExist());

			createProgrammeHasTextSection(programme, mapOfEntries);

			createOpenCaptionsSection(programme, mapOfEntries);

			addEntryToMap(mapOfEntries, CommandLineConstants.CONTACT_EMAIL, programme.getTechnical().getContactInformation().getContactEmail());
			addEntryToMap(mapOfEntries, CommandLineConstants.CONTACT_TELEPHONE_NUMBER, programme.getTechnical().getContactInformation()
					.getContactTelephoneNumber());

			return FileUtils.createTextFileWithValues(ukdppFrameworkFileName, mapOfEntries);
		} catch (Exception e) {
			statusReport.reportSystemError("Caught exception when creating UkDppFramework Text File");
			LOGGER.error("Caught exception when creating UkDppFramework Text File", e);
			return false;
		}
	}

	private void createProgrammeHasTextSection(final Programme programme, final Map<String, String> mapOfEntries) {
		final Boolean programmeHasText = programme.getTechnical().getAdditional().isProgrammeHasText();

		if (programmeHasText != null) {
			mapOfEntries.put(CommandLineConstants.PROGRAMME_HAS_TEXT, String.valueOf(programmeHasText));
			if (programmeHasText) {
				mapOfEntries.put(CommandLineConstants.PROGRAMME_TEXT_LANGUAGE, programme.getTechnical().getAdditional().getProgrammeTextLanguage());
			}
		} else {
			// DMIDPP-255 - default to text enabled and language English
			mapOfEntries.put(CommandLineConstants.PROGRAMME_HAS_TEXT, String.valueOf(Boolean.TRUE));
			mapOfEntries.put(CommandLineConstants.PROGRAMME_TEXT_LANGUAGE, DEFAULT_TEXT_LANGUAGE);
		}
	}

	private void createSigningSection(final Programme programme, final Map<String, String> mapOfEntries) {
		SigningEnum signingPresent = programme.getTechnical().getAccessServices().getSigningPresent();
		int signingPresentInt = ukDppSigningPresentMap.getIntValue(signingPresent.value());
		mapOfEntries.put(CommandLineConstants.SIGNING_PRESENT, signingPresentInt + " (" + signingPresent.value() + ")");
		if (signingPresentInt == 0 || signingPresentInt == 2) {
			String signLanguage = programme.getTechnical().getAccessServices().getSignLanguage().value();
			int signLanguageInt = ukDppSignLanguageMap.getIntValue(signLanguage);
			mapOfEntries.put(CommandLineConstants.SIGN_LANGUAGE, signLanguageInt + " (" + signLanguage + ")");
		}
	}

	private void createAudioDescriptionSection(final Programme programme, final Map<String, String> mapOfEntries, final boolean audioDescriptionPresent) {
		mapOfEntries.put(CommandLineConstants.AUDIO_DESCRIPTION_PRESENT, "" + audioDescriptionPresent);
		if (audioDescriptionPresent) {
			String audioDescriptionType = programme.getTechnical().getAccessServices().getAudioDescriptionType().value();
			int audioDescriptionTypeInt = ukDppAudioDescriptionTypeMap.getIntValue(audioDescriptionType);
			mapOfEntries.put(CommandLineConstants.AUDIO_DESCRIPTION_TYPE, audioDescriptionTypeInt + " (" + audioDescriptionType + ")");
		}
	}

	private void createFPASection(final Programme programme, final Map<String, String> mapOfEntries) {
		String fpaPass = programme.getTechnical().getVideo().getFPAPass().value();
		int fpaPassInt = ukDppFpaMap.getIntValue(fpaPass);
		mapOfEntries.put(CommandLineConstants.FPA_PASS, fpaPassInt + " (" + fpaPass + ")");
		addEntryToMap(mapOfEntries, CommandLineConstants.FPA_MANUFACTURER, programme.getTechnical().getVideo().getFPAManufacturer());
		addEntryToMap(mapOfEntries, CommandLineConstants.FPA_VERSION, programme.getTechnical().getVideo().getFPAVersion());
	}

	private void create3DSection(final Programme programme, final Map<String, String> mapOfEntries) {
		boolean threeD = programme.getTechnical().getVideo().isThreeD();
		mapOfEntries.put(CommandLineConstants.THREED, "" + threeD);
		if (threeD) {
			String threeDType = programme.getTechnical().getVideo().getThreeDType().value();
			int threeDTypeInt = ukDpp3DTypeMap.getIntValue(threeDType);
			mapOfEntries.put(CommandLineConstants.THREED_TYPE, threeDTypeInt + " (" + threeDType + ")");
		}
	}

	private void createOtherIdentifierSection(Programme programme, Map<String, String> mapOfEntries) {
		String otherIdentifier = programme.getEditorial().getOtherIdentifier();
		if (otherIdentifier != null && otherIdentifier.length() > 0) {
			addEntryToMap(mapOfEntries, CommandLineConstants.OTHER_IDENTIFIER, otherIdentifier);
			addEntryToMap(mapOfEntries, CommandLineConstants.OTHER_IDENTIFIER_TYPE, programme.getEditorial().getOtherIdentifierType());
		}
	}

	private void createOpenCaptionsSection(Programme programme, Map<String, String> mapOfEntries) {
		boolean openCaptionsPresent = programme.getTechnical().getAccessServices().isOpenCaptionsPresent();
		mapOfEntries.put(CommandLineConstants.OPEN_CAPTIONS_PRESENT, "" + openCaptionsPresent);
		if (openCaptionsPresent) {
			mapOfEntries.put(CommandLineConstants.OPEN_CAPTIONS_LANGUAGE, programme.getTechnical().getAccessServices().getOpenCaptionsLanguage());
			String openCaptionsType = programme.getTechnical().getAccessServices().getOpenCaptionsType().value();
			int openCaptionTypeInt = ukDppOpenCaptionTypeMap.getIntValue(openCaptionsType);
			mapOfEntries.put(CommandLineConstants.OPEN_CAPTIONS_TYPE, "" + openCaptionTypeInt + " (" + openCaptionsType + ")");
		}
	}

	private void addEntryToMap(Map<String, String> mapOfEntries, String key, String value) {
		if (value == null || value.equals("null")) {
			LOGGER.debug("No value found for " + key);
			// mapOfEntries.put(key, "");
			return;
		}
		mapOfEntries.put(key, value);
	}

}

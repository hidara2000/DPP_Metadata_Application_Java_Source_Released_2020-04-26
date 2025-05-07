package dpp.parsers;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;

import dpp.bmx.CommandLineConstants;
import dpp.bmx.CommandLineResult;
import dpp.schema.AudiodescriptiontypeEnum;
import dpp.schema.AudioloudnessstandardEnum;
import dpp.schema.AudiotracklayoutEnum;
import dpp.schema.ClosedcaptionstypeEnum;
import dpp.schema.FpapassEnum;
import dpp.schema.ObjectFactory;
import dpp.schema.OpencaptionstypeEnum;
import dpp.schema.Part;
import dpp.schema.Parts;
import dpp.schema.Programme;
import dpp.schema.SigningEnum;
import dpp.schema.SignlanguageEnum;
import dpp.schema.ThreedtypeEnum;
import dpp.util.DateUtils;
import dpp.valuemaps.As11AudioTrackLayoutMap;
import dpp.valuemaps.As11ClosedCaptionTypeMap;
import dpp.valuemaps.PictureRatioMapper;
import dpp.valuemaps.UkDpp3DTypeMap;
import dpp.valuemaps.UkDppAudioDescriptionTypeMap;
import dpp.valuemaps.UkDppAudioLoudnessStandardMap;
import dpp.valuemaps.UkDppCompliantAudioStandardMap;
import dpp.valuemaps.UkDppFpaMap;
import dpp.valuemaps.UkDppOpenCaptionTypeMap;
import dpp.valuemaps.UkDppSignLanguageMap;
import dpp.valuemaps.UkDppSigningPresentMap;

/**
 * Parses the info returned by mfx2raw --as11. This comprises as11 info plus potentially uk dpp info
 * 
 */
public class As11InfoParser extends AbstractBaseParser {
	private static final Logger LOGGER = Logger.getLogger(As11InfoParser.class);

	private final ObjectFactory dppObjectFactory = new ObjectFactory();
	private final UkDpp3DTypeMap ukDpp3DTypeMap = new UkDpp3DTypeMap();
	private final UkDppFpaMap ukDppFpaMap = new UkDppFpaMap();
	private final UkDppAudioLoudnessStandardMap ukDppAudioLoudnessStandardMap = new UkDppAudioLoudnessStandardMap();
	private final UkDppAudioDescriptionTypeMap ukDppAudioDescriptionTypeMap = new UkDppAudioDescriptionTypeMap();
	private final UkDppOpenCaptionTypeMap ukDppOpenCaptionTypeMap = new UkDppOpenCaptionTypeMap();
	private final UkDppSigningPresentMap ukDppSigningPresentMap = new UkDppSigningPresentMap();
	private final UkDppSignLanguageMap ukDppSignLanguageMap = new UkDppSignLanguageMap();
	private final As11AudioTrackLayoutMap as11AudioTrackLayoutMap = new As11AudioTrackLayoutMap();
	private final As11ClosedCaptionTypeMap as11ClosedCaptionTypeMap = new As11ClosedCaptionTypeMap();
	private final UkDppCompliantAudioStandardMap ukDppCompliantAudioStandardMap = new UkDppCompliantAudioStandardMap();

	private final Pattern segmentationPartsSectionPattern = Pattern.compile("PartNo/Total\\s+SOM\\s+Duration");
	private final Pattern segmentationPartsPattern = Pattern.compile("(\\d+)/(\\d+)\\s+(\\d\\d:\\d\\d:\\d\\d:\\d\\d)\\s+(\\d\\d:\\d\\d:\\d\\d:\\d\\d)");

	public void populateProgrammeWithAs11Info(Programme programme, CommandLineResult commandLineResultAS11Info) {

		List<String> stdOutputOfAs11 = commandLineResultAS11Info.getStdOutput();

		// Primarily As11 info: -
		parseShimName(programme, stdOutputOfAs11);

		parseTitles(programme, stdOutputOfAs11);

		parseAudioTrackLayout(programme, stdOutputOfAs11);

		parseClosedCaptionsSection(programme, stdOutputOfAs11);

		// Primarily Uk Dpp info: -
		parseProductionNumber(programme, stdOutputOfAs11);

		parseSynopsis(programme, stdOutputOfAs11);

		parseOriginator(programme, stdOutputOfAs11);

		parseOtherIdentifier(programme, stdOutputOfAs11);

		parseOtherIdentifierType(programme, stdOutputOfAs11);

		parseGenre(programme, stdOutputOfAs11);

		parseCopyrightYear(programme, stdOutputOfAs11);

		parseDistributor(programme, stdOutputOfAs11);

		parsePictureRatio(programme, stdOutputOfAs11);

		parse3DSection(programme, stdOutputOfAs11);

		parseProductPlacement(programme, stdOutputOfAs11);

		parseVideoComments(programme, stdOutputOfAs11);

		parseFPASection(programme, stdOutputOfAs11);

		parseLanguageSection(programme, stdOutputOfAs11);

		// Audio Loudness Standard maps to Compliant Audio Standard.
		parseAudioLoudnessStandardSection(programme, stdOutputOfAs11);

		parseAudioComments(programme, stdOutputOfAs11);

		parseLineUpStart(programme, stdOutputOfAs11);

		parseIdentOrClockStart(programme, stdOutputOfAs11);

		parseTotalNumberOfParts(programme, stdOutputOfAs11);

		parseTotalProgrammeDuration(programme, stdOutputOfAs11);

		parseAudioDescriptionSection(programme, stdOutputOfAs11);

		parseOpenCaptionsSection(programme, stdOutputOfAs11);

		parseSigningSection(programme, stdOutputOfAs11);

		parseCompletionDate(programme, stdOutputOfAs11);

		parseContactInfo(programme, stdOutputOfAs11);

		parseProgrammeHasText(programme, stdOutputOfAs11);

		parseTextlessElementsExist(programme, stdOutputOfAs11);

		parseSegmentationPartsSection(programme, stdOutputOfAs11);
	}

	private void parseTextlessElementsExist(Programme programme, List<String> stdOutputOfAs11) {
		Boolean textLessElementsExist = getValueForFieldAsBoolean(CommandLineConstants.TEXTLESS_ELEMENTS_EXIST, stdOutputOfAs11);
		if (checkValueIsSet(textLessElementsExist)) {
			programme.getTechnical().getAdditional().setTextlessElementExist(textLessElementsExist);
		}
	}

	private void parseProgrammeHasText(Programme programme, List<String> stdOutputOfAs11) {
		Boolean programmeHasText = getValueForFieldAsBoolean(CommandLineConstants.PROGRAMME_HAS_TEXT, stdOutputOfAs11);
		if (checkValueIsSet(programmeHasText)) {
			programme.getTechnical().getAdditional().setProgrammeHasText(programmeHasText);
			String programmeTextLanguage = getValueForField(CommandLineConstants.PROGRAMME_TEXT_LANGUAGE, stdOutputOfAs11);
			if (checkValueIsSet(programmeTextLanguage)) {
				programme.getTechnical().getAdditional().setProgrammeTextLanguage(programmeTextLanguage);
			}
		}
	}

	private void parseTotalProgrammeDuration(Programme programme, List<String> stdOutputOfAs11) {
		String totalProgrammeDuration = getValueForFieldWithoutHumanReadablePart(CommandLineConstants.TOTAL_PROGRAMME_DURATION, stdOutputOfAs11);
		if (checkValueIsSet(totalProgrammeDuration)) {
			programme.getTechnical().getTimecodes().setTotalProgrammeDuration(totalProgrammeDuration);
		}
	}

	private void parseTotalNumberOfParts(Programme programme, List<String> stdOutputOfAs11) {
		String totalNumberOfParts = getValueForField(CommandLineConstants.TOTAL_NUMBER_OF_PARTS, stdOutputOfAs11);
		if (checkValueIsSet(totalNumberOfParts)) {
			try {
				int totalNumberOfPartsAsInt = Integer.parseInt(totalNumberOfParts);
				programme.getTechnical().getTimecodes().setTotalNumberOfParts(totalNumberOfPartsAsInt);
			} catch (NumberFormatException nfe) {
				LOGGER.error("Failed to parse TotalNumberOfParts. (" + totalNumberOfParts + ")");
			}
		}
	}

	/**
	 * Returns true if we have any of the DPP only fields - in this case we are just checking for ProductionNumber which is a mandatory dpp field.
	 */
	public boolean haveDppFields(CommandLineResult commandLineResultAS11Info) {
		boolean isDpp = false;
		String productionNumber = getValueForField(CommandLineConstants.PRODUCTION_NUMBER, commandLineResultAS11Info.getStdOutput());
		if (checkValueIsSet(productionNumber)) {
			isDpp = true;
		}
		return isDpp;
	}

	private void parseAudioTrackLayout(Programme programme, List<String> stdOutputOfAs11) {
		Integer audioTrackLayoutIntEnum = getValueForFieldAsIntegerEnumValue(CommandLineConstants.AUDIO_TRACK_LAYOUT, stdOutputOfAs11);
		String audioTrackLayout = as11AudioTrackLayoutMap.getStringValue(audioTrackLayoutIntEnum);
		if (checkValueIsSet(audioTrackLayout)) {
			AudiotracklayoutEnum audiotracklayoutEnum = as11AudioTrackLayoutMap.getAudiotracklayoutEnumFromValue(audioTrackLayout);
			programme.getTechnical().getAudio().setAudioTrackLayout(audiotracklayoutEnum);
		}
	}

	private void parseShimName(Programme programme, List<String> stdOutputOfAs11) {
		String shimName = getValueForField(CommandLineConstants.SHIM_NAME, stdOutputOfAs11);
		if (checkValueIsSet(shimName)) {
			programme.getTechnical().setShimName(shimName);
		}
	}

	private void parseTitles(Programme programme, List<String> stdOutputOfAs11) {
		String seriesTitle = getValueForField(CommandLineConstants.SERIES_TITLE, stdOutputOfAs11);
		if (checkValueIsSet(seriesTitle)) {
			programme.getEditorial().setSeriesTitle(seriesTitle);
		}

		String programmeTitle = getValueForField(CommandLineConstants.PROGRAMME_TITLE, stdOutputOfAs11);
		if (checkValueIsSet(programmeTitle)) {
			programme.getEditorial().setProgrammeTitle(programmeTitle);
		}

		String episodeTitleOrNumber = getValueForField(CommandLineConstants.EPISODE_TITLE_NUMBER, stdOutputOfAs11);
		if (checkValueIsSet(episodeTitleOrNumber)) {
			programme.getEditorial().setEpisodeTitleNumber(episodeTitleOrNumber);
		}
	}

	private void parseContactInfo(Programme programme, List<String> stdOutputOfAs11) {
		String contactEmail = getValueForField(CommandLineConstants.CONTACT_EMAIL, stdOutputOfAs11);
		if (checkValueIsSet(contactEmail)) {
			programme.getTechnical().getContactInformation().setContactEmail(contactEmail);
		}

		String contactPhone = getValueForField(CommandLineConstants.CONTACT_TELEPHONE_NUMBER, stdOutputOfAs11);
		if (checkValueIsSet(contactPhone)) {
			programme.getTechnical().getContactInformation().setContactTelephoneNumber(contactPhone);
		}
	}

	private void parseIdentOrClockStart(Programme programme, List<String> stdOutputOfAs11) {
		String identClockStart = getValueForFieldWithoutHumanReadablePart(CommandLineConstants.IDENT_CLOCK_START, stdOutputOfAs11);
		if (checkValueIsSet(identClockStart)) {
			programme.getTechnical().getTimecodes().setIdentClockStart(identClockStart);
		}
	}

	private void parseLineUpStart(Programme programme, List<String> stdOutputOfAs11) {
		String lineUpStart = getValueForFieldWithoutHumanReadablePart(CommandLineConstants.LINE_UP_START, stdOutputOfAs11);
		if (checkValueIsSet(lineUpStart)) {
			programme.getTechnical().getTimecodes().setLineUpStart(lineUpStart);
		}
	}

	private void parseProductPlacement(Programme programme, List<String> stdOutputOfAs11) {
		Boolean productPlacement = getValueForFieldAsBoolean(CommandLineConstants.PRODUCT_PLACEMENT, stdOutputOfAs11);
		if (checkValueIsSet(productPlacement)) {
			programme.getTechnical().getVideo().setProductPlacement(productPlacement);
		}
	}

	// This needs to do some mapping from:
	// 4/3 , 14/9 , 5/3 , 16/9 , 37/20 , 7/3 or 12/5
	// to:
	// "4:3 (1.33:1)", "14:9 (1.55:1)", "15:9 (1.66:1)", "16:9 (1.78:1)", "16.65:9 (1.85:1)", "21:9 (2.33:1)" or "21.6:9 (2.40:1)".
	private void parsePictureRatio(Programme programme, List<String> stdOutputOfAs11) {
		String pictureRatio = getValueForField(CommandLineConstants.PICTURE_RATIO, stdOutputOfAs11);
		if (checkValueIsSet(pictureRatio)) {
			String xmlPictureRatio = PictureRatioMapper.getXmlPictureRatioFromRationalPictureRatio(pictureRatio);

			programme.getTechnical().getVideo().setPictureRatio(xmlPictureRatio);
		}
	}

	private void parseDistributor(Programme programme, List<String> stdOutputOfAs11) {
		String distributor = getValueForField(CommandLineConstants.DISTRIBUTOR, stdOutputOfAs11);
		if (checkValueIsSet(distributor)) {
			programme.getEditorial().setDistributor(distributor);
		}
	}

	private void parseOriginator(Programme programme, List<String> stdOutputOfAs11) {
		String originator = getValueForField(CommandLineConstants.ORIGINATOR, stdOutputOfAs11);
		if (checkValueIsSet(originator)) {
			programme.getEditorial().setOriginator(originator);
		}
	}

	private void parseOtherIdentifierType(Programme programme, List<String> stdOutputOfAs11) {
		String otherIdentifierType = getValueForField(CommandLineConstants.OTHER_IDENTIFIER_TYPE, stdOutputOfAs11);
		if (checkValueIsSet(otherIdentifierType)) {
			programme.getEditorial().setOtherIdentifierType(otherIdentifierType);
		}
	}

	private void parseOtherIdentifier(Programme programme, List<String> stdOutputOfAs11) {
		String otherIdentifier = getValueForField(CommandLineConstants.OTHER_IDENTIFIER, stdOutputOfAs11);
		if (checkValueIsSet(otherIdentifier)) {
			programme.getEditorial().setOtherIdentifier(otherIdentifier);
		}
	}

	private void parseGenre(Programme programme, List<String> stdOutputOfAs11) {
		String genre = getValueForField(CommandLineConstants.GENRE, stdOutputOfAs11);
		if (checkValueIsSet(genre)) {
			programme.getEditorial().setGenre(genre);
		}
	}

	private void parseSynopsis(Programme programme, List<String> stdOutputOfAs11) {
		String synopsis = getValueForField(CommandLineConstants.SYNOPSIS, stdOutputOfAs11);
		if (checkValueIsSet(synopsis)) {
			programme.getEditorial().setSynopsis(synopsis);
		}
	}

	private void parseProductionNumber(Programme programme, List<String> stdOutputOfAs11) {
		String productionNumber = getValueForField(CommandLineConstants.PRODUCTION_NUMBER, stdOutputOfAs11);
		if (checkValueIsSet(productionNumber)) {
			programme.getEditorial().setProductionNumber(productionNumber);
		}
	}

	private void parseAudioComments(Programme programme, List<String> stdOutputOfAs11) {
		String audioComments = getValueForField(CommandLineConstants.AUDIO_COMMENTS, stdOutputOfAs11);
		if (checkValueIsSet(audioComments)) {
			programme.getTechnical().getAudio().setAudioComments(audioComments);
		}
	}

	private void parseVideoComments(Programme programme, List<String> stdOutputOfAs11) {
		String videoComments = getValueForField(CommandLineConstants.VIDEO_COMMENTS, stdOutputOfAs11);
		if (checkValueIsSet(videoComments)) {
			programme.getTechnical().getVideo().setVideoComments(videoComments);
		}
	}

	private void parseCopyrightYear(Programme programme, List<String> stdOutputOfAs11) {
		String copyrightYear = getValueForField(CommandLineConstants.COPYRIGHT_YEAR, stdOutputOfAs11);
		if (checkValueIsSet(copyrightYear)) {
			XMLGregorianCalendar xgcCopyrightYear = DateUtils.createXmlGregorianCalendarDateFromYear(copyrightYear);
			programme.getEditorial().setCopyrightYear(xgcCopyrightYear);
		}
	}

	private void parseCompletionDate(Programme programme, List<String> stdOutputOfAs11) {
		String completionDate = getValueForField(CommandLineConstants.COMPLETION_DATE, stdOutputOfAs11);

		if (checkValueIsSet(completionDate)) {
			XMLGregorianCalendar xgcCompletionDate = DateUtils.createXmlGregorianCalendarDateFromDateTime(completionDate);
			programme.getTechnical().getAdditional().setCompletionDate(xgcCompletionDate);
		}
	}

	private void parseAudioLoudnessStandardSection(Programme programme, List<String> stdOutputOfAs11) {
		Integer audioLoudnessStandardInt = getValueForFieldAsIntegerEnumValue(CommandLineConstants.AUDIO_LOUDNESS_STANDARD, stdOutputOfAs11);
		if (checkValueIsSet(audioLoudnessStandardInt)) {
			String audioLoudness = ukDppAudioLoudnessStandardMap.getStringValue(audioLoudnessStandardInt);
			AudioloudnessstandardEnum audioloudnessEnum = ukDppCompliantAudioStandardMap.getAudioloudnessEnumFromValue(audioLoudness);
			programme.getTechnical().getAudio().setAudioLoudnessStandard(audioloudnessEnum);
		}
	}

	private void parseSegmentationPartsSection(Programme programme, List<String> stdOutputOfAs11) {

		Parts parts = dppObjectFactory.createParts();
		boolean insideSOM = false;
		for (String lineItem : stdOutputOfAs11) {
			String trimmedItem = lineItem.trim();

			if (!insideSOM) {
				Matcher somMatcher = segmentationPartsSectionPattern.matcher(trimmedItem);
				if (somMatcher.matches()) {
					insideSOM = true;
				}
			}

			if (insideSOM) {
				Matcher partsMatcher = segmentationPartsPattern.matcher(trimmedItem);
				if (partsMatcher.matches()) {
					String partNumber = partsMatcher.group(1);
					String partTotal = partsMatcher.group(2);
					String partSom = partsMatcher.group(3);
					String partDuration = partsMatcher.group(4);

					Part part = createPart(partNumber, partTotal, partSom, partDuration);
					parts.getPart().add(part);
				}
			}
		}
		programme.getTechnical().getTimecodes().setParts(parts);
	}

	private Part createPart(String partNumber, String partTotal, String partSom, String partDuration) {
		Part part = dppObjectFactory.createPart();
		part.setPartNumber(Integer.parseInt(partNumber));
		part.setPartSOM(partSom);
		part.setPartDuration(partDuration);
		part.setPartTotal(Integer.parseInt(partTotal));

		return part;
	}

	private void parseClosedCaptionsSection(Programme programme, List<String> stdOutputOfAs11) {
		Boolean closedCaptionsPresent = getValueForFieldAsBoolean(CommandLineConstants.CLOSED_CAPTIONS_PRESENT, stdOutputOfAs11);
		if (checkValueIsSet(closedCaptionsPresent)) {
			String closedCaptionsLanguage = getValueForField(CommandLineConstants.CLOSED_CAPTIONS_LANGUAGE, stdOutputOfAs11);
			programme.getTechnical().getAccessServices().setClosedCaptionsPresent(closedCaptionsPresent);

			if (checkValueIsSet(closedCaptionsLanguage)) {
				programme.getTechnical().getAccessServices().setClosedCaptionsLanguage(closedCaptionsLanguage);
			}

			Integer closedCaptionsTypeIntEnum = getValueForFieldAsIntegerEnumValue(CommandLineConstants.CLOSED_CAPTIONS_TYPE, stdOutputOfAs11);
			String closedCaptionsType = as11ClosedCaptionTypeMap.getStringValue(closedCaptionsTypeIntEnum);
			if (checkValueIsSet(closedCaptionsType)) {
				ClosedcaptionstypeEnum closedcaptionstypeEnum = as11ClosedCaptionTypeMap.getClosedcaptionstypeEnum(closedCaptionsType);
				programme.getTechnical().getAccessServices().setClosedCaptionsType(closedcaptionstypeEnum);
			}
		}
	}

	private void parseSigningSection(Programme programme, List<String> stdOutputOfAs11) {
		Integer signingPresentEnumInt = getValueForFieldAsIntegerEnumValue(CommandLineConstants.SIGNING_PRESENT, stdOutputOfAs11);
		String signingPresent = ukDppSigningPresentMap.getStringValue(signingPresentEnumInt);
		if (checkValueIsSet(signingPresent)) {
			SigningEnum signingEnum = ukDppSigningPresentMap.getSigningEnumFromValue(signingPresent);

			if (signingPresentEnumInt == 0 || signingPresentEnumInt == 2) {
				int signLanguageInt = getValueForFieldAsIntegerEnumValue(CommandLineConstants.SIGN_LANGUAGE, stdOutputOfAs11);
				String signLanguage = ukDppSignLanguageMap.getStringValue(signLanguageInt);
				if (checkValueIsSet(signLanguage)) {
					SignlanguageEnum signlanuageEnum = ukDppSignLanguageMap.getSignlanguageEnumFromValue(signLanguage);
					programme.getTechnical().getAccessServices().setSignLanguage(signlanuageEnum);
				}
			}
			programme.getTechnical().getAccessServices().setSigningPresent(signingEnum);
		}
	}

	private void parseOpenCaptionsSection(Programme programme, List<String> stdOutputOfAs11) {
		Boolean openCaptionsPresent = getValueForFieldAsBoolean(CommandLineConstants.OPEN_CAPTIONS_PRESENT, stdOutputOfAs11);
		if (checkValueIsSet(openCaptionsPresent)) {
			programme.getTechnical().getAccessServices().setOpenCaptionsPresent(openCaptionsPresent);

			String openCaptionsLanguage = getValueForField(CommandLineConstants.OPEN_CAPTIONS_LANGUAGE, stdOutputOfAs11);
			if (checkValueIsSet(openCaptionsLanguage)) {
				programme.getTechnical().getAccessServices().setOpenCaptionsLanguage(openCaptionsLanguage);
			}
			Integer openCaptionsTypeNumber = getValueForFieldAsIntegerEnumValue(CommandLineConstants.OPEN_CAPTIONS_TYPE, stdOutputOfAs11);
			String openCaptionsType = ukDppOpenCaptionTypeMap.getStringValue(openCaptionsTypeNumber);
			if (checkValueIsSet(openCaptionsType)) {
				OpencaptionstypeEnum opencaptionstypeEnum = ukDppOpenCaptionTypeMap.getOpencaptionstypeEnumFromValue(openCaptionsType);
				programme.getTechnical().getAccessServices().setOpenCaptionsType(opencaptionstypeEnum);
			}
		}
	}

	private void parseAudioDescriptionSection(Programme programme, List<String> stdOutputOfAs11) {
		Boolean audioDescriptionPresent = getValueForFieldAsBoolean(CommandLineConstants.AUDIO_DESCRIPTION_PRESENT, stdOutputOfAs11);
		if (checkValueIsSet(audioDescriptionPresent)) {
			if (audioDescriptionPresent) {
				int audioDescriptionTypeEnumInt = getValueForFieldAsIntegerEnumValue(CommandLineConstants.AUDIO_DESCRIPTION_TYPE, stdOutputOfAs11);
				String audioDescriptionType = ukDppAudioDescriptionTypeMap.getStringValue(audioDescriptionTypeEnumInt);
				if (checkValueIsSet(audioDescriptionType)) {
					AudiodescriptiontypeEnum audiodescriptiontypeEnum = ukDppAudioDescriptionTypeMap.getAudiodescriptiontypeEnumFromValue(audioDescriptionType);
					programme.getTechnical().getAccessServices().setAudioDescriptionType(audiodescriptiontypeEnum);
				}
			}
			programme.getTechnical().getAccessServices().setAudioDescriptionPresent(audioDescriptionPresent);
		}
	}

	private void parseLanguageSection(Programme programme, List<String> stdOutputOfAs11) {
		String primaryAudioLanguage = getValueForField(CommandLineConstants.PRIMARY_AUDIO_LANGUAGE, stdOutputOfAs11);
		if (checkValueIsSet(primaryAudioLanguage)) {
			programme.getTechnical().getAudio().setPrimaryAudioLanguage(primaryAudioLanguage);
		}
		String secondaryAudioLanguage = getValueForField(CommandLineConstants.SECONDARY_AUDIO_LANGUAGE, stdOutputOfAs11);
		if (checkValueIsSet(secondaryAudioLanguage)) {
			programme.getTechnical().getAudio().setSecondaryAudioLanguage(secondaryAudioLanguage);
		}
		String tertiaryAudioLanguage = getValueForField(CommandLineConstants.TERTIARY_AUDIO_LANGUAGE, stdOutputOfAs11);
		if (checkValueIsSet(tertiaryAudioLanguage)) {
			programme.getTechnical().getAudio().setTertiaryAudioLanguage(tertiaryAudioLanguage);
		}

	}

	private void parseFPASection(Programme programme, List<String> stdOutputOfAs11) {
		Integer fpaPass = getValueForFieldAsIntegerEnumValue(CommandLineConstants.FPA_PASS, stdOutputOfAs11);
		String fpaPassValue = ukDppFpaMap.getStringValue(fpaPass);
		if (checkValueIsSet(fpaPassValue)) {
			FpapassEnum fpapassEnum = ukDppFpaMap.getFpapassEnumFromValue(fpaPassValue);
			programme.getTechnical().getVideo().setFPAPass(fpapassEnum);
		}
		String fpaManufacturer = getValueForField(CommandLineConstants.FPA_MANUFACTURER, stdOutputOfAs11);
		if (checkValueIsSet(fpaManufacturer)) {
			programme.getTechnical().getVideo().setFPAManufacturer(fpaManufacturer);
		}
		String fpaVersion = getValueForField(CommandLineConstants.FPA_VERSION, stdOutputOfAs11);
		if (checkValueIsSet(fpaVersion)) {
			programme.getTechnical().getVideo().setFPAVersion(fpaVersion);
		}
	}

	private void parse3DSection(Programme programme, List<String> stdOutputOfAs11) {
		Boolean threeD = getValueForFieldAsBoolean(CommandLineConstants.THREED, stdOutputOfAs11);
		if (checkValueIsSet(threeD)) {
			if (threeD) {
				int ThreeDTypeAsIntEnumValue = getValueForFieldAsIntegerEnumValue(CommandLineConstants.THREED_TYPE, stdOutputOfAs11);
				String threeDType = ukDpp3DTypeMap.getStringValue(ThreeDTypeAsIntEnumValue);
				if (checkValueIsSet(threeDType)) {
					ThreedtypeEnum threedtypeEnum = ukDpp3DTypeMap.getThreedtypeEnumFromValue(threeDType);
					programme.getTechnical().getVideo().setThreeDType(threedtypeEnum);
				}
			}
			programme.getTechnical().getVideo().setThreeD(threeD);
		} else {
			programme.getTechnical().getVideo().setThreeD(false);
		}
	}

}

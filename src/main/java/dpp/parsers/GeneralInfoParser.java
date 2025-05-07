package dpp.parsers;

import java.math.BigInteger;
import java.util.List;

import org.apache.log4j.Logger;

import dpp.ObjectFactoryConstants;
import dpp.bmx.CommandLineConstants;
import dpp.bmx.CommandLineResult;
import dpp.reporting.StatusReport;
import dpp.schema.AudiosamplingfrequencyDt;
import dpp.schema.Programme;
import dpp.schema.VideobitrateDt;
import dpp.validators.NonProgrammeData;

public class GeneralInfoParser extends AbstractBaseParser {
	private static final Logger LOGGER = Logger.getLogger(GeneralInfoParser.class);

	/**
	 * Populates the programme with values from the general info obtained from "mxf2raw -i". Because of repeating groups (especially track info) we need to be
	 * able to find a particular section (e.g. video track) and then to get values following that (e.g. essence type) Note that although in general we want to
	 * validate the data after parsing it, this is not possible to do easily for the audio tracks as there are multiple tracks which need to be validated but
	 * the xml schema is only interested in aggregate values.
	 * 
	 * @param programme
	 * @param commandLineResultGeneralInfo
	 */
	public void populateProgrammeWithGeneralInfo(Programme programme, CommandLineResult commandLineResultGeneralInfo, StatusReport statusReport) {

		List<String> stdOutputOfGeneral = commandLineResultGeneralInfo.getStdOutput();
		String[] arrayNameValues = stdOutputOfGeneral.toArray(new String[stdOutputOfGeneral.size()]);

		int indexOfVideoTrack = getIndexOfVideoTrack(arrayNameValues);
		if (indexOfVideoTrack < 0) {
			String msg = "Source file has no video track";
			statusReport.reportSystemError(msg);
			LOGGER.error(msg);
		} else {

			try {
				parseVideoEssenceSection(programme, arrayNameValues);

				parsePictureFormatSection(programme, arrayNameValues);

				parseAfdSection(programme, arrayNameValues, statusReport);

				parseAudioSection(programme, arrayNameValues);
			} catch (Exception e) {
				statusReport.reportSystemError("Caught exception populating Programme with General Info");
				LOGGER.error("Caught exception populating Programme with General Info", e);
			}
		}
	}

	/**
	 * This method populates all the fields which are not persisted in the Programme but which are used in validation routines.
	 * 
	 * @param stdOutputOfGeneral
	 * @param arrayNameValues
	 * @param statusReport
	 * @param nonProgrammeData
	 */
	public NonProgrammeData parseNonProgrammeData(CommandLineResult commandLineResultGeneralInfo, StatusReport statusReport) {
		NonProgrammeData nonProgrammeData = new NonProgrammeData();

		List<String> stdOutputOfGeneral = commandLineResultGeneralInfo.getStdOutput();
		String[] arrayNameValues = stdOutputOfGeneral.toArray(new String[stdOutputOfGeneral.size()]);

		String duration = getValueForField(CommandLineConstants.DURATION, stdOutputOfGeneral);
		Long durationAsLong = null;
		try {
			durationAsLong = Long.parseLong(duration);
			nonProgrammeData.setDuration(durationAsLong);
		} catch (NumberFormatException nfe) {
			LOGGER.error("Failed to parse duration. (" + duration + ")");
		}
		int numberOfAudioTracks = getTotalNumberOfAudioChannels(arrayNameValues);
		nonProgrammeData.setTotalNumberOfAudioChannels(numberOfAudioTracks);

		int indexOfVideoTrack = getIndexOfVideoTrack(arrayNameValues);
		String editRate = findValueAfterIndex(arrayNameValues, indexOfVideoTrack, CommandLineConstants.EDIT_RATE);
		String videoEssenceType = findValueAfterIndex(arrayNameValues, indexOfVideoTrack, CommandLineConstants.ESSENCE_TYPE);
		String materialStartTimecode = findValueAfterIndex(arrayNameValues, 0, CommandLineConstants.MATERIAL_START_TIMECODE);

		if (materialStartTimecode == null) {
			String error = "Failed to parse 'Material start timecode' from general output.";
			statusReport.reportValidationError(error);
			LOGGER.error(error);
		}

		nonProgrammeData.setEditRate(editRate);
		nonProgrammeData.setMaterialStartTimecode(materialStartTimecode);

		if (videoEssenceType.toUpperCase().contains(CommandLineConstants.AVC.toUpperCase())) {
			nonProgrammeData.setHighDefinition(true);
		} else {
			nonProgrammeData.setHighDefinition(false);
		}

		nonProgrammeData.setVideoEssenceType(videoEssenceType);

		return nonProgrammeData;

	}

	/**
	 * Audio Sampling Frequency Sampling rate (Use this value to populate the field) (Total No. of Audio Tracks == 4 || Total No. of Audio Tracks == 16) ? OK :
	 * Error; for (All Audio Tracks) { Sampling rate.toString.contains("48000") ? (Audio Sampling Frequency = 48000) : Error; }
	 * 
	 * Audio Bit Depth Bits per sample (Use this value) for (All Audio Tracks) { Bits per sample.toString.contains("24") ? (Audio Bit Depth = 24) : Error; }
	 * 
	 * Audio Codec Parameters Essence type (Use this value) for (All Audio Tracks) { Essence type.toString().contains(�PCM�) ? (Audio Codec Parameters = PCM) :
	 * Error; }
	 * 
	 * @param programme
	 * @param arrayNameValues
	 * @param statusReport
	 */
	private void parseAudioSection(Programme programme, String[] arrayNameValues) {
		int numberOfAudioTracks = getNumberOfAudioTracks(arrayNameValues);
		boolean allSamplingRatesCorrect = true;
		boolean allAudioBitDepthCorrect = true;
		boolean anyEssenceTypePCM = false;
		boolean anyEssenceTypeAES3 = false;
		boolean anyEssenceTypeOther = false;

		for (int trackNo = 1; trackNo <= numberOfAudioTracks; trackNo++) {
			int indexOfAudioTrack = findIndexWithValue(arrayNameValues, CommandLineConstants.ESSENCE_KIND, CommandLineConstants.SOUND, trackNo);
			String samplingRate = findValueAfterIndex(arrayNameValues, indexOfAudioTrack, CommandLineConstants.SAMPLING_RATE);
			String bitsPerSample = findValueAfterIndex(arrayNameValues, indexOfAudioTrack, CommandLineConstants.BITS_PER_SAMPLE);
			String essenceType = findValueAfterIndex(arrayNameValues, indexOfAudioTrack, CommandLineConstants.ESSENCE_TYPE);

			boolean samplingRateCorrect = samplingRate.equalsIgnoreCase(CommandLineConstants.AUDIO_SAMPLING_RATE_RATIO)
					|| samplingRate.equalsIgnoreCase(CommandLineConstants.AUDIO_SAMPLING_RATE);
			if (!samplingRateCorrect) {
				programme.getTechnical().getAudio().setAudioSamplingFrequency(getAudioSamplingFrequency(samplingRate)); // wrong value
				allSamplingRatesCorrect = false;
			}

			boolean audioBitDepthCorrect = bitsPerSample.contains(CommandLineConstants.AUDIO_BIT_DEPTH);
			if (!audioBitDepthCorrect) {
				try {
					long erroneousBitDepth = Long.parseLong(bitsPerSample);
					programme.getTechnical().getAudio().setAudioBitDepth(erroneousBitDepth);
				} catch (NumberFormatException nfe) {
					programme.getTechnical().getAudio().setAudioBitDepth(0);
				}
				allAudioBitDepthCorrect = false;
			}

			boolean essenceTypePCM = essenceType.contains(CommandLineConstants.PCM) && (!essenceType.contains(CommandLineConstants.MXF2RAW_D10_AES3_PCM));
			boolean essenceTypeAES3 = essenceType.contains(CommandLineConstants.MXF2RAW_D10_AES3_PCM);
			if (essenceTypePCM) {
				anyEssenceTypePCM = true;
			} else if (essenceTypeAES3) {
				anyEssenceTypeAES3 = true;
			} else {
				anyEssenceTypeOther = true;
			}

			boolean sampleRateAndBitDepthOK = (allSamplingRatesCorrect && allAudioBitDepthCorrect);
			if (sampleRateAndBitDepthOK) {
				break; // In error so don't need to look at the others.
			}
		}

		boolean onlyPcmFound = anyEssenceTypePCM && !anyEssenceTypeAES3 && !anyEssenceTypeOther;
		boolean onlyAes3Found = !anyEssenceTypePCM && anyEssenceTypeAES3 && !anyEssenceTypeOther;
		if (onlyPcmFound) {
			programme.getTechnical().getAudio().setAudioCodecParameters(CommandLineConstants.PCM);
		} else if (onlyAes3Found) {
			programme.getTechnical().getAudio().setAudioCodecParameters(CommandLineConstants.DPP_D10_AES3_PCM);
		}

		if (allSamplingRatesCorrect) {
			programme.getTechnical().getAudio().setAudioSamplingFrequency(getAudioSamplingFrequency(CommandLineConstants.AUDIO_SAMPLING_RATE_KHZ));
		}

		if (allAudioBitDepthCorrect) {
			programme.getTechnical().getAudio().setAudioBitDepth(CommandLineConstants.AUDIO_BIT_DEPTH_VALUE);
		}

	}

	/**
	 * Find the number of audio channels - tracks with n channels are counted as n
	 */
	protected int getTotalNumberOfAudioChannels(String[] arrayNameValues) {
		int channelsFound = 0;

		for (int loop = 0; loop < arrayNameValues.length; loop++) {
			String currentItem = arrayNameValues[loop];
			String value = "";
			String trimmedItem = currentItem.trim();

			if (trimmedItem.startsWith(CommandLineConstants.ESSENCE_KIND)) {
				int colonPosition = trimmedItem.indexOf(':');
				value = trimmedItem.substring(colonPosition + 1).trim();
				if (value.equalsIgnoreCase(CommandLineConstants.SOUND)) {
					String channelCount = findValueAfterIndex(arrayNameValues, loop, CommandLineConstants.CHANNEL_COUNT);
					int currentChannelCount = Integer.parseInt(channelCount);
					channelsFound += currentChannelCount;
				}

			}
		}

		return channelsFound;
	}

	/**
	 * Find the number of audio tracks - tracks with multiple channels are counted as 1
	 */
	protected int getNumberOfAudioTracks(String[] arrayNameValues) {
		int audioTracksFound = 0;

		for (int loop = 0; loop < arrayNameValues.length; loop++) {
			String currentItem = arrayNameValues[loop];
			String value = "";
			String trimmedItem = currentItem.trim();

			if (trimmedItem.startsWith(CommandLineConstants.ESSENCE_KIND)) {
				int colonPosition = trimmedItem.indexOf(':');
				value = trimmedItem.substring(colonPosition + 1).trim();
				if (value.equalsIgnoreCase(CommandLineConstants.SOUND)) {
					audioTracksFound++;
				}

			}
		}

		return audioTracksFound;
	}

	private void parseAfdSection(Programme programme, String[] arrayNameValues, StatusReport statusReport) {
		/*
		 * if (AFD != "(not set)") { AFD = AFD; } else if (Aspect ratio != "(not set)") { if (Aspect ratio == 4/3) { AFD = 9; } else if (Aspect ratio == 16/9) {
		 * AFD = 10; } else if (Aspect ratio == 14/9) { AFD = 14; } else { Error; } } else { Error; }
		 */
		try {
			int indexOfVideoTrack = getIndexOfVideoTrack(arrayNameValues);
			String afd = findValueAfterIndex(arrayNameValues, indexOfVideoTrack, CommandLineConstants.AFD);
			if (checkValueIsSet(afd)) {
				programme.getTechnical().getVideo().setAFD(BigInteger.valueOf(Integer.parseInt(afd)));
			} else {
				String aspectRatio = findValueAfterIndex(arrayNameValues, indexOfVideoTrack, CommandLineConstants.ASPECT_RATIO);
				if (checkValueIsSet(aspectRatio)) {
					if (aspectRatio.equals(CommandLineConstants.ASPECT_RATIO_4_3)) {
						programme.getTechnical().getVideo().setAFD(BigInteger.valueOf(9));
					} else if (aspectRatio.equals(CommandLineConstants.ASPECT_RATIO_16_9)) {
						programme.getTechnical().getVideo().setAFD(BigInteger.valueOf(10));
					} else if (aspectRatio.equals(CommandLineConstants.ASPECT_RATIO_14_9)) {
						programme.getTechnical().getVideo().setAFD(BigInteger.valueOf(14));
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Caught exception parsing AFD section.", e);
			statusReport.reportSystemError("Caught exception parsing AFD section.");
		}

	}

	private void parsePictureFormatSection(Programme programme, String[] arrayNameValues) {
		int indexOfVideoTrack = getIndexOfVideoTrack(arrayNameValues);
		String editRate = findValueAfterIndex(arrayNameValues, indexOfVideoTrack, CommandLineConstants.EDIT_RATE);
		if (!(editRate.equals(CommandLineConstants.EDIT_RATE_VALUE) || editRate.equals(CommandLineConstants.EDIT_RATE_RATIO))) {
			return; // can't map the picture format if we don't have an edit rate of 25fps
		}

		String videoCodec = programme.getTechnical().getVideo().getVideoCodec(); // should have parsed this out already
		if (videoCodec.equalsIgnoreCase(CommandLineConstants.DPP_AVCI_CODEC)) {

			programme.getTechnical().getVideo().setPictureFormat(CommandLineConstants.PICTURE_FORMAT_1080I);
		} else if (videoCodec.equalsIgnoreCase(CommandLineConstants.DPP_D10_CODEC)) {

			String aspectRatio = findValueAfterIndex(arrayNameValues, indexOfVideoTrack, CommandLineConstants.ASPECT_RATIO);
			if (aspectRatio.equals(CommandLineConstants.ASPECT_RATIO_4_3)) {
				programme.getTechnical().getVideo().setPictureFormat(CommandLineConstants.PICTURE_FORMAT_576I_4_3);
			} else if (aspectRatio.equals(CommandLineConstants.ASPECT_RATIO_16_9)) {
				programme.getTechnical().getVideo().setPictureFormat(CommandLineConstants.PICTURE_FORMAT_576I_16_9);
			}
		}

	}

	private void parseVideoEssenceSection(Programme programme, String[] arrayNameValues) {
		int indexOfVideoTrack = getIndexOfVideoTrack(arrayNameValues);

		String essenceType = findValueAfterIndex(arrayNameValues, indexOfVideoTrack, CommandLineConstants.ESSENCE_TYPE);
		String avciHeader = findValueAfterIndex(arrayNameValues, indexOfVideoTrack, CommandLineConstants.AVCI_HEADER);

		boolean isEssenceAVC_Intra = essenceType.toUpperCase().contains(CommandLineConstants.MXF2RAW_AVCI_CODEC.toUpperCase())
				|| essenceType.toUpperCase().contains(CommandLineConstants.MXF2RAW_AVC_INTRA_CODEC.toUpperCase());

		boolean isEssenceD10 = essenceType.toUpperCase().contains(CommandLineConstants.MXF2RAW_IMX_CODEC.toUpperCase())
				|| essenceType.toUpperCase().contains(CommandLineConstants.MXF2RAW_D10_CODEC.toUpperCase());

		if (isEssenceAVC_Intra && avciHeader.equalsIgnoreCase(Boolean.TRUE.toString())) {
			programme.getTechnical().getVideo().setVideoCodec(CommandLineConstants.DPP_AVCI_CODEC);
			programme.getTechnical().getVideo().setVideoCodecParameters(CommandLineConstants.DPP_AVCI_VIDEO_CODEC_PARAMETERS);
		} else if (isEssenceD10) {
			programme.getTechnical().getVideo().setVideoCodec(CommandLineConstants.DPP_D10_CODEC);
			programme.getTechnical().getVideo().setVideoCodecParameters(CommandLineConstants.DPP_D10_VIDEO_CODEC_PARAMETERS);
		}

		if (essenceType.toUpperCase().contains(CommandLineConstants.ONE_HUNDRED_MBPS.toUpperCase())) {
			programme.getTechnical().getVideo().setVideoBitRate(getVideoBitRate(CommandLineConstants.ONE_HUNDRED_INT));
		} else if (essenceType.toUpperCase().contains(CommandLineConstants.FIFTY_MBPS.toUpperCase())) {
			programme.getTechnical().getVideo().setVideoBitRate(getVideoBitRate(CommandLineConstants.FIFTY_INT));
		}
	}

	private int getIndexOfVideoTrack(String[] arrayNameValues) {
		return findIndexWithValue(arrayNameValues, CommandLineConstants.ESSENCE_KIND, CommandLineConstants.PICTURE, 1);
	}

	private String findValueAfterIndex(String[] arrayNameValues, int afterIndex, String keyName) {
		String retVal = null;
		for (int loop = afterIndex; loop < arrayNameValues.length; loop++) {
			String currentItem = arrayNameValues[loop];
			String trimmedItem = currentItem.trim();

			if (trimmedItem.startsWith(keyName)) {
				int colonPosition = trimmedItem.indexOf(':');
				retVal = trimmedItem.substring(colonPosition + 1).trim();
				break;

			}
		}
		return retVal;
	}

	/**
	 * returns index in stdOutputOfGeneral of the instance'th of keyName where value is value (list is of format keyName : value)
	 * 
	 * @param keyName
	 *            value on lhs of key : value entry
	 * @param value
	 *            value to match for the keyName
	 * @param instanceRequired
	 *            the nth instance of keyName with value, this allows us to find the 3rd audio track for instance.
	 * @return index if found, -1 if not found.
	 */
	private int findIndexWithValue(String[] arrayNameValues, String keyName, String valueRequired, int instanceRequired) {
		int instancesFoundSoFar = 0;
		int retVal = -1;

		for (int loop = 0; loop < arrayNameValues.length; loop++) {
			String currentItem = arrayNameValues[loop];
			String value = "";
			String trimmedItem = currentItem.trim();

			if (trimmedItem.startsWith(keyName)) {
				int colonPosition = trimmedItem.indexOf(':');
				value = trimmedItem.substring(colonPosition + 1).trim();
				if (value.equalsIgnoreCase(valueRequired)) {
					instancesFoundSoFar++;
					if (instanceRequired == instancesFoundSoFar) {
						retVal = loop;
						break;
					}
				}

			}
		}

		return retVal;
	}

	private AudiosamplingfrequencyDt getAudioSamplingFrequency(String samplingRate) {
		AudiosamplingfrequencyDt audiosamplingfrequencyDt = ObjectFactoryConstants.DPP_OBJECT_FACTORY.createAudiosamplingfrequencyDt();
		audiosamplingfrequencyDt.setValue(BigInteger.valueOf(Integer.parseInt(samplingRate)));
		audiosamplingfrequencyDt.setUnit(CommandLineConstants.AUDIO_FREQUENCY_UNIT);

		return audiosamplingfrequencyDt;
	}

	private VideobitrateDt getVideoBitRate(int value) {

		VideobitrateDt videobitrateDt = ObjectFactoryConstants.DPP_OBJECT_FACTORY.createVideobitrateDt();
		videobitrateDt.setValue(BigInteger.valueOf(value));
		videobitrateDt.setUnit(CommandLineConstants.VIDEO_BIT_RATE_UNIT);

		return videobitrateDt;
	}
}

package dpp.validators;

import java.math.BigInteger;

import org.apache.log4j.Logger;

import dpp.bmx.CommandLineConstants;
import dpp.reporting.StatusReport;
import dpp.schema.FpapassEnum;
import dpp.schema.Programme;
import dpp.schema.ThreedtypeEnum;
import dpp.schema.Video;
import dpp.schema.VideobitrateDt;
import dpp.valuemaps.PictureRatioMapper;

public class VideoValidator {

	private static final Logger LOGGER = Logger.getLogger(VideoValidator.class);

	public boolean validateVideo(final Programme programme, NonProgrammeData nonProgrammeData, StatusReport statusReport, boolean validateOnlyStructuralData) {
		// Structural metadata is that which we can populate using mfx2raw -i, ie we should always be able to populate it.
		boolean validVideo = false;

		boolean validVideoStructuralMetadata = validateVideoStructuralMetadata(programme, nonProgrammeData, statusReport);
		if (validateOnlyStructuralData) {
			validVideo = validVideoStructuralMetadata;
		} else {
			boolean validVideoNonStructuralMetadata = validateVideoNonStructuralMetadata(programme, statusReport);
			validVideo = validVideoStructuralMetadata && validVideoNonStructuralMetadata;
		}

		return validVideo;
	}

	protected boolean validateVideoNonStructuralMetadata(Programme programme, StatusReport statusReport) {
		// ThreeDType Must be set when "ThreeD" = true; must not when "ThreeD" = false.
		// FPAManufacturer and FPAVersion Must be set when "FPAPass" = "Yes" or "No"; must not when "FPAPass" = "Not tested".
		boolean validVideoNonStructuralMetadata = true;
		Video video = programme.getTechnical().getVideo();

		boolean validThreeD = validateThreeD(video, statusReport);

		boolean validFpa = validateFpa(video, statusReport);

		boolean validPictureRatio = validatePictureRatio(video, statusReport);

		validVideoNonStructuralMetadata = validThreeD && validFpa && validPictureRatio;
		return validVideoNonStructuralMetadata;
	}

	protected boolean validateFpa(Video video, StatusReport statusReport) {
		boolean validFPA = true;
		String fpaManufacturer = video.getFPAManufacturer();
		FpapassEnum fpaPass = video.getFPAPass();
		String fpaVersion = video.getFPAVersion();
		if (fpaPass == null || fpaPass == FpapassEnum.NOT_TESTED) {
			if (fpaManufacturer != null || fpaVersion != null) {
				statusReport.reportValidationError("FPA details specified for non-FPA programme.");
				validFPA = false;
			}
		} else {
			boolean validFpaManufacturer = validateFpaManufacturer(fpaManufacturer, statusReport);
			boolean validFpaVersion = validateFpaVersion(fpaVersion, statusReport);
			validFPA = validFpaManufacturer && validFpaVersion;
		}
		return validFPA;
	}

	// If set, must be 3.4 or above.
	protected boolean validateFpaVersion(String fpaVersion, StatusReport statusReport) {
		if (fpaVersion == null || fpaVersion.length() == 0) {
			statusReport.reportValidationError("FPA version not specified for FPA programme.");
			return false;
		}

		// get the digits before the decimal point and first digit after
		try {
			int indexOfDecimalPoint = fpaVersion.indexOf(".");
			if (-1 == indexOfDecimalPoint) {
				indexOfDecimalPoint = fpaVersion.length() - 1; // handles integer case
			}
			String versionWithOneDecimalPlace = fpaVersion.substring(0, indexOfDecimalPoint + 2);
			float fpaVersionWithOneDecimalPlace = Float.parseFloat(versionWithOneDecimalPlace);
			if (fpaVersionWithOneDecimalPlace < 3.4) {
				statusReport.reportValidationError("FPA version must at least 3.4");
				return false;
			}
		} catch (Exception e) {
			statusReport.reportValidationError("FPA version invalid");
			return false;
		}

		return true;

	}

	protected boolean validateFpaManufacturer(String fpaManufacturer, StatusReport statusReport) {
		if (fpaManufacturer == null || fpaManufacturer.isEmpty()) {
			statusReport.reportValidationError("FPA manufacturer not specified for FPA programme.");
			return false;
		}
		if (!fpaManufacturer.contains("Harding")) {
			statusReport.reportValidationError("FPA manufacturer not valid for FPA programme.");
			return false;
		}
		return true;
	}

	protected boolean validateThreeD(Video video, StatusReport statusReport) {
		boolean validThreeD = true;
		boolean threeD = video.isThreeD();
		ThreedtypeEnum threeDType = video.getThreeDType();
		if (threeD) {
			if (threeDType == null) {
				statusReport.reportValidationError("No 3D Type for 3D programme.");
				validThreeD = false;
			}
		} else {
			if (threeDType != null) {
				statusReport.reportValidationError("3D Type specified for non-3D programme.");
				validThreeD = false;
			}
		}
		return validThreeD;
	}

	protected boolean validatePictureRatio(Video video, StatusReport statusReport) {
		String pictureRatio = video.getPictureRatio();
		if (pictureRatio == null || pictureRatio.isEmpty()) {
			return true;
		}

		boolean pictureRatioValid = PictureRatioMapper.isValidXmlPictureRatio(pictureRatio);
		if (!pictureRatioValid) {
			statusReport.reportValidationError("Picture ratio is not valid.");
		}

		return pictureRatioValid;
	}

	// STRUCTURAL METADATA VALIDATION

	protected boolean validateVideoStructuralMetadata(Programme programme, NonProgrammeData nonProgrammeData, StatusReport statusReport) {
		boolean validVideoEssence = validateVideoEssence(nonProgrammeData, statusReport);
		boolean validVideoBitrateCodecAndParams = validateVideoBitrateCodecAndParams(programme, statusReport);
		boolean validPictureFormatSection = validatePictureFormatSection(programme, statusReport);
		boolean validEditRate = validateEditRate(nonProgrammeData, statusReport);
		boolean validAfd = validateAfd(programme, statusReport);
		return validVideoEssence && validVideoBitrateCodecAndParams && validPictureFormatSection && validEditRate && validAfd;
	}

	protected boolean validateVideoEssence(NonProgrammeData nonProgrammeData, StatusReport statusReport) {
		boolean validVideoEssence = true;

		String videoEssenceTypeUpper = nonProgrammeData.getVideoEssenceType().toUpperCase();
		boolean tenEightyInterlaced = videoEssenceTypeUpper.contains(CommandLineConstants.TEN_EIGHTY_INTERLACED.toUpperCase());
		// boolean sevenTwentyInterlaced = videoEssenceTypeUpper.contains(CommandLineConstants.SEVEN_TWENTY_INTERLACED.toUpperCase());
		boolean avci = videoEssenceTypeUpper.contains(CommandLineConstants.AVC.toUpperCase());
		boolean d10 = videoEssenceTypeUpper.contains(CommandLineConstants.MXF2RAW_D10_CODEC.toUpperCase());
		boolean imx = videoEssenceTypeUpper.contains(CommandLineConstants.MXF2RAW_IMX_CODEC.toUpperCase());

		if (avci && (!tenEightyInterlaced)) {
			statusReport.reportValidationError("Invalid video essence for AVCi (" + nonProgrammeData.getVideoEssenceType() + ")");
			validVideoEssence = false;
		} else if (!(d10 || imx || avci)) {
			statusReport.reportValidationError("Invalid video essence (not AVCI or D10)");
			validVideoEssence = false;
		}

		return validVideoEssence;
	}

	/*
	 * ok if ((Video Codec == "AVCi" && Picture Format == "1920x1080") or (Video Codec == "D10" && Picture Format == "720x576" || "702x576"))
	 */
	protected boolean validatePictureFormatSection(final Programme programme, StatusReport statusReport) {
		boolean validPictureFormatSection = true;
		String videoCodec = programme.getTechnical().getVideo().getVideoCodec();
		String pictureFormat = programme.getTechnical().getVideo().getPictureFormat();

		try {
			if (videoCodec.equalsIgnoreCase(CommandLineConstants.DPP_AVCI_CODEC)) { // HD
				if (!pictureFormat.equalsIgnoreCase(CommandLineConstants.PICTURE_FORMAT_1080I)) {
					statusReport.reportValidationError("Invalid picture format for AVCI (" + pictureFormat + ")");
					validPictureFormatSection = false;
				}
			} else if (videoCodec.equalsIgnoreCase(CommandLineConstants.DPP_D10_CODEC)) {
				if (!pictureFormat.equalsIgnoreCase(CommandLineConstants.PICTURE_FORMAT_576I_4_3)
						&& !pictureFormat.equals(CommandLineConstants.PICTURE_FORMAT_576I_16_9)) {
					statusReport.reportValidationError("Invalid picture format for IMX (" + pictureFormat + ")");
					validPictureFormatSection = false;
				}
			} else {
				statusReport.reportValidationError("Invalid video codec (" + videoCodec + ")");
				validPictureFormatSection = false;
			}
		} catch (Exception e) {
			LOGGER.error("Invalid picture format, caught exception", e);
			statusReport.reportValidationError("Invalid picture format.");
			validPictureFormatSection = false;
		}

		return validPictureFormatSection;

	}

	/**
	 * Check edit rate and report error if not correct (25).
	 */
	protected boolean validateEditRate(NonProgrammeData nonProgrammeData, StatusReport statusReport) {
		boolean validEditRate = true;
		String editRate = nonProgrammeData.getEditRate();
		boolean editRateOk = editRate.equalsIgnoreCase(CommandLineConstants.EDIT_RATE_VALUE) || editRate.equalsIgnoreCase(CommandLineConstants.EDIT_RATE_RATIO);
		if (!editRateOk) {
			statusReport.reportValidationError("Invalid edit rate found for video track [" + editRate + "]");
			validEditRate = false;
		}
		return validEditRate;
	}

	/**
	 * Valid if:
	 * 
	 * Video Codec = "AVCi", Codec Parameters = �High 4:2:2 level 4.1�, bit rate = �100Mbps� OR Video Codec = "D10", Codec Parameters = 4:2:2 P@ML�, bit rate =
	 * �50Mbps�
	 */
	protected boolean validateVideoBitrateCodecAndParams(final Programme programme, StatusReport statusReport) {
		boolean validVideoEssence = true;
		String videoCodec = programme.getTechnical().getVideo().getVideoCodec();
		VideobitrateDt videoBitRateType = programme.getTechnical().getVideo().getVideoBitRate();
		String videoBitRate = String.valueOf(videoBitRateType.getValue());
		String videoCodecParameters = programme.getTechnical().getVideo().getVideoCodecParameters();

		if (videoCodec != null) {
			if (videoCodec.equalsIgnoreCase(CommandLineConstants.MXF2RAW_AVC_INTRA_CODEC)) {
				if (!videoCodecParameters.equals(CommandLineConstants.DPP_AVCI_VIDEO_CODEC_PARAMETERS)) {
					statusReport.reportValidationError("Unrecognised video codec parameters for AVCi (" + videoCodecParameters + ")");
					validVideoEssence = false;
				}
				if (!videoBitRate.equalsIgnoreCase(CommandLineConstants.ONE_HUNDRED)) {
					statusReport.reportValidationError("Unrecognised video bit rate for AVCi (" + videoBitRate + "Mbps)");
					validVideoEssence = false;
				}
			} else if (videoCodec.equalsIgnoreCase(CommandLineConstants.MXF2RAW_D10_CODEC)) {
				if (!videoCodecParameters.equals(CommandLineConstants.DPP_D10_VIDEO_CODEC_PARAMETERS)) {
					statusReport.reportValidationError("Unrecognised video codec parameters for D10 (" + videoCodecParameters + ")");
					validVideoEssence = false;
				}
				if (!videoBitRate.equalsIgnoreCase(CommandLineConstants.FIFTY)) {
					statusReport.reportValidationError("Unrecognised video bit rate for D10 (" + videoBitRate + "Mbps)");
					validVideoEssence = false;
				}
			} // note - invalid video codec is checked in validatePictureFormatSection
		}

		return validVideoEssence;
	}

	/**
	 * Validate AFD Must be 9, 10 or 14. If "PictureRatio" is set, AFD must be 9 when "PictureRatio" < 16/9, 10 when "PictureRatio" > 16/9, or 10 or 14 when
	 * "PictureRatio" = 16/9.
	 */
	protected boolean validateAfd(final Programme programme, StatusReport statusReport) {

		BigInteger afdType = programme.getTechnical().getVideo().getAFD();
		int afd = afdType != null ? afdType.intValue() : 0;
		boolean validAfd = afd == 9 || afd == 10 || afd == 14;

		if (!validAfd) {
			statusReport.reportValidationError("Invalid AFD (" + afd + "), must be 9, 10 or 14");
			return validAfd;
		}

		String pictureRatio = programme.getTechnical().getVideo().getPictureRatio();
		if (pictureRatio != null && !pictureRatio.isEmpty()) {
			if (pictureRatio.startsWith(PictureRatioMapper.XML_START_PICTURE_RATIO_4_3)
					|| pictureRatio.startsWith(PictureRatioMapper.XML_START_PICTURE_RATIO_14_9)
					|| pictureRatio.startsWith(PictureRatioMapper.XML_START_PICTURE_RATIO_5_3)) {

				validAfd = afd == 9;
				if (!validAfd) {
					statusReport.reportValidationError("Invalid AFD (" + afd + "), must be 9 with picture ratio " + pictureRatio);
				}
			} else if (pictureRatio.startsWith(PictureRatioMapper.XML_START_PICTURE_RATIO_16_9)) {

				validAfd = afd == 10 || afd == 14;
				if (!validAfd) {
					statusReport.reportValidationError("Invalid AFD (" + afd + "), must be 10 or 14 with picture ratio " + pictureRatio);
				}
			} else if (pictureRatio.startsWith(PictureRatioMapper.XML_START_PICTURE_RATIO_37_20)
					|| pictureRatio.startsWith(PictureRatioMapper.XML_START_PICTURE_RATIO_7_3)
					|| pictureRatio.startsWith(PictureRatioMapper.XML_START_PICTURE_RATIO_12_5)) {

				validAfd = afd == 10;
				if (!validAfd) {
					statusReport.reportValidationError("Invalid AFD (" + afd + "), must be 10 with picture ratio " + pictureRatio);
				}
			}
		}

		return validAfd;
	}
	// END OF STRUCTURAL METADATA VALIDATION

}

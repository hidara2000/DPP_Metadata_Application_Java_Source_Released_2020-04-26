package dpp.valuemaps;

import dpp.bmx.CommandLineConstants;

/**
 * BMX reports picture ratios as a rational number e.g. 37/20 and xml/gui does them as real:int (n:1) e.g. 16.65:9 (1.85:1)
 */
public class PictureRatioMapper {

	// Picture Ratios - as stored in the xml and reported to the user.
	public static final String XML_PICTURE_RATIO_4_3 = "4:3 (1.33:1)";
	public static final String XML_PICTURE_RATIO_14_9 = "14:9 (1.55:1)";
	public static final String XML_PICTURE_RATIO_5_3 = "15:9 (1.66:1)";
	public static final String XML_PICTURE_RATIO_16_9 = "16:9 (1.78:1)";
	public static final String XML_PICTURE_RATIO_37_20 = "16.65:9 (1.85:1)";
	public static final String XML_PICTURE_RATIO_7_3 = "21:9 (2.33:1)";
	public static final String XML_PICTURE_RATIO_12_5 = "21.6:9 (2.40:1)";

	public static final String XML_START_PICTURE_RATIO_4_3 = "4:3";
	public static final String XML_START_PICTURE_RATIO_14_9 = "14:9";
	public static final String XML_START_PICTURE_RATIO_5_3 = "15:9";
	public static final String XML_START_PICTURE_RATIO_16_9 = "16:9";
	public static final String XML_START_PICTURE_RATIO_37_20 = "16.65:9";
	public static final String XML_START_PICTURE_RATIO_7_3 = "21:9";
	public static final String XML_START_PICTURE_RATIO_12_5 = "21.6:9";

	public static boolean isValidXmlPictureRatio(final String xmlRatio) {
		boolean validRatio = xmlRatio.startsWith(XML_START_PICTURE_RATIO_4_3) || xmlRatio.startsWith(XML_START_PICTURE_RATIO_14_9)
				|| xmlRatio.startsWith(XML_START_PICTURE_RATIO_5_3) || xmlRatio.startsWith(XML_START_PICTURE_RATIO_16_9)
				|| xmlRatio.startsWith(XML_START_PICTURE_RATIO_37_20) || xmlRatio.startsWith(XML_START_PICTURE_RATIO_7_3)
				|| xmlRatio.startsWith(XML_START_PICTURE_RATIO_12_5);
		return validRatio;
	}

	public static String getXmlPictureRatioFromRationalPictureRatio(final String ratioAsRational) {
		if (ratioAsRational.equals(CommandLineConstants.BMX_PICTURE_RATIO_4_3)) {
			return XML_PICTURE_RATIO_4_3;
		}
		if (ratioAsRational.equals(CommandLineConstants.BMX_PICTURE_RATIO_14_9)) {
			return XML_PICTURE_RATIO_14_9;
		}
		if (ratioAsRational.equals(CommandLineConstants.BMX_PICTURE_RATIO_5_3)) {
			return XML_PICTURE_RATIO_5_3;
		}
		if (ratioAsRational.equals(CommandLineConstants.BMX_PICTURE_RATIO_16_9)) {
			return XML_PICTURE_RATIO_16_9;
		}
		if (ratioAsRational.equals(CommandLineConstants.BMX_PICTURE_RATIO_37_20)) {
			return XML_PICTURE_RATIO_37_20;
		}
		if (ratioAsRational.equals(CommandLineConstants.BMX_PICTURE_RATIO_7_3)) {
			return XML_PICTURE_RATIO_7_3;
		}
		if (ratioAsRational.equals(CommandLineConstants.BMX_PICTURE_RATIO_12_5)) {
			return XML_PICTURE_RATIO_12_5;
		}

		return "UNKNOWN PICTURE RATIO (" + ratioAsRational + ")";

	}

	public static String getRationalPictureRatioFromXmlPictureRatio(final String xmlRatio) {
		if (xmlRatio.startsWith(XML_START_PICTURE_RATIO_4_3)) {
			return CommandLineConstants.BMX_PICTURE_RATIO_4_3;
		}
		if (xmlRatio.startsWith(XML_START_PICTURE_RATIO_14_9)) {
			return CommandLineConstants.BMX_PICTURE_RATIO_14_9;
		}
		if (xmlRatio.startsWith(XML_START_PICTURE_RATIO_5_3)) {
			return CommandLineConstants.BMX_PICTURE_RATIO_5_3;
		}
		if (xmlRatio.startsWith(XML_START_PICTURE_RATIO_16_9)) {
			return CommandLineConstants.BMX_PICTURE_RATIO_16_9;
		}
		if (xmlRatio.startsWith(XML_START_PICTURE_RATIO_37_20)) {
			return CommandLineConstants.BMX_PICTURE_RATIO_37_20;
		}
		if (xmlRatio.startsWith(XML_START_PICTURE_RATIO_7_3)) {
			return CommandLineConstants.BMX_PICTURE_RATIO_7_3;
		}
		if (xmlRatio.startsWith(XML_START_PICTURE_RATIO_12_5)) {
			return CommandLineConstants.BMX_PICTURE_RATIO_12_5;
		}

		return "UNKNOWN PICTURE RATIO (" + xmlRatio + ")";

	}

}

package dpp.bmx;

public class CommandLineConstants {

	// General
	public static final String PCM = "PCM";
	public static final String MXF2RAW_D10_AES3_PCM = "D10 AES3 PCM";
	public static final String DPP_D10_AES3_PCM = "AES3";
	public static final String AUDIO_BIT_DEPTH = "24";
	public static final String DURATION = "Duration";
	public static final String ESSENCE_KIND = "Essence kind";
	public static final String ESSENCE_TYPE = "Essence type";
	public static final String MATERIAL_START_TIMECODE = "Material start timecode";
	public static final String STORED_DIMENSIONS = "Stored dimensions";
	public static final String DISPLAY_DIMENSIONS = "Display dimensions";
	public static final String AFD = "AFD";
	public static final String ASPECT_RATIO = "Aspect ratio";
	public static final String SAMPLING_RATE = "Sampling rate";
	public static final String BITS_PER_SAMPLE = "Bits per sample";
	public static final String AVC = "AVC";
	public static final String MXF2RAW_AVCI_CODEC = "AVCi";
	public static final String MXF2RAW_AVC_INTRA_CODEC = "AVC-Intra";
	public static final String MXF2RAW_D10_CODEC = "D10";
	public static final String MXF2RAW_IMX_CODEC = "IMX";
	public static final String DPP_D10_CODEC = "D10 50";
	public static final String DPP_AVCI_CODEC = "AVC-Intra 100";
	public static final String DPP_AVCI_VIDEO_CODEC_PARAMETERS = "High 4:2:2 Intra@L4.1";
	public static final String DPP_D10_VIDEO_CODEC_PARAMETERS = "4:2:2 P@ML";
	public static final String VIDEO_BIT_RATE_UNIT = "Mbps";
	public static final String ONE_HUNDRED_MBPS = "100Mbps";
	public static final String ONE_HUNDRED = "100";
	public static final int ONE_HUNDRED_INT = 100;
	public static final long ONE_HUNDRED_MBPS_LONG = 100000000;
	public static final String FIFTY_MBPS = "50Mbps";
	public static final String FIFTY = "50";
	public static final int FIFTY_INT = 50;
	public static final long FIFTY_MBPS_LONG = 50000000;
	public static final String SOUND = "Sound";
	public static final String PICTURE = "Picture";
	public static final String NOT_SET = "not set";
	public static final String NOT_SET_PARENTHESIS = "(not set)";
	public static final String AUDIO_SAMPLING_RATE_RATIO = "48000/1";
	public static final String AUDIO_SAMPLING_RATE = "48000";
	public static final String AUDIO_SAMPLING_RATE_KHZ = "48";
	public static final String AUDIO_FREQUENCY_UNIT = "kHz";
	public static final long AUDIO_BIT_DEPTH_VALUE = 24;
	public static final String CHANNEL_COUNT = "Channel count";
	public static final String EDIT_RATE = "Edit rate";
	public static final String AVCI_HEADER = "AVCI header";
	public static final String TEN_EIGHTY_INTERLACED = "1080i";
	public static final String SEVEN_TWENTY_INTERLACED = "720i";
	public static final String EDIT_RATE_RATIO = "25/1";
	public static final String EDIT_RATE_VALUE = "25";

	// As11/UkDpp
	public static final String PRODUCTION_NUMBER = "ProductionNumber";
	public static final String SYNOPSIS = "Synopsis";
	public static final String ORIGINATOR = "Originator";
	public static final String COPYRIGHT_YEAR = "CopyrightYear";
	public static final String DISTRIBUTOR = "Distributor";
	public static final String PICTURE_RATIO = "PictureRatio";
	public static final String THREED = "ThreeD";
	public static final String PRODUCT_PLACEMENT = "ProductPlacement";
	public static final String FPA_PASS = "FPAPass";
	public static final String FPA_MANUFACTURER = "FPAManufacturer";
	public static final String FPA_VERSION = "FPAVersion";
	public static final String SECONDARY_AUDIO_LANGUAGE = "SecondaryAudioLanguage";
	public static final String TERTIARY_AUDIO_LANGUAGE = "TertiaryAudioLanguage";
	public static final String AUDIO_LOUDNESS_STANDARD = "AudioLoudnessStandard";
	public static final String LINE_UP_START = "LineUpStart ";
	public static final String IDENT_CLOCK_START = "IdentClockStart";
	public static final String AUDIO_DESCRIPTION_PRESENT = "AudioDescriptionPresent";
	public static final String OPEN_CAPTIONS_PRESENT = "OpenCaptionsPresent";
	public static final String OPEN_CAPTIONS_TYPE = "OpenCaptionsType";
	public static final String OPEN_CAPTIONS_LANGUAGE = "OpenCaptionsLanguage";
	public static final String SIGNING_PRESENT = "SigningPresent";
	public static final String COMPLETION_DATE = "CompletionDate";
	public static final String CONTACT_EMAIL = "ContactEmail";
	public static final String CONTACT_TELEPHONE_NUMBER = "ContactTelephoneNumber";
	public static final String SERIES_TITLE = "SeriesTitle";
	public static final String PROGRAMME_TITLE = "ProgrammeTitle";
	public static final String EPISODE_TITLE_NUMBER = "EpisodeTitleNumber";
	public static final String SHIM_NAME = "ShimName";
	public static final String AUDIO_TRACK_LAYOUT = "AudioTrackLayout";
	public static final String PRIMARY_AUDIO_LANGUAGE = "PrimaryAudioLanguage";
	public static final String CLOSED_CAPTIONS_PRESENT = "ClosedCaptionsPresent";
	public static final String CLOSED_CAPTIONS_TYPE = "ClosedCaptionsType";
	public static final String CLOSED_CAPTIONS_LANGUAGE = "ClosedCaptionsLanguage";
	public static final String OTHER_IDENTIFIER = "OtherIdentifier";
	public static final String OTHER_IDENTIFIER_TYPE = "OtherIdentifierType";
	public static final String GENRE = "Genre";
	public static final String THREED_TYPE = "ThreeDType";
	public static final String VIDEO_COMMENTS = "VideoComments";
	public static final String AUDIO_COMMENTS = "AudioComments";
	public static final String TOTAL_PROGRAMME_DURATION = "TotalProgrammeDuration";
	public static final String TOTAL_NUMBER_OF_PARTS = "TotalNumberOfParts";
	public static final String AUDIO_DESCRIPTION_TYPE = "AudioDescriptionType";
	public static final String SIGN_LANGUAGE = "SignLanguage";
	public static final String TEXTLESS_ELEMENTS_EXIST = "TextlessElementsExist";
	public static final String PROGRAMME_HAS_TEXT = "ProgrammeHasText";
	public static final String PROGRAMME_TEXT_LANGUAGE = "ProgrammeTextLanguage";

	public static final String PICTURE_FORMAT_1080I = "1080i50 16:9";
	public static final String PICTURE_FORMAT_576I_4_3 = "576i50 4:3";
	public static final String PICTURE_FORMAT_576I_16_9 = "576i50 16:9";
	public static final String ASPECT_RATIO_14_9 = "14/9";
	public static final String ASPECT_RATIO_16_9 = "16/9";
	public static final String ASPECT_RATIO_4_3 = "4/3";

	// Picture Ratios - as reported and used by bmx
	public static final String BMX_PICTURE_RATIO_4_3 = "4/3";
	public static final String BMX_PICTURE_RATIO_14_9 = "14/9";
	public static final String BMX_PICTURE_RATIO_5_3 = "5/3";
	public static final String BMX_PICTURE_RATIO_16_9 = "16/9";
	public static final String BMX_PICTURE_RATIO_37_20 = "37/20";
	public static final String BMX_PICTURE_RATIO_7_3 = "7/3";
	public static final String BMX_PICTURE_RATIO_12_5 = "12/5";

}

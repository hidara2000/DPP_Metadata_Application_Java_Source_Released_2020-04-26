package dpp.enums;

public enum CommandTypeEnum {

	EXTRACT, GENERATE_MXF_AND_XML, GENERATE_SIDECAR, VALIDATE_XML, VALIDATE_DPP, VALIDATE_EDITORIAL, VALIDATE_VIDEO, VALIDATE_TIMECODE, VALIDATE_OTHERS, VALIDATE_AUDIO, UNKNOWN;

	public String value() {
		return name();
	}

	public static CommandTypeEnum fromValue(final String value) {
		CommandTypeEnum commandTypeEnum = UNKNOWN;
		try {
			commandTypeEnum = valueOf(value.trim().toUpperCase());
		} catch (Exception e) {
			commandTypeEnum = UNKNOWN;
		}

		return commandTypeEnum;
	}

}

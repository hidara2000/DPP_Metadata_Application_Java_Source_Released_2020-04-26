package dpp.enums;

public enum MxfTypeEnum {

	OP1A, AS11, DPP, UNKNOWN;

	public String value() {
		return name();
	}

	public static MxfTypeEnum fromValue(final String value) {
		MxfTypeEnum mxfType = UNKNOWN;
		try {
			mxfType = valueOf(value.trim().toUpperCase());
		} catch (Exception e) {
			mxfType = UNKNOWN;
		}

		return mxfType;
	}

}

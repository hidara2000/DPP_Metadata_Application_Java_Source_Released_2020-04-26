package dpp.valuemaps;

import org.apache.log4j.Logger;

import dpp.schema.SignlanguageEnum;

public class UkDppSignLanguageMap extends BaseTypeMap {
	private static final Logger LOGGER = Logger.getLogger(UkDppSignLanguageMap.class);

	public UkDppSignLanguageMap() {
		super();
		mapOfValuesToAmwaNum.put(0, "BSL (British Sign Language)");
		mapOfValuesToAmwaNum.put(1, "BSL (Makaton)");
	}

	public SignlanguageEnum getSignlanguageEnumFromValue(final String signingLanguage) {
		SignlanguageEnum signlanuageEnum = SignlanguageEnum.BSL_BRITISH_SIGN_LANGUAGE;

		try {
			signlanuageEnum = SignlanguageEnum.fromValue(signingLanguage);
		} catch (IllegalArgumentException iae) {
			LOGGER.error("Unexpected signing language: " + signingLanguage + ", defaulting to British Sign Language");
		}

		return signlanuageEnum;
	}
}

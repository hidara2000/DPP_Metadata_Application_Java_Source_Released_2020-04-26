package dpp.valuemaps;

import org.apache.log4j.Logger;

import dpp.schema.SigningEnum;

public class UkDppSigningPresentMap extends BaseTypeMap {
	private static final Logger LOGGER = Logger.getLogger(UkDppSigningPresentMap.class);

	public UkDppSigningPresentMap() {
		super();
		mapOfValuesToAmwaNum.put(0, "Yes");
		mapOfValuesToAmwaNum.put(1, "No");
		mapOfValuesToAmwaNum.put(2, "Signer only");
	}

	public SigningEnum getSigningEnumFromValue(final String signingPresent) {
		SigningEnum signingEnum = SigningEnum.NO;

		try {
			signingEnum = SigningEnum.fromValue(signingPresent);
		} catch (IllegalArgumentException iae) {
			LOGGER.error("Unexpected signing present: " + signingPresent + ", defaulting to No");
		}

		return signingEnum;
	}

}

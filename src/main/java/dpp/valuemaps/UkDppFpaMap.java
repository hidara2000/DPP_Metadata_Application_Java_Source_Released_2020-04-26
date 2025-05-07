package dpp.valuemaps;

import org.apache.log4j.Logger;

import dpp.schema.FpapassEnum;

public class UkDppFpaMap extends BaseTypeMap {
	private static final Logger LOGGER = Logger.getLogger(UkDppFpaMap.class);

	public UkDppFpaMap() {
		super();
		mapOfValuesToAmwaNum.put(0, "Yes");
		mapOfValuesToAmwaNum.put(1, "No");
		mapOfValuesToAmwaNum.put(2, "Not tested");
	}

	public FpapassEnum getFpapassEnumFromValue(final String fpaPass) {
		FpapassEnum fpapassEnum = FpapassEnum.YES;

		try {
			fpapassEnum = FpapassEnum.fromValue(fpaPass);
		} catch (IllegalArgumentException iae) {
			LOGGER.error("Unexpected fpaPass: " + fpaPass + ", defaulting to Yes");
		}

		return fpapassEnum;
	}
}

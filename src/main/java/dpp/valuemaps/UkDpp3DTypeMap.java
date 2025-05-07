package dpp.valuemaps;

import org.apache.log4j.Logger;

import dpp.schema.ThreedtypeEnum;

public class UkDpp3DTypeMap extends BaseTypeMap {
	private static final Logger LOGGER = Logger.getLogger(UkDpp3DTypeMap.class);

	public UkDpp3DTypeMap() {
		super();
		mapOfValuesToAmwaNum.put(0, "Side by side");
		mapOfValuesToAmwaNum.put(1, "Dual");
		mapOfValuesToAmwaNum.put(2, "Left eye only");
		mapOfValuesToAmwaNum.put(3, "Right eye only");
	}

	public ThreedtypeEnum getThreedtypeEnumFromValue(final String threeDType) {
		ThreedtypeEnum threedtypeEnum = ThreedtypeEnum.DUAL;

		try {
			threedtypeEnum = ThreedtypeEnum.fromValue(threeDType);
		} catch (IllegalArgumentException iae) {
			LOGGER.error("Unexpected 3D Type: " + threeDType + ", defaulting to Dual");
		}

		return threedtypeEnum;
	}
}

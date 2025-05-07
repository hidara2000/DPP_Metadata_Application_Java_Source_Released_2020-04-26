package dpp.valuemaps;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides base for mapping between numeric values for AS11/DPP values (used by command line bmx) and the equivalent text values (supported by the xsd/front
 * end) see http://www.amwa.tv/downloads/specifications/AMWA_AS-11_10_2012-01-18.pdf for enumerations.
 * 
 */
public abstract class BaseTypeMap {

	public static final int ITEM_NOT_FOUND = -999;
	protected Map<Integer, String> mapOfValuesToAmwaNum = new HashMap<Integer, String>();

	public int getIntValue(final String mxfEnumString) {
		for (Map.Entry<Integer, String> entry : mapOfValuesToAmwaNum.entrySet()) {
			String value = entry.getValue();
			if (value.equalsIgnoreCase(mxfEnumString)) {
				return entry.getKey();
			}
		}
		return ITEM_NOT_FOUND;

	}

	public String getStringValue(final Integer mxfIntEnum) {
		if (mxfIntEnum == null) {
			return null;
		}
		String stringValue = mapOfValuesToAmwaNum.get(mxfIntEnum);
		if (stringValue == null) {
			return "";
		}
		return stringValue;
	}

}

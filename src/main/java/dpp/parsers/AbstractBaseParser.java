package dpp.parsers;

import java.util.List;

import org.apache.log4j.Logger;

import dpp.bmx.CommandLineConstants;

public abstract class AbstractBaseParser {
	public static final int ITEM_NOT_FOUND = -999;
	private static final Logger LOGGER = Logger.getLogger(AbstractBaseParser.class);

	/**
	 * This method assumes that the listFieldsAndValues is a list where each line is of the format "  field_name: value". White space is not predictable. No
	 * consideration is given as to any nesting or ordering of values.
	 * 
	 * @param field
	 * @param listFieldsAndValues
	 * @return
	 */
	public String getValueForField(String field, List<String> listFieldsAndValues) {
		String fieldWithASpace = field + " "; // stops us confusing ThreeD and ThreeDType for instance.
		String value = "";
		for (String currentItem : listFieldsAndValues) {
			String trimmedItem = currentItem.trim();

			if (trimmedItem.startsWith(fieldWithASpace)) {
				int colonPosition = trimmedItem.indexOf(':');
				value = trimmedItem.substring(colonPosition + 1).trim();
			}

		}

		return value;
	}

	public Boolean getValueForFieldAsBoolean(String field, List<String> listFieldsAndValues) {
		Boolean returnValue = null;
		String rawValue = getValueForField(field, listFieldsAndValues);
		if (rawValue != null && rawValue.equalsIgnoreCase("true")) {
			returnValue = true;
		} else if (rawValue != null && rawValue.equalsIgnoreCase("false")) {
			returnValue = false;
		}

		return returnValue;
	}

	/**
	 * This method will take input of the following form e.g. "      FPAPass                   : 1 (No)" and strip out the numeric value - in this case 1 which
	 * can then be used to look up the text value using one of the valuemaps e.g. UkDppFpaMap
	 */
	public Integer getValueForFieldAsIntegerEnumValue(String field, List<String> listFieldsAndValues) {
		Integer intValue = null; // ITEM_NOT_FOUND;
		try {
			String enumNumber = getValueForFieldWithoutHumanReadablePart(field, listFieldsAndValues);
			if (!enumNumber.isEmpty()) {
				intValue = Integer.parseInt(enumNumber);
			}
		} catch (Exception e) {
			LOGGER.error("Failed to parse number value for field [" + field + "]", e);
			intValue = ITEM_NOT_FOUND;
		}

		return intValue;

	}

	/**
	 * Fields are often reported by the command line with an initial primary value followed by clarifying info in parentheses, this method returns just the
	 * primary value, e.g. "      LineUpStart               : 10:00:00:00 (0 offset)" would return just the 10:00:00:00 part
	 */
	public String getValueForFieldWithoutHumanReadablePart(String field, List<String> listFieldsAndValues) {
		String primaryValue = "";
		try {
			String rawValue = getValueForField(field, listFieldsAndValues);
			// so now we have something like 10:00:00:00 (0 offset)
			int leftParamPosition = rawValue.indexOf('(');
			if (leftParamPosition >= 0) {
				primaryValue = rawValue.substring(0, leftParamPosition).trim();
			}
		} catch (Exception e) {
			LOGGER.error("Failed to get primary value for field [" + field + "]", e);
		}
		return primaryValue;

	}

	protected boolean checkValueIsSet(String value) {
		return ((value != null) && !value.isEmpty() && !value.equalsIgnoreCase(CommandLineConstants.NOT_SET)
				& !value.equalsIgnoreCase(CommandLineConstants.NOT_SET_PARENTHESIS));
	}

	protected boolean checkValueIsSet(Boolean value) {
		return (value != null);
	}

	protected boolean checkValueIsSet(Integer value) {
		return (value != null);
	}

}

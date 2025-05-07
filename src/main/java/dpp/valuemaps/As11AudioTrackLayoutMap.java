package dpp.valuemaps;

import org.apache.log4j.Logger;

import dpp.schema.AudiotracklayoutEnum;

public class As11AudioTrackLayoutMap extends BaseTypeMap {
	private static final Logger LOGGER = Logger.getLogger(As11AudioTrackLayoutMap.class);

	public As11AudioTrackLayoutMap() {
		super();
		mapOfValuesToAmwaNum.put(0, "EBU R 48: 1a");
		mapOfValuesToAmwaNum.put(1, "EBU R 48: 1b");
		mapOfValuesToAmwaNum.put(2, "EBU R 48: 1c");
		mapOfValuesToAmwaNum.put(3, "EBU R 48: 2a");
		mapOfValuesToAmwaNum.put(4, "EBU R 48: 2b");
		mapOfValuesToAmwaNum.put(5, "EBU R 48: 2c");
		mapOfValuesToAmwaNum.put(6, "EBU R 48: 3a");
		mapOfValuesToAmwaNum.put(7, "EBU R 48: 3b");
		mapOfValuesToAmwaNum.put(7, "EBU R 48: 4a");
		mapOfValuesToAmwaNum.put(9, "EBU R 48: 4b");
		mapOfValuesToAmwaNum.put(10, "EBU R 48: 4c");
		mapOfValuesToAmwaNum.put(11, "EBU R 48: 5a");
		mapOfValuesToAmwaNum.put(12, "EBU R 48: 5b");
		mapOfValuesToAmwaNum.put(13, "EBU R 48: 6a");
		mapOfValuesToAmwaNum.put(14, "EBU R 48: 6b");
		mapOfValuesToAmwaNum.put(15, "EBU R 48: 7a");
		mapOfValuesToAmwaNum.put(16, "EBU R 48: 7b");
		mapOfValuesToAmwaNum.put(17, "EBU R 48: 8a");
		mapOfValuesToAmwaNum.put(18, "EBU R 48: 8b");
		mapOfValuesToAmwaNum.put(19, "EBU R 48: 8c");
		mapOfValuesToAmwaNum.put(20, "EBU R 48: 9a");
		mapOfValuesToAmwaNum.put(21, "EBU R 48: 9b");
		mapOfValuesToAmwaNum.put(22, "EBU R 48: 10a");
		mapOfValuesToAmwaNum.put(23, "EBU R 48: 11a");
		mapOfValuesToAmwaNum.put(24, "EBU R 48: 11b");
		mapOfValuesToAmwaNum.put(25, "EBU R 48: 11c");
		mapOfValuesToAmwaNum.put(26, "EBU R 123: 2a");
		mapOfValuesToAmwaNum.put(27, "EBU R 123: 4a");
		mapOfValuesToAmwaNum.put(28, "EBU R 123: 4b");
		mapOfValuesToAmwaNum.put(29, "EBU R 123: 4c");
		mapOfValuesToAmwaNum.put(30, "EBU R 123: 8a");
		mapOfValuesToAmwaNum.put(31, "EBU R 123: 8b");
		mapOfValuesToAmwaNum.put(32, "EBU R 123: 8c");
		mapOfValuesToAmwaNum.put(33, "EBU R 123: 8d");
		mapOfValuesToAmwaNum.put(34, "EBU R 123: 8e");
		mapOfValuesToAmwaNum.put(35, "EBU R 123: 8f");
		mapOfValuesToAmwaNum.put(36, "EBU R 123: 8g");
		mapOfValuesToAmwaNum.put(37, "EBU R 123: 8h");
		mapOfValuesToAmwaNum.put(38, "EBU R 123: 8i");
		mapOfValuesToAmwaNum.put(39, "EBU R 123: 12a");
		mapOfValuesToAmwaNum.put(40, "EBU R 123: 12b");
		mapOfValuesToAmwaNum.put(41, "EBU R 123: 12c");
		mapOfValuesToAmwaNum.put(42, "EBU R 123: 12d");
		mapOfValuesToAmwaNum.put(43, "EBU R 123: 12e");
		mapOfValuesToAmwaNum.put(44, "EBU R 123: 12f");
		mapOfValuesToAmwaNum.put(45, "EBU R 123: 12g");
		mapOfValuesToAmwaNum.put(46, "EBU R 123: 12h");
		mapOfValuesToAmwaNum.put(47, "EBU R 123: 16a");
		mapOfValuesToAmwaNum.put(48, "EBU R 123: 16b");
		mapOfValuesToAmwaNum.put(49, "EBU R 123: 16c");
		mapOfValuesToAmwaNum.put(50, "EBU R 123: 16d");
		mapOfValuesToAmwaNum.put(51, "EBU R 123: 16e");
		mapOfValuesToAmwaNum.put(52, "EBU R 123: 16f");
		mapOfValuesToAmwaNum.put(255, "Undefined");

	}

	public AudiotracklayoutEnum getAudiotracklayoutEnumFromValue(final String audioTrackLayout) {
		AudiotracklayoutEnum audiotracklayoutEnum = AudiotracklayoutEnum.UNDEFINED;

		try {
			audiotracklayoutEnum = AudiotracklayoutEnum.fromValue(audioTrackLayout);
		} catch (IllegalArgumentException iae) {
			LOGGER.error("Unexpected audioTrackLayout: " + audioTrackLayout);
		}

		return audiotracklayoutEnum;
	}

}

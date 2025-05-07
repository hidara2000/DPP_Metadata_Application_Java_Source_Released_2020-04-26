package dpp.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import dpp.App;

public class FileUtils {
	private static final String AS11_CORE_SUFFIX_AND_EXTENSION = "_as11core.txt";
	private static final String UK_DPP_SUFFIX_AND_EXTENSION = "_ukdpp.txt";
	private static final String SEGMENTATION_SUFFIX_AND_EXTENSION = "_seg.txt";

	private static final Logger LOGGER = Logger.getLogger(FileUtils.class);

	/**
	 * Creates text file with one line for each key value pair in the format key: value
	 */
	public static boolean createTextFileWithValues(final String fileName, final Map<String, String> mapOfEntries) {

		List<String> listOfLines = new ArrayList<String>();

		for (Map.Entry<String, String> entry : mapOfEntries.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			String line = key + ": " + value;
			listOfLines.add(line);
		}

		return createTextFileFromListOfLines(fileName, listOfLines);
	}

	/**
	 * given fileName which has an extension of "extension" returns fileName without the extension
	 */
	public static String stripXmlExtensionFromFilename(final String extension, final String fileName) {
		String result = fileName;
		String normalisedExtension = "." + extension.toUpperCase();

		int indexOfExtension = fileName.toUpperCase().indexOf(normalisedExtension);
		if (indexOfExtension >= 0) {
			result = fileName.substring(0, indexOfExtension);
		}

		return result;

	}

	/**
	 * Creates text file with one line for each string value.
	 */
	public static boolean createTextFileFromListOfLines(final String fileName, final List<String> listOfLines) {
		boolean result = true;
		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), App.UTF_8_ENCODING_NAME));

			for (String line : listOfLines) {
				bufferedWriter.write(line);
				bufferedWriter.newLine();
			}
		} catch (IOException e) {
			result = false;
			LOGGER.error("Problem writing file : ", e);
		} finally {
			try {
				bufferedWriter.close();
			} catch (Exception e) {
				result = false;
				LOGGER.error("Problem closing file : ", e);
			}
		}
		return result;
	}

	/**
	 * Just dump the report to standard out.
	 */
	public static void logToStandardOutFromListOfLine(final List<String> reportAsListOfLines) {
		for (String line : reportAsListOfLines) {
			System.out.println(line);
		}
	}

	public static String getAs11SegmentationFileNameFromXmlFileName(final String xmlFileName) {
		return stripXmlExtensionFromFilename("xml", xmlFileName) + SEGMENTATION_SUFFIX_AND_EXTENSION;
	}

	public static String getAs11CoreFileNameFromXmlFileName(final String xmlFileName) {
		return stripXmlExtensionFromFilename("xml", xmlFileName) + AS11_CORE_SUFFIX_AND_EXTENSION;
	}

	public static String getUkDppFileNameFromXmlFileName(final String xmlFileName) {
		return stripXmlExtensionFromFilename("xml", xmlFileName) + UK_DPP_SUFFIX_AND_EXTENSION;
	}

	public static boolean doesFileExist(final String fileName) {
		File file = new File(fileName);

		return file.exists();
	}

	public static long getFileSize(final String fileName) {
		long sizeOfFile = 0;
		try {
			File sourcefile = new File(fileName);

			sizeOfFile = sourcefile.length();

		} catch (Exception e) {
			LOGGER.error("Caught exception trying to determine file length." + e);
		}

		return sizeOfFile;
	}

	public static boolean checkSufficientDiskSpace(final String mxfFileName, final String transwrappedMxfFileName) {
		boolean haveSufficientDiskSpace = true;
		try {
			File sourcefile = new File(mxfFileName);

			// Need to create a small file where the destination file is to be as otherwise getUsableSpace returns zero.
			createTempFile(transwrappedMxfFileName);

			File destinationfile = new File(transwrappedMxfFileName);

			final long sizeOfSourceMxf = sourcefile.length();

			final long usableSpace = destinationfile.getUsableSpace();

			// assuming that a transwrapped file might be 10% bigger than the original.
			final double estimatedSizeOfTranswrappedFile = 1.1 * sizeOfSourceMxf;
			if (usableSpace <= estimatedSizeOfTranswrappedFile) {
				haveSufficientDiskSpace = false;
			}

			destinationfile.delete();
		} catch (Exception e) {
			LOGGER.error("Caught exception trying to determine if we have sufficient disk space." + e);
			haveSufficientDiskSpace = false;
		}

		return haveSufficientDiskSpace;
	}

	public static boolean checkHaveWritePermission(String transwrappedMxfFileName) {
		boolean haveWritePermission = false;
		try {
			// Need to try to create a small file where the destination file is to be as .canWrite returns true if and only if the file system actually contains
			// a file denoted by this abstract pathname and the application is allowed to write to the file; false otherwise.
			createTempFile(transwrappedMxfFileName);

			File destinationfile = new File(transwrappedMxfFileName);

			haveWritePermission = destinationfile.canWrite();

			destinationfile.delete();
		} catch (Exception e) {
			LOGGER.error("Caught exception trying to determine if we have write permission." + e);
			haveWritePermission = false;
		}
		return haveWritePermission;
	}

	public static boolean createTempFile(final String fileName, long size) {
		boolean result = true;
		final String OneHundredChars = "lasdjfkljewkljkljslkdfjlksdjfklsjkfjklsjdflkjsdklfjklsdjflksdjfkljsdkljfkljsdlkfjklsdjfkljklsjkdjkjd";
		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(fileName));

			for (int i = 0; i < size / 100; i++) {
				bufferedWriter.write(OneHundredChars);
			}
			bufferedWriter.newLine();
		} catch (IOException e) {
			result = false;
			LOGGER.error("Problem writing file : ", e);
		} finally {
			try {
				bufferedWriter.close();
			} catch (IOException e) {
				result = false;
				LOGGER.error("Problem closing file : ", e);
			}
		}
		return result;
	}

	public static boolean deleteFile(String fileName) {
		File fileToBeDeleted = new File(fileName);
		boolean deleteOk = fileToBeDeleted.delete();
		return deleteOk;
	}

	protected static boolean createTempFile(final String fileName) {
		boolean result = true;
		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(fileName));

			bufferedWriter.write("empty file");
			bufferedWriter.newLine();
		} catch (IOException e) {
			result = false;
			LOGGER.error("Problem writing file : ", e);
		} finally {
			try {
				bufferedWriter.close();
			} catch (IOException e) {
				result = false;
				LOGGER.error("Problem closing file : ", e);
			}
		}
		return result;
	}

	public static String readFile(String filename) throws IOException {

		String contents = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filename));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			contents = sb.toString();
		} finally {
			if (br != null) {
				br.close();
			}
		}

		return contents;
	}

	public static String readFile(InputStream stream) throws IOException {

		String contents = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(stream, App.UTF_8_ENCODING_NAME));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			contents = sb.toString();
		} finally {
			if (br != null) {
				br.close();
			}
		}

		return contents;
	}

}

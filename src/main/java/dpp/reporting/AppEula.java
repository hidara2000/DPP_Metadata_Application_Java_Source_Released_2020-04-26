package dpp.reporting;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import dpp.util.FileUtils;

public class AppEula {

	private static final Logger logger = Logger.getLogger(AppEula.class);
	private static final String ERROR_MESSAGE = "Unable to read 'eula.txt' file";

	public void displayEula(String eulaFilename) {

		String contents;
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(eulaFilename);
		if (inputStream != null) {
			try {
				contents = FileUtils.readFile(inputStream);
				System.out.println(contents);
			} catch (IOException e) {
				logger.warn(String.format("Exception trying to load %s", eulaFilename), e);

				System.err.println(ERROR_MESSAGE);
			}
		} else {
			logger.warn(String.format("File %s not found in classpath", eulaFilename));
			System.err.println(ERROR_MESSAGE);
		}
	}
}

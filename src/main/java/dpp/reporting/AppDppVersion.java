package dpp.reporting;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

import dpp.ConfigFactory;
import model.Config;

public class AppDppVersion {

	private static final Logger logger = Logger.getLogger(AppDppVersion.class);
	private static final String ERROR_MESSAGE = "Unable to read 'config-java.xml' file";

	public void displayDppVersion(String filename) {

		try {
			Config config = getConfig(filename);
			if (config != null) {
				System.out.println(String.format("DPP Business Logics v%s", config.getBusinessLogicVersion()));
			} else {
				reportError();
			}
		} catch (DocumentException e) {
			logger.warn(String.format("Exception trying to load %s", filename), e);
			reportError();
		}
	}

	public void displayShimNames(String filename) {

		try {
			Config config = getConfig(filename);
			if (config != null) {
				System.out.println(config.getHdShimName());
				System.out.println(config.getSdShimName());
			} else {
				reportError();
			}
		} catch (DocumentException e) {
			logger.error(String.format("Exception trying to load %s", filename), e);
			reportError();
		}
	}

	private void reportError() {
		System.err.println(ERROR_MESSAGE);
	}

	private Config getConfig(String filename) throws DocumentException {

		ConfigFactory configFactory = new ConfigFactory();
		return configFactory.getConfig(filename);
	}
}

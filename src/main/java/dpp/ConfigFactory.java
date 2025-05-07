package dpp;

import java.net.URL;

import org.dom4j.DocumentException;

import dpp.parsers.ConfigParser;
import model.Config;

public class ConfigFactory {

	public Config getConfig(String filename) throws DocumentException {
		URL path = getClass().getClassLoader().getResource(filename);
		Config config = null;

		if (path != null) {
			ConfigParser parser = new ConfigParser();
			config = parser.readConfig(path);
		}

		return config;
	}
}

package dpp.parsers;

import java.net.URL;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import model.Config;

/**
 * Use XPath to read version numbers from java-config.xml.
 * 
 * @author Darren Greaves
 * 
 */
public class ConfigParser {

	public Config readConfig(URL path) throws DocumentException {

		Document document = getDocument(path);

		String hdShimName = readHdShimName(document);
		String sdShimName = readSdShimName(document);
		String businessLogicVersion = readBusinessLogicVersion(document);
		String shimVersion = readShimVersion(document);

		return new Config(hdShimName, sdShimName, businessLogicVersion, shimVersion);
	}

	private Document getDocument(URL path) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(path);

		return document;
	}

	private String readHdShimName(Document document) {

		return getElementText(document, "//configuration/defaultShimNameHD");
	}

	private String readSdShimName(Document document) {

		return getElementText(document, "//configuration/defaultShimNameSD");
	}

	private String readBusinessLogicVersion(Document document) {

		return getElementText(document, "//configuration/businessLogicVersion");
	}

	private String readShimVersion(Document document) {

		return getElementText(document, "//configuration/shimVersion");
	}

	private String getElementText(Document document, String xpath) {
		Node node = document.selectSingleNode(xpath);

		String text = null;
		if (node != null) {
			text = node.getText();
		}

		return text;
	}
}

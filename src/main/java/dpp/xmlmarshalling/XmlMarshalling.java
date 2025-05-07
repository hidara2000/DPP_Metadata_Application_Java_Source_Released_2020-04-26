package dpp.xmlmarshalling;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import dpp.reporting.StatusReport;
import dpp.schema.Programme;

public class XmlMarshalling {
	private static final Logger LOGGER = Logger.getLogger(XmlMarshalling.class);
	private JAXBContext jaxbContext = null;
	private Unmarshaller unMarshaller = null;
	private Marshaller marshaller = null;

	public XmlMarshalling() {
		super();
		try {
			this.jaxbContext = JAXBContext.newInstance("dpp.schema");
			this.unMarshaller = jaxbContext.createUnmarshaller();
			this.marshaller = jaxbContext.createMarshaller();
		} catch (JAXBException e) {
			LOGGER.error("Unable to create XmlMarshalling object", e);
		}

	}

	/**
	 * Marshalls an xml file from a Programme.
	 * 
	 * @param statusReport
	 */
	public boolean generateXmlFileFromProgramme(Programme programme, String xmlFileName, StatusReport statusReport) {
		boolean successfullyGeneratedXmlFile = false;

		// marshall the data into the xml file.
		File xmlFile = new File(xmlFileName);
		try {
			marshaller.marshal(programme, xmlFile);
			successfullyGeneratedXmlFile = true;
		} catch (JAXBException e) {
			statusReport.reportSystemError("Unable to marshall xml file for programme");
			LOGGER.error("Unable to marshall xml file for programme", e);
		}

		return successfullyGeneratedXmlFile;
	}

	/**
	 * Unmarshalls an xml file into Programme
	 * 
	 * @param statusReport
	 */
	public Programme getProgrammeFromXmlFile(String xmlFileName, StatusReport statusReport) {
		Programme programmeFromXmlFile = null;
		File xmlFile = new File(xmlFileName);
		try {
			programmeFromXmlFile = (Programme) unMarshaller.unmarshal(xmlFile);
		} catch (JAXBException e) {
			statusReport.reportSystemError("Unable to unmarshall xml file for programme");
			LOGGER.error("Unable to unmarshall xml file for programme", e);
		}
		return programmeFromXmlFile;
	}

}

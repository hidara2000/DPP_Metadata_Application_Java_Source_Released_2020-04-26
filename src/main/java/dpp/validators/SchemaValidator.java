package dpp.validators;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import dpp.reporting.StatusReport;
import dpp.schema.FpapassEnum;
import dpp.schema.Programme;
import dpp.xmlmarshalling.XmlMarshalling;

public class SchemaValidator {
	private static final Logger LOGGER = Logger.getLogger(SchemaValidator.class);
	private final String dPP_FULL_XSD = "schema/dpp.xsd";
	private static final XmlMarshalling XML_MARSHALLING = new XmlMarshalling();

	public boolean validateXmlFileAgainstSchema(String xmlFilePath, StatusReport statusReport) {
		boolean validXml = false;
		final File fileToValidate = new File(xmlFilePath);
		SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		FileInputStream fileInputStream = null;
		if (fileToValidate.exists() && fileToValidate.canRead()) {
			try {
				fileInputStream = new FileInputStream(fileToValidate);
			} catch (FileNotFoundException fnfe) {
				String fileNotFound = "Failed to find xml file [" + xmlFilePath + "] so could not validate against schema. ";
				LOGGER.error(fileNotFound, fnfe);
				statusReport.reportSystemError(fileNotFound);
			}
		}

		if (fileInputStream != null) {
			String errorPrepend = "The media file has been rejected due to noncompliant metadata - DPP Metadata Schema Validation Error:";
			String failureMessage = String.format("Failed to validate xml file [%s] against schema", xmlFilePath);
			try {
				URL schemaResource = getClass().getClassLoader().getResource(dPP_FULL_XSD);
				LOGGER.debug("schemaResource: " + schemaResource);
				Schema schema = schemaFactory.newSchema(schemaResource);

				final Validator validator = schema.newValidator();
				/*
				 * According to the spec (http://docs.oracle.com/javase/1.5.0/docs/api/javax/xml/validation/Validator.html), source can only be either a
				 * SAXSource or DOMSource.
				 */
				validator.validate(new SAXSource(new InputSource(fileInputStream)));
				validXml = true;
			} catch (SAXParseException e) {
				LOGGER.error(failureMessage, e);
				statusReport
						.reportValidationError(String
								.format("%s The media file has been rejected due to noncompliant metadata - DPP Metadata Schema Validation Error: File %s is not valid because of error '%s' at line number %d, column %d",
										errorPrepend, fileToValidate, e.getMessage(), e.getLineNumber(), e.getColumnNumber()));
			} catch (SAXException e) {
				LOGGER.error(failureMessage, e);
				statusReport.reportValidationError(String.format("%s File %s is not valid because %s", errorPrepend, fileToValidate, e.getMessage()));
			} catch (IOException e) {
				LOGGER.error(failureMessage, e);
				statusReport.reportValidationError(String.format("%s File %s is not valid because %s", errorPrepend, fileToValidate, e.getMessage()));
			} finally {
				/* Make sure the stream is closed to release the handle to the file. */
				try {
					fileInputStream.close();
				} catch (IOException ioe) {
					/* Allow GC to collect reference */
					fileInputStream = null;
				}
			}
		}

		if (!validXml) {
			Programme programme = XML_MARSHALLING.getProgrammeFromXmlFile(xmlFilePath, statusReport);
			LOGGER.debug("programmeFromXmlFile = " + programme);
			if (programme == null) {
				LOGGER.debug("Unable to validate fields against acceptable values as could not unmarshall the file");
			} else {
				validateFieldsAgainstAcceptableValues(programme, statusReport);
			}
		}

		return validXml;
	}

	/**
	 * This method is intended to give some more information about invalid fields if the schema validation has failed since the output from the schema validator
	 * is not always very clear
	 * 
	 */
	protected void validateFieldsAgainstAcceptableValues(Programme programme, StatusReport statusReport) {

		Boolean productPlacement = programme.getTechnical().getVideo().isProductPlacement();
		if (productPlacement == null) {
			statusReport.reportValidationError("Invalid value for Product Placement");
		}

		Boolean threeD = programme.getTechnical().getVideo().isThreeD();
		if (threeD == null) {
			statusReport.reportValidationError("Invalid value for 3D");
		}

		FpapassEnum fpaPass = programme.getTechnical().getVideo().getFPAPass();
		if (fpaPass == null) {
			statusReport.reportValidationError("Invalid value for FPA Pass");
		}

	}

}

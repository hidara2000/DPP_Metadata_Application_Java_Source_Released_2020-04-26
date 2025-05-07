package dpp.util;

import java.math.BigInteger;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;

public class DateUtils {
	private static final String PROBLEM_CREATING_XML_DATE_FOR_DATE = "Problem creating xml date for date : ";
	private static final Logger LOGGER = Logger.getLogger(DateUtils.class);

	/**
	 * assumes year is in the format yyyy e.g. 2011
	 */
	public static XMLGregorianCalendar createXmlGregorianCalendarFromYear(String year) {
		GregorianCalendar gc = new GregorianCalendar();
		DatatypeFactory dtf = null;
		XMLGregorianCalendar xgc = null;
		try {
			dtf = DatatypeFactory.newInstance();
			xgc = dtf.newXMLGregorianCalendar(gc);
			xgc.setYear(new BigInteger(year));
		} catch (DatatypeConfigurationException e) {
			LOGGER.error("Problem creating xml date for year : " + year, e);
		} catch (NumberFormatException nfe) {
			LOGGER.error("Problem creating xml date for year : " + year, nfe);
		}
		return xgc;
	}

	/**
	 * assumes date is in the format yyyy-mm-dd e.g. 2011-08-01
	 */
	public static XMLGregorianCalendar createXmlGregorianCalendarFromDate(String date) {
		GregorianCalendar gc = new GregorianCalendar();
		DatatypeFactory dtf = null;
		XMLGregorianCalendar xgc = null;

		if (date.length() != 10) {
			LOGGER.error("Date supplied in wrong format - " + date);
			return xgc;
		}

		try {
			dtf = DatatypeFactory.newInstance();
			xgc = dtf.newXMLGregorianCalendar(gc);

			int year = Integer.valueOf(date.substring(0, 4));
			int month = Integer.valueOf(date.substring(5, 7));
			int day = Integer.valueOf(date.substring(8, 10));

			LOGGER.debug("Setting xml date to " + year + "--" + month + "--" + day);

			xgc.setYear(year);
			xgc.setMonth(month);
			xgc.setDay(day);
		} catch (DatatypeConfigurationException e) {
			LOGGER.error(PROBLEM_CREATING_XML_DATE_FOR_DATE + date, e);
		} catch (NumberFormatException nfe) {
			LOGGER.error(PROBLEM_CREATING_XML_DATE_FOR_DATE + date, nfe);
		}
		return xgc;
	}

	/**
	 * assumes date is in the format yyyy-mm-dd e.g. 2011-08-01 - Only interested in the Year month and date for this method.
	 */
	public static XMLGregorianCalendar createXmlGregorianCalendarDateFromDate(String date) {
		DatatypeFactory dtf = null;
		XMLGregorianCalendar xgc = null;

		if (date.length() != 10) {
			LOGGER.error("Date supplied in wrong format - " + date);
			return xgc;
		}

		try {
			int year = Integer.valueOf(date.substring(0, 4));
			int month = Integer.valueOf(date.substring(5, 7));
			int day = Integer.valueOf(date.substring(8, 10));
			LOGGER.debug("Setting xml date to " + year + "--" + month + "--" + day);

			dtf = DatatypeFactory.newInstance();
			xgc = dtf.newXMLGregorianCalendarDate(year, month, day, DatatypeConstants.FIELD_UNDEFINED);

		} catch (DatatypeConfigurationException e) {
			LOGGER.error(PROBLEM_CREATING_XML_DATE_FOR_DATE + date, e);
		} catch (NumberFormatException nfe) {
			LOGGER.error(PROBLEM_CREATING_XML_DATE_FOR_DATE + date, nfe);
		}
		return xgc;
	}

	/**
	 * assumes date is in the format yyyy-mm-dd e.g. 2011-08-01 - Only interested in the Year for this method.
	 */
	public static XMLGregorianCalendar createXmlGregorianCalendarDateFromYear(String date) {
		DatatypeFactory dtf = null;
		XMLGregorianCalendar xgc = null;

		if (date.length() != 4) {
			LOGGER.error("Date supplied in wrong format - " + date);
			return xgc;
		}

		try {
			int year = Integer.valueOf(date.substring(0, 4));
			LOGGER.debug("Setting xml date to " + year);

			dtf = DatatypeFactory.newInstance();
			xgc = dtf
					.newXMLGregorianCalendarDate(year, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED);

		} catch (DatatypeConfigurationException e) {
			LOGGER.error(PROBLEM_CREATING_XML_DATE_FOR_DATE + date, e);
		} catch (NumberFormatException nfe) {
			LOGGER.error(PROBLEM_CREATING_XML_DATE_FOR_DATE + date, nfe);
		}
		return xgc;
	}

	/**
	 * assumes date is in the format yyyy-mm-ddThh:mm:ss e.g. 2011-08-01T09:00:00 - Only interested in the Year for this method.
	 */
	public static XMLGregorianCalendar createXmlGregorianCalendarDateFromDateTime(final String dateTime) {
		DatatypeFactory dtf = null;
		XMLGregorianCalendar xgc = null;

		try {
			dtf = DatatypeFactory.newInstance();
			xgc = dtf.newXMLGregorianCalendar(dateTime);
		} catch (DatatypeConfigurationException e) {
			LOGGER.error(PROBLEM_CREATING_XML_DATE_FOR_DATE + dateTime, e);
		} catch (NumberFormatException nfe) {
			LOGGER.error(PROBLEM_CREATING_XML_DATE_FOR_DATE + dateTime, nfe);
		}
		return xgc;
	}

	/**
	 * returns date as string yyyy e.g. 2011
	 */
	public static String getDateAsYyyy(XMLGregorianCalendar xgc) {
		if (xgc == null) {
			return "UNKNOWN";
		}
		return String.format("%4d", xgc.getYear());
	}

	/**
	 * returns date as string yyyy-mm-dd e.g. 2011-08-01
	 */
	public static String getDateAsYyyyMmDd(XMLGregorianCalendar xgc) {
		if (xgc == null) {
			return "UNKNOWN";
		}
		return String.format("%04d-%02d-%02d", xgc.getYear(), xgc.getMonth(), xgc.getDay());
	}

}

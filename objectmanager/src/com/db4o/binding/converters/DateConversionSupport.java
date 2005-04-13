package com.db4o.binding.converters;

import java.text.*;
import java.util.*;

/**
 * Base support for date/string conversion handling according to the
 * default locale.
 * 
 * NOTE: parse(format(date)) will usually *not* be equal to date, since the
 * string representation doesn't cover the sub-second range.
 */
public abstract class DateConversionSupport {
	public final static int DATE_FORMAT=DateFormat.SHORT;
	public final static int TIME_FORMAT=DateFormat.MEDIUM;
	public final int DEFAULT_FORMATTER_INDEX=0;

	/**
	 * Alternative formatters for date, time and date/time.
	 */
	// TODO: These could be shared, but would have to be synchronized.
	private DateFormat[] formatters={
			DateFormat.getDateTimeInstance(DATE_FORMAT,TIME_FORMAT),
			DateFormat.getDateInstance(DATE_FORMAT),
			DateFormat.getTimeInstance(TIME_FORMAT)
	};
	
	/**
	 * Tries all available formatters to parse the given string according to the
	 * default locale and returns the result of the first successful run.
	 * 
	 * @param str A string specifying a date according to the default locale
	 * @return The parsed date, or null, if no available formatter could interpret the input string
	 */
	protected Date parse(String str) {
		Date parsed=null;
		for (int formatterIdx = 0; formatterIdx < formatters.length; formatterIdx++) {
			try {
				parsed=formatters[formatterIdx].parse(str);
				break;
			} catch (ParseException e) {
			}
		}
		return parsed;
	}
	
	/**
	 * Formats the given date with the default formatter according to the default locale.
	 */
	protected String format(Date date) {
		return format(date,DEFAULT_FORMATTER_INDEX);
	}

	/**
	 * Formats the given date with the given formatter according to the default locale.
	 */
	protected String format(Date date,int formatterIdx) {
		return formatters[formatterIdx].format(date);
	}

	protected int numFormatters() {
		return formatters.length;
	}
}
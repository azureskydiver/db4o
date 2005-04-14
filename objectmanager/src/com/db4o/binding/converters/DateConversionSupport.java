package com.db4o.binding.converters;

import java.text.*;
import java.util.*;

/**
 * Base support for date/string conversion handling according to the
 * default locale or in plain long milliseconds.
 * 
 * NOTE: parse(format(date)) will generally *not* be equal to date, since the
 * string representation may not cover the sub-second range, time-only string
 * representations will be counted from the beginning of the era, etc.
 */
public abstract class DateConversionSupport {
	public final static int DATE_FORMAT=DateFormat.SHORT;
	public final static int TIME_FORMAT=DateFormat.MEDIUM;
	public final static int DEFAULT_FORMATTER_INDEX=0;

	private final static int NUM_VIRTUAL_FORMATTERS=1;
	
	/**
	 * Alternative formatters for date, time and date/time.
	 * Raw milliseconds are covered as a special case.
	 */
	// TODO: These could be shared, but would have to be synchronized.
	private DateFormat[] formatters={
			new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS Z"),
			DateFormat.getDateTimeInstance(DATE_FORMAT,TIME_FORMAT),
			DateFormat.getDateInstance(DATE_FORMAT),
			DateFormat.getTimeInstance(TIME_FORMAT),
			new SimpleDateFormat("HH:mm:ss.SSS")
	};
	
	/**
	 * Tries all available formatters to parse the given string according to the
	 * default locale or as a raw millisecond value and returns the result of the
	 * first successful run.
	 * 
	 * @param str A string specifying a date according to the default locale or in raw milliseconds
	 * @return The parsed date, or null, if no available formatter could interpret the input string
	 */
	protected Date parse(String str) {
		for (int formatterIdx = 0; formatterIdx < formatters.length; formatterIdx++) {
			Date parsed=parse(str,formatterIdx);
			if(parsed!=null) {
				return parsed;
			}
		}
		return null;
	}

	protected Date parse(String str,int formatterIdx) {
		if(formatterIdx<numFormatters()-1) {
			try {
				return formatters[formatterIdx].parse(str);
			} catch (ParseException exc) {
			}
		}
		else {
			try {
				long millisecs=Long.parseLong(str);
				return new Date(millisecs);
			}
			catch(NumberFormatException exc) {
			}
		}
		return null;
	}
	
	/**
	 * Formats the given date with the default formatter according to the default locale.
	 */
	protected String format(Date date) {
		return format(date,DEFAULT_FORMATTER_INDEX);
	}

	protected String format(Date date,int formatterIdx) {
		if(formatterIdx<numFormatters()-1) {
			return formatters[formatterIdx].format(date);
		}
		return String.valueOf(date.getTime());
	}

	protected int numFormatters() {
		return formatters.length+NUM_VIRTUAL_FORMATTERS;
	}
}
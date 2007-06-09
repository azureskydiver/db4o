/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers.net;

import java.util.*;

import com.db4o.internal.*;

/**
 * @exclude
 * @sharpen.ignore
 */
// TODO: Between .NET and Java there seems to be a difference of two days between era offsets?!?
public class NetDateTime extends NetSimpleTypeHandler{
	private final static String ZEROES="0000";
	
	private final static String[] MONTHS= {
		"Jan",
		"Feb",
		"Mar",
		"Apr",
		"May",
		"Jun",
		"Jul",
		"Aug",
		"Sep",
		"Oct",
		"Nov",
		"Dec"
	};
	
    // ms between 01.01.0001,00:00:00.000 and 01.01.1970,00:00:00.000
	//private static final long ERA_DIFFERENCE_IN_MS = 62135604000000L; // Carl's diff
	private static final long ERA_DIFFERENCE_IN_MS = 62135596800000L; // .net diff	
    //private static final long ERA_DIFFERENCE_IN_MS = 62135769600000L; // java diff
    
    // ratio from .net ticks (100ns) to java ms
    private static final long TICKS_TO_MS_RATIO = 10000;

	public NetDateTime(ObjectContainerBase stream) {
		super(stream, 25, 8);
	}
	
	public String toString(byte[] bytes) {
        long ticks = 0;
        for (int i = 0; i < 8; i++) {
            ticks = (ticks << 8) + (bytes[i] & 255);
        }
        long ms = ticks / TICKS_TO_MS_RATIO - ERA_DIFFERENCE_IN_MS;
        Date date=new Date(ms);
        Calendar cal=Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTime(date);
        StringBuffer result=new StringBuffer()
        	.append(prependZeroes(cal.get(Calendar.YEAR),4))
        	.append('-')
        	.append(MONTHS[cal.get(Calendar.MONTH)])
        	.append('-')
        	.append(prependZeroes(cal.get(Calendar.DAY_OF_MONTH),2))
        	.append(", ")
        	.append(prependZeroes(cal.get(Calendar.HOUR_OF_DAY),2))
        	.append(':')
        	.append(prependZeroes(cal.get(Calendar.MINUTE),2))
        	.append(':')
        	.append(prependZeroes(cal.get(Calendar.SECOND),2))
        	.append('.')
        	.append(prependZeroes(cal.get(Calendar.MILLISECOND),3))
        	.append(" UTC");
        return result.toString();
    }
	
	private String prependZeroes(int val,int size) {
		String str=String.valueOf(val);
		int missing=size-str.length();
		if(missing>0) {
			str=ZEROES.substring(0,missing)+str;
		}
		return str;
	}
}

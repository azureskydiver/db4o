/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.handlers;

import java.util.*;

import com.db4o.*;

/**
 * @exclude
 */
// TODO: Between .NET and Java there seems to be a difference of two days between era offsets?!?
public class NetDateTime extends NetSimpleTypeHandler{
	
    // ms between 01.01.0001,00:00:00.000 and 01.01.1970,00:00:00.000
	//private static final long ERA_DIFFERENCE_IN_MS = 62135604000000L; // Carl's diff
	private static final long ERA_DIFFERENCE_IN_MS = 62135596800000L; // .net diff	
    //private static final long ERA_DIFFERENCE_IN_MS = 62135769600000L; // java diff
    
    // ratio from .net ticks (100ns) to java ms
    private static final long TICKS_TO_MS_RATIO = 10000;

	public NetDateTime(YapStream stream) {
		super(stream, 25, 8);
	}
	
	public String toString(byte[] bytes) {
        long ticks = 0;
        for (int i = 0; i < 8; i++) {
            ticks = (ticks << 8) + (long)(bytes[i] & 255);
        }
        long ms = ticks / TICKS_TO_MS_RATIO - ERA_DIFFERENCE_IN_MS;
        return new Date(ms).toString();
    }
}

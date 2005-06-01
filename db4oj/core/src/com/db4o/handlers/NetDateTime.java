/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.handlers;

import java.util.*;

import com.db4o.*;

/**
 * @exclude
 */
public class NetDateTime extends NetSimpleTypeHandler{
	
    private static final long DIFFERENCE_IN_TICKS = 62135604000000L;
    private static final long RATIO = 10000;

	public NetDateTime(YapStream stream) {
		super(stream, 25, 8);
	}
	
	public String toString(byte[] bytes) {
        long ticks = 0;
        for (int i = 0; i < 8; i++) {
            ticks = (ticks << 8) + (long)(bytes[i] & 255);
        }
        long ms = ticks / RATIO - DIFFERENCE_IN_TICKS;
        return new Date(ms).toString();
	}
}

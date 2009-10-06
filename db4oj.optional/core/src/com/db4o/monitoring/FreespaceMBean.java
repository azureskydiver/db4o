/* Copyright (C) 2009  Versant Corp.  http://www.db4o.com */

package com.db4o.monitoring;

/**
 * @exclude
 */
@decaf.Ignore
public interface FreespaceMBean {
	
	int getSlotCount();
	
	double getReusedSlotsPerSecond();
	
	int getTotalFreespace();
	
	double getAverageSlotSize();

}

/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
package com.db4odoc.diagnostics;

/**
 * @sharpen.ignore
 */
public class ArbitraryQuery extends com.db4o.query.Predicate<Pilot>{    
    public int[] points;
    
    public ArbitraryQuery(int[] points) {
        this.points=points;
    }
    
    public boolean match(Pilot pilot) {
    	for (int i = 0; i < points.length; i++) {
 			if (((Pilot)pilot).getPoints() == points[i])
			{
				return true;
			}
		}
    	return ((Pilot)pilot).getName().startsWith("Rubens");
    }
}

package com.db4odoc.f1.diagnostics;

import com.db4o.query.Predicate;

public class ArbitraryQuery extends Predicate{    
    private int[] points;
    
    public ArbitraryQuery(int[] points) {
        this.points=points;
    }
    
    public boolean match(Pilot pilot) {
    	for (int i = 0; i < points.length; i++) {
 			if (pilot.getPoints() == points[i])
			{
				return true;
			}
		}
    	return pilot.getName().startsWith("Rubens");
    }
    
}

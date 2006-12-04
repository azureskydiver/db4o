package com.db4odoc.diagnostics;

import com.db4o.query.Predicate;

public class ArbitraryQuery extends Predicate<Pilot>{    
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

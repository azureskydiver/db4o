package com.db4odoc.f1.diagnostics;

import com.db4o.query.Predicate;
import com.db4odoc.f1.evaluations.Pilot;

public class ArbitraryQuery extends Predicate{    
    public int[] points;
    
    public ArbitraryQuery(int[] points) {
        this.points=points;
    }
    
    public boolean match(Object pilot) {
    	for (int i = 0; i < points.length; i++) {
 			if (((Pilot)pilot).getPoints() == points[i])
			{
				return true;
			}
		}
    	return ((Pilot)pilot).getName().startsWith("Rubens");
    }

    
}

package com.db4odoc.query.nq.oldjava;

import com.db4o.query.Predicate;

// #example: The query class
class AllJohns extends Predicate {
    public boolean match(Pilot pilot) {
        return pilot.getName().equals("John");
    }
}
// #end example

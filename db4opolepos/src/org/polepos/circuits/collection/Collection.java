/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package org.polepos.circuits.collection;

import org.polepos.framework.*;


public class Collection extends Circuit {

    @Override
    public String description() {
        return "operation on a collection";
    }

    @Override
    public Class requiredDriver() {
        return CollectionDriver.class;
    }

    @Override
    protected void addLaps() {
    	add(new Lap("store", false, false));
        add(new Lap("getFirstElement"));
        add(new Lap("getMiddleElement"));
        add(new Lap("getLastElement"));
        add(new Lap("getAllElements"));
    }

}

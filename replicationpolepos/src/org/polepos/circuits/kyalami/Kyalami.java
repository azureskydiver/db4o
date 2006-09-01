/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package org.polepos.circuits.kyalami;

import org.polepos.framework.*;


/**
 * @exclude
 */
public class Kyalami extends Circuit {

    @Override
    public String description() {
        return "simplest replication run";
    }

    @Override
    public Class requiredDriver() {
        return KyalamiDriver.class;
    }

    @Override
    protected void addLaps() {
        add(new Lap("storeInA"));
        add(new Lap("replicate"));
    }

}

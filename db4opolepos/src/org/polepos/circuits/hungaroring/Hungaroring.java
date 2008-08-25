package org.polepos.circuits.hungaroring;

import org.polepos.framework.*;

public class Hungaroring extends Circuit {

    @Override
    public String description() {
        return "excecutes a variety of queries to test multithreading efficiency";
    }

    @Override
    public Class requiredDriver() {
        return HungaroringDriver.class;
    }

    @Override
    protected void addLaps() {
    	
        add(new Lap("write", false, false));

        add(new Lap("queryWithTwoWorkers"));
        add(new Lap("queryWithFourWorkers"));
                
    }


}

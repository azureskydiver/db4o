package org.polepos.circuits.multithreadedqueries;

import org.polepos.framework.*;

public class MultithreadedQueries extends CircuitBase {

    @Override
    public String description() {
        return "excecutes a variety of queries to test multithreading efficiency";
    }

    @Override
    public Class requiredDriver() {
        return MultithreadedQueriesDriver.class;
    }

    @Override
    protected void addLaps() {
    	
        add(new Lap("write", false, false));

        add(new Lap("queryWithTwoWorkers"));
        add(new Lap("queryWithFourWorkers"));
                
    }


}

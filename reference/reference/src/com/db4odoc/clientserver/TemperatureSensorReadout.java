/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4odoc.clientserver;

import java.util.*;


public class TemperatureSensorReadout extends SensorReadout {
    private double temperature;
    
    public TemperatureSensorReadout(
            Date time,Car car,
            String description,double temperature) {
        super(time,car,description);
        this.temperature=temperature;
    }
    
    public double getTemperature() {
        return temperature;
    }

    public String toString() {
        return super.toString()+" temp : "+temperature;
    }
}

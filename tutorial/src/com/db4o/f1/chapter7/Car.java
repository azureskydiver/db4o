package com.db4o.f1.chapter7;

import java.util.*;

import com.db4o.activation.*;
import com.db4o.ta.*;

public class Car implements Activatable {
    private String model;
    private Pilot pilot;
    private SensorReadout history;
    private transient Activator _activator;

    public Car(String model) {
        this.model=model;
        this.pilot=null;
        this.history=null;
    }

    public Pilot getPilot() {
        activate();
        return pilot;
    }
    
    public Pilot getPilotWithoutActivation() {
        return pilot;
    }

    public void setPilot(Pilot pilot) {
        activate();
        this.pilot=pilot;
    }

    public String getModel() {
        activate();
        return model;
    }
    
    public SensorReadout getHistory() {
        activate();
        return history;
    }
    
    public void snapshot() {
        activate();
        appendToHistory(new TemperatureSensorReadout(
                new Date(),this,"oil",pollOilTemperature()));
        appendToHistory(new TemperatureSensorReadout(
                new Date(),this,"water",pollWaterTemperature()));
        appendToHistory(new PressureSensorReadout(
                new Date(),this,"oil",pollOilPressure()));
    }

    protected double pollOilTemperature() {
        activate();
        return 0.1*countHistoryElements();
    }

    protected double pollWaterTemperature() {
        activate();
        return 0.2*countHistoryElements();
    }

    protected double pollOilPressure() {
        return 0.3*countHistoryElements();
    }

    public String toString() {
        activate();
        return model+"["+pilot+"]/"+countHistoryElements();
    }
    
    private int countHistoryElements() {
        activate();
        return (history==null ? 0 : history.countElements());
    }
    
    private void appendToHistory(SensorReadout readout) {
        activate();
        if(history==null) {
            history=readout;
        }
        else {
            history.append(readout);
        }
    }

    public void activate() {
        if(_activator != null) {
            _activator.activate();
        }
    }

    public void bind(Activator activator) {
        if(_activator != null || activator == null) {
            throw new IllegalStateException();
        }
        _activator = activator;
    }

}
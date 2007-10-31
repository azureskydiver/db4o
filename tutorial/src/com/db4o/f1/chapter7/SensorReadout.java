package com.db4o.f1.chapter7;

import java.util.*;

import com.db4o.activation.*;
import com.db4o.ta.*;

public class SensorReadout implements Activatable {
    private Date time;
    private Car car;
    private String description;
    private SensorReadout next;
    private transient Activator _activator;

    protected SensorReadout(Date time,Car car,String description) {
        this.time=time;
        this.car=car;
        this.description=description;
        this.next=null;
    }

    public Car getCar() {
        activate();
        return car;
    }

    public Date getTime() {
        activate();
        return time;
    }

    public String getDescription() {
        activate();
        return description;
    }

    public SensorReadout getNext() {
        activate();
        return next;
    }
    
    public void append(SensorReadout readout) {
        activate();
        if(next==null) {
            next=readout;
        }
        else {
            next.append(readout);
        }
    }
    
    public int countElements() {
        activate();
        return (next==null ? 1 : next.countElements()+1);
    }
    
    public String toString() {
        activate();
        return car+" : "+time+" : "+description;
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
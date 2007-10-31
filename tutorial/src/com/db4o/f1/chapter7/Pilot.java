package com.db4o.f1.chapter7;

import com.db4o.activation.*;
import com.db4o.ta.*;

public class Pilot implements Activatable{
    private String name;
    private int points;
    private transient Activator _activator;

    public Pilot(String name,int points) {
        this.name=name;
        this.points=points;
    }

    public int getPoints() {
        activate();
        return points;
    }

    public void addPoints(int points) {
        activate();
        this.points+=points;
    }

    public String getName() {
        activate();
        return name;
    }

    public String toString() {
        activate();
        return name+"/"+points;
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
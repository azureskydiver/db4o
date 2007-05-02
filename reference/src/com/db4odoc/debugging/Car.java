/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
package com.db4odoc.debugging;

import java.util.*;

public class Car {
    private String model;
    private List history;

    public Car(String model) {
        this(model,new ArrayList());
    }

    public Car(String model,List history) {
        this.model=model;
        this.history=history;
    }

    public String getModel() {
        return model;
    }

    public List getHistory() {
        return history;
    }
    
    
    public String toString() {
        return model;
    }
}
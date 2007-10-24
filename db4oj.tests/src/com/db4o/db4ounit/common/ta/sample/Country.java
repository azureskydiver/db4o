/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta.sample;

import com.db4o.activation.*;
import com.db4o.ta.*;


public class Country implements Activatable {
    
    public State[] _states;
    
    public State getState(String zipCode){
        activate();
        return _states[0];
    }
    
    private transient Activator _activator;
    
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

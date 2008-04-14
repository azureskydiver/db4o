/* Copyright (C) 2008 db4objects Inc. http://www.db4o.com */

package com.db4odoc.tp.rollback;

import com.db4o.activation.*;
import com.db4o.ta.*;

public class Id implements Activatable {
	int number = 0;
	
	transient Activator _activator;
	
	public Id(int number){
		this.number = number;
	}
	
	public void bind(Activator activator) {
    	if (_activator == activator) {
    		return;
    	}
    	if (activator != null && _activator != null) {
            throw new IllegalStateException();
        }
		_activator = activator;
	}

	public void activate(ActivationPurpose purpose) {
		if (_activator == null)
			return;
		_activator.activate(purpose);
	}

	
	public String toString(){
		activate(ActivationPurpose.READ);
		return String.valueOf(number);
	}

	public void change(int i) {
		activate(ActivationPurpose.WRITE);
		this.number = i;
	}
}

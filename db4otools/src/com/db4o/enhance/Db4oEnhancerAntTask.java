/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.enhance;

import com.db4o.instrumentation.ant.*;
import com.db4o.nativequery.main.*;
import com.db4o.ta.instrumentation.ant.*;


/**
 * Ant task to enhance class files for db4o.
 */
public class Db4oEnhancerAntTask extends Db4oFileEnhancerAntTask {
    
    private boolean _nq = true;
	private boolean _ta = true;
	private boolean _collections = true;

	public Db4oEnhancerAntTask(){
    }

    public void setNq(boolean nq) {
    	_nq = nq;
    }
    
    public void setTa(boolean ta) {
    	_ta = ta;
    }

    public void setCollections(boolean collections) {
    	_collections = collections;
    }
    
    public void execute() {
    	if(_nq) {
            add(new NQAntClassEditFactory());
    	}
    	if(_ta) {
            add(new TAAntClassEditFactory(_collections));
    	}
    	super.execute();
    }
}

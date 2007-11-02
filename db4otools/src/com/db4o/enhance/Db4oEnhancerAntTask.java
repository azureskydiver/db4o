/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.enhance;

import com.db4o.instrumentation.ant.*;
import com.db4o.nativequery.main.*;
import com.db4o.ta.instrumentation.ant.*;


/**
 * FIXME: Write documentation
 */
public class Db4oEnhancerAntTask extends Db4oFileEnhancerAntTask {
    
    public Db4oEnhancerAntTask(){
        add(new NQAntClassEditFactory());
        add(new TAAntClassEditFactory());
    }

}

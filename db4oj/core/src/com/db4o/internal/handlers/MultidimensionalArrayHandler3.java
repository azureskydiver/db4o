/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.handlers;


/**
 * @exclude
 */
public class MultidimensionalArrayHandler3 extends MultidimensionalArrayHandler {
    
    protected ArrayVersionHelper createVersionHelper() {
        return new ArrayVersionHelper3();
    }

}

/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.handlers;


/**
 * @exclude
 */
public class ArrayHandler3 extends ArrayHandler {
    
    protected ArrayVersionHelper createVersionHelper() {
        return new ArrayVersionHelper3();
    }

}
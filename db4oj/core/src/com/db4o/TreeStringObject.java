/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * 
 */
public class TreeStringObject extends TreeString {

    public final Object i_object;

    public TreeStringObject(String a_key, Object a_object) {
        super(a_key);
        this.i_object = a_object;
    }

}
/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * @exclude
 */
public class QEEqual extends QEAbstract
{
    public void indexBitMap(boolean[] bits){
        bits[1] = true;
    }
}

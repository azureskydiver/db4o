/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

public class QEEqual extends QEAbstract
{
    void indexBitMap(boolean[] bits){
        bits[1] = true;
    }
}

/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.header;

import com.db4o.*;


/**
 * @exclude
 */
public abstract class FileHeader {
    
    public abstract void writeVariablePart1(YapFile file);

}

/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.header;

import com.db4o.inside.*;


/**
 * @exclude
 */
public abstract class FileHeader {
    
    public abstract SystemData systemData();

    public abstract void variablePartChanged();

}

/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.assorted;


public class NullWrapperItem {
    
    public Integer _integer;
    
    public static final String INTEGER_FIELDNAME = "_integer";


    public NullWrapperItem() {
    }

    public NullWrapperItem(Integer i) {
        _integer = i;
    }

}

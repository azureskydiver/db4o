/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.assorted;

import java.util.*;


/**
 * @exclude
 */
public class IndexCreateDropItem {
    
    public int _int;
    
    public String _string;
    
    public Date _date;

    public IndexCreateDropItem(int int_, String string_, Date date_) {
        _int = int_;
        _string = string_;
        _date = date_;
    }
    
    public IndexCreateDropItem(int int_) {
        this(int_, int_ == 0 ? null : "" + int_, int_ == 0 ? null : new Date(int_));
    }

}

/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;

public final class SearchTarget {
    
    public static final SearchTarget LOWEST = new SearchTarget(-1);
    
    public static final SearchTarget ANY = new SearchTarget(0);
    
    public static final SearchTarget HIGHEST = new SearchTarget(1);
    

    private final int _target;
    
    private SearchTarget(final int target) {
        _target = target;
    }
}

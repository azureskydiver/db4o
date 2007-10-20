/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta;

import java.util.*;

public class TADateItemArray extends ActivatableImpl {

    public static final Date[] data = {
        new Date(0),
        new Date(1),
        new Date(1191972104500L),
    };

    public Date[] _typed;

    public Object[] _untyped;

    public TADateItemArray _next;

    public int _depth;

    public TADateItemArray() {

    }
    
    public TADateItemArray(Date[] values) {
        _typed = new Date[values.length];
        _untyped = new Object[values.length];
        System.arraycopy(values, 0, _typed, 0, data.length);
        System.arraycopy(values, 0, _untyped, 0, data.length);
    }

    public static TADateItemArray itemArrayList(int depth) {
        if (depth == 0) {
            return null;
        }
        TADateItemArray header = new TADateItemArray(data);
        header._depth = depth;
        header._next = itemArrayList(depth - 1);
        return header;
    }

    public int depth() {
        activate();
        return _depth;
    }

    public Date[] getTyped() {
        activate();
        return _typed;
    }

    public Object[] getUntyped() {
        activate();
        return _untyped;
    }

    public TADateItemArray next() {
        activate();
        return _next;
    }
}

package com.db4o.db4ounit.common.ta.ta;

import java.util.*;

import com.db4o.db4ounit.common.ta.*;

public class TADateItem extends ActivatableImpl {

    public static final long DAY = 1000 * 60 * 60 * 24;

    public Date _typed;

    public Object _untyped;

    public TADateItem _next;

    public TADateItem(Date date) {
        this._typed = date;
        this._untyped = date;
    }

    public static TADateItem itemList(Date date, int depth) {
        if (depth == 0) {
            return null;
        }
        TADateItem header = new TADateItem(date);
        header._next = itemList(new Date(date.getTime() + (depth - 1) * DAY),
                depth - 1);
        return header;
    }

    public Date getTyped() {
        activate();
        return _typed;
    }

    public Object getUntyped() {
        activate();
        return _untyped;
    }

    public TADateItem next() {
        activate();
        return _next;
    }

    public String toString() {
        return _typed + "[" + (_next == null ? null : "next") + "]";
    }
}

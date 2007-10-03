/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.handlers;

import java.util.*;

import com.db4o.internal.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public class DateHandler0 extends DateHandler{

    public DateHandler0(ObjectContainerBase container) {
        super(container);
    }
    
    public Object read(ReadContext context) {
        final long value = context.readLong();
        if (value == Long.MAX_VALUE) {
            return primitiveNull();
        }
        return new Date(value);
    }

}

/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.internal.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public class StringHandler0 extends StringHandler {

    public StringHandler0(TypeHandler4 template) {
        super(template);
    }
    
    public Object read(ReadContext readContext) {
        UnmarshallingContext context = (UnmarshallingContext) readContext;
        Buffer reader =   
            context.container().bufferByAddress(context.readInt(), context.readInt()); 
        if (reader == null) {
            return null;
        }
        return readString(context, reader);
    }

}

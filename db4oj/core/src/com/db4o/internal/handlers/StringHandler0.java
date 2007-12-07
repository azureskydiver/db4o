/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.internal.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public class StringHandler0 extends StringHandler {

    public StringHandler0(TypeHandler4 template) {
        super(template);
    }
    
    public Object read(ReadContext context) {
        Buffer buffer = readIndirectedBuffer(context); 
        if (buffer == null) {
            return null;
        }
        return readString(context, buffer);
    }
    
    public void delete(DeleteContext context){
    	super.delete(context);
    	context.defragmentRecommended();
    }

}

/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.marshall;

import com.db4o.*;


public class StringMarshaller1 extends StringMarshaller{
    
    public int marshalledLength(YapStream stream, Object obj) {
        if(obj == null){
            return 0;
        }
        return stream.alignToBlockSize( stream.stringIO().length((String)obj) );
    }


}

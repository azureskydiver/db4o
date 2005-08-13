/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside;

import com.db4o.*;


public class Exceptions4 {

    public static final void throwRuntimeException (int code) {
        throwRuntimeException(code, null, null);
    }

    public static final void throwRuntimeException (int code, Throwable cause) {
    	throwRuntimeException(code, null, cause);
    }

    public static final void throwRuntimeException (int code, String msg) {
        throwRuntimeException(code, msg, null);
    }

    public static final void throwRuntimeException (int code, String msg, Throwable cause) {
    	Messages.logErr(Db4o.configure(), code,msg, cause);
        throw new RuntimeException(Messages.get(code, msg));
    }
    
    public static final void notSupported(){
        if(Deploy.csharp){
            throw new UnsupportedOperationException();
        }else{
            throwRuntimeException(53);
        }
    }

}

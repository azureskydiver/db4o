/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.diagnostic;

import com.db4o.diagnostic.*;


/**
 * @exclude
 * 
 * This is just a very basic first implementation that allows
 * passing String messages.
 * 
 * Possible future content of classes that implement Diagnostic
 * could be:
 * - time something takes
 * - severity
 * - the "cause" object itself
 * - individual classes for individual cases
 */
public class DiagnosticMessage implements Diagnostic{
    
    private final String _message;
    
    public DiagnosticMessage(String message){
        _message = message;
    }
    
    public String toString() {
        return _message;
    }

}

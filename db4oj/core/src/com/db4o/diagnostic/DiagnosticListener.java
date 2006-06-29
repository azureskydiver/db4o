/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.diagnostic;


/**
 * listens to Diagnostic messages. 
 * <br><br>Create a class that implements this listener interface and add
 * the listener by calling Db4o.configure().diagnostic().addListener().
 * @see DiagnosticConfiguration
 */
public interface DiagnosticListener {
    
    /**
     * this method will be called with Diagnostic messages.  
     */
    public void onDiagnostic(Diagnostic d);

}

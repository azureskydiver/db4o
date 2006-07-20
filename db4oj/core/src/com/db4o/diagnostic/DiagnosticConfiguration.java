/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.diagnostic;


/**
 * provides methods to configure the behaviour of db4o diagnostics.
 * <br><br>Diagnostic listeners can be be added and removed with calls
 * to this interface.
 * To install the most basic listener call:<br>
 * <code>Db4o.configure().diagnostic().addListener(new DiagnosticToConsole());</code>
 */
public interface DiagnosticConfiguration {
    
    /**
     * adds a DiagnosticListener to listen to Diagnostic messages.
     */
    public void addListener(DiagnosticListener listener);
    
    /**
     * removes all DiagnosticListeners.
     */
    public void removeAllListeners();

	public void queryStatistics(boolean enabled);

	public boolean queryStatistics();
}

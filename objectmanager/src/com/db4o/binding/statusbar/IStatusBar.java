/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.statusbar;

public interface IStatusBar {

    /**
     * Sets the status bar message
     * 
     * @param message the message to set
     */
    void setMessage(String message);

    /**
     * Clears whatever message is in the status bar
     */
    void clearMessage();

}

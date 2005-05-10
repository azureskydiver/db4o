/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.statusbar;

public class StatusBar {
    
    private static class NullStatusBar implements IStatusBar {
        public void setMessage(String message) {
        }
        public void clearMessage() {
        }
    }
    
    private static IStatusBar statusBar = new NullStatusBar();
    
    public static void setStatusBar(IStatusBar statusBar) {
        StatusBar.statusBar = statusBar;
    }

    public static IStatusBar getDefault() {
        return statusBar;
    }

}

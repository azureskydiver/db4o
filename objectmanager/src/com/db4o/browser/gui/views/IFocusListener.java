/*
 * Created on Jan 24, 2005
 */
package com.db4o.browser.gui.views;

/**
 * Interface FocusListener.
 *
 * @author djo
 */
public interface IFocusListener {
    /**
     * Called when the browser pane's object focus changes.
     * @param e the FocusEvent
     */
    public void focusChanged(ObjectFocusEvent e);
}

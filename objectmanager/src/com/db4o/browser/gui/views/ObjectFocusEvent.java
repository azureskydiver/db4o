/*
 * Created on Jan 24, 2005
 */
package com.db4o.browser.gui.views;

import com.db4o.browser.gui.tree.ITreeNode;

/**
 * Class FocusEvent.  The event object that is sent when the focus changes.
 *
 * @author djo
 */
public class ObjectFocusEvent {
    public final ITreeNode focusedNode;
    
    public ObjectFocusEvent(ITreeNode focusedNode) {
        this.focusedNode = focusedNode;
    }
}

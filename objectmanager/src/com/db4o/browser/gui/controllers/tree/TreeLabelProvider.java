/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.gui.controllers.tree;

import org.eclipse.jface.viewers.LabelProvider;

import com.db4o.browser.gui.standalone.StandaloneBrowser;
import com.db4o.browser.model.GraphPosition;
import com.db4o.browser.model.nodes.IModelNode;
import com.swtworkbench.community.xswt.metalogger.Logger;

/**
 * TreeLabelProvider.
 *
 * @author djo
 */
public class TreeLabelProvider extends LabelProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
        try {
            GraphPosition pos = (GraphPosition) element;
            IModelNode current = pos.getCurrent();
    		return current == null ? "null" : current.getText();
        } catch (Throwable t) {
            Logger.log().error(t, "Exception getting tree label");
            return "Please email " + StandaloneBrowser.LOGFILE + " to support@db4o.com";
        }
    }

}

/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.gui.controllers;

import org.eclipse.jface.viewers.LabelProvider;

import com.db4o.browser.model.GraphPosition;

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
        GraphPosition pos = (GraphPosition) element;
		return pos.getCurrent().getText();
	}

}

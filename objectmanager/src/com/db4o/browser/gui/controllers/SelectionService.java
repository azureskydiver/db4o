/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.gui.controllers;

import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;

/**
 * Class SelectionService.  Tracks the current application selection and broadcasts changes to all 
 * interested listeners.
 *
 * @author djo
 */
public class SelectionService implements ISelectionProvider, ISelectionChangedListener {
	
	private static SelectionService selectionService = null;
	
	/**
	 * Method getDefault.  Return the SelectionService singleton.
	 * 
	 * @return SelectionService the SelectionService singleton.
	 */
	public static SelectionService getDefault() {
		if (selectionService == null) {
			selectionService = new SelectionService();
		}
		return selectionService;
	}
	
	private ISelection selection;
	private LinkedList selectionChangedListeners = new LinkedList();
	
	private void fireSelectionChangedEvent() {
		SelectionChangedEvent e = new SelectionChangedEvent(this, selection);
		for (Iterator i = selectionChangedListeners.iterator(); i.hasNext();) {
			ISelectionChangedListener listener = (ISelectionChangedListener) i.next();
			listener.selectionChanged(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection() {
		return selection;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection) {
		this.selection=selection;
		fireSelectionChangedEvent();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		setSelection(selection);
	}
	
}

/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.gui.controllers.tree;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;

import com.db4o.browser.model.GraphPosition;
import com.db4o.browser.model.IGraphIterator;
import com.swtworkbench.community.xswt.metalogger.Logger;

/**
 * TreeSelectionChangedController.  When the tree's selection changes, updates
 * the model's selection.
 *
 * @author djo
 */
public class TreeSelectionChangedController implements
		ISelectionChangedListener {

	private int treeSelectionChanging=0;
    
    private GraphPosition lastSelection = null;

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		try {
			++treeSelectionChanging;
			
			IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			
			if (!selection.isEmpty()) {
				GraphPosition node = (GraphPosition) selection.getFirstElement();
	
				final TreeViewer source = (TreeViewer) event.getSource();
				IGraphIterator model = (IGraphIterator) source.getInput();
                if (model.isPathSelectionChangable()) {
                    model.setSelectedPath(node);
                    lastSelection = node;
                    source.refresh();
                } else if (lastSelection != null) {
                    Display.getCurrent().asyncExec(new Runnable() {
                        public void run() {
                            source.setSelection(new StructuredSelection(lastSelection));
                        }
                    });
                } else {
                    lastSelection = node;
                    throw new RuntimeException(getClass().getName() + ": Cannot reset the selection back to null!");
                }
			}
		} catch (Throwable t) {
            Logger.log().error(t, "Exception handling tree selection change");
		} finally {
            --treeSelectionChanging;
        }
	}
	
	/**
	 * Indicates to clients if the tree's selection is in the process of
	 * changing.
	 * 
	 * @return true if the tree's selection is changing; false otherwise.
	 */
	public boolean isTreeSelectionChanging() {
		return treeSelectionChanging > 0;
	}

    /**
     * Delegated from SelectionChangedController (IGraphIteratorSelectionListener method)
     * 
     * @return true always.
     */
    public boolean canSelectionChange() {
        return true;
    }

}

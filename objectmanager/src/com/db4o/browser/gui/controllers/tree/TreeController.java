/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.gui.controllers.tree;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ve.sweet.controllers.RefreshService;
import org.eclipse.ve.sweet.objectviewer.IEditStateListener;
import org.eclipse.ve.sweet.objectviewer.IObjectViewer;

import com.db4o.browser.gui.controllers.BrowserTabController;
import com.db4o.browser.gui.controllers.IBrowserController;
import com.db4o.browser.gui.controllers.SelectionChangedController;
import com.db4o.browser.model.GraphPosition;
import com.db4o.browser.model.IGraphIterator;
import com.db4o.browser.model.nodes.ClassNode;
import com.db4o.browser.model.nodes.IModelNode;

/**
 * TreeController.
 *
 * @author djo
 */
public class TreeController implements IBrowserController {
	private final BrowserTabController parent;
	private final TreeViewer viewer;
	private final SelectionChangedController selectionListener;
	
	public TreeController(final BrowserTabController parent, Tree tree) {
		this.parent = parent;
		this.viewer = new TreeViewer(tree);
		
        viewer.setContentProvider(new TreeContentProvider());
        viewer.setLabelProvider(new TreeLabelProvider());
		final TreeSelectionChangedController treeSelectionChangedController = new TreeSelectionChangedController();
		viewer.addSelectionChangedListener(treeSelectionChangedController);
        
        tree.addMouseListener(new MouseAdapter() {
            public void mouseDoubleClick(MouseEvent e) {
                IGraphIterator input = (IGraphIterator) viewer.getInput();
                input.reset();
                if (input.hasNext()) {
                	GraphPosition selectedNode = (GraphPosition)((StructuredSelection)viewer.getSelection()).getFirstElement();
                	input.setPath(selectedNode);
                    IModelNode selection = (IModelNode) input.next();
                    input.previous();
                    if (selection instanceof ClassNode) {
                        ClassNode node = (ClassNode) selection;
                        parent.getQueryController().open(node.getReflectClass(), parent.getCurrentConnection().path());
                    }
                }
            }
        });
        
        tree.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if (e.character=='*')
                    e.doit = false;
            }

            public void keyReleased(KeyEvent e) {
                if (e.character=='*')
                    e.doit = false;
            }
        });

		selectionListener = parent.getSelectionChangedController();
		selectionListener.setTreeViewer(viewer);
		selectionListener.setTreeSelectionChangedController(treeSelectionChangedController);
		
		RefreshService.getDefault().addEditStateListener(editStateListener);
	}
	
	private IEditStateListener editStateListener = new IEditStateListener() {
		public void stateChanged(IObjectViewer sender) {
			if (viewer.getTree().isDisposed()) {
				RefreshService.getDefault().removeEditStateListener(editStateListener);
				return;
			}
			if (!sender.isDirty())
				viewer.refresh();
		}
	};
	
	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.controllers.IBrowserController#setInput(com.db4o.browser.model.IGraphIterator, com.db4o.browser.model.GraphPosition)
	 */
	public void setInput(IGraphIterator input, GraphPosition selection) {
		IGraphIterator oldInput = (IGraphIterator) viewer.getInput();
		if (oldInput != null)
			oldInput.removeSelectionChangedListener(selectionListener);
		
		viewer.setInput(input);
		input.addSelectionChangedListener(selectionListener);
	}

    public void deselectAll() {
        viewer.setSelection(StructuredSelection.EMPTY);
        viewer.collapseAll();
    }
}

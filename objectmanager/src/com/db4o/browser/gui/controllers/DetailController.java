/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.gui.controllers;

import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ve.sweet.CannotSaveException;
import org.eclipse.ve.sweet.objectviewer.IObjectViewer;

import com.db4o.browser.gui.views.DbBrowserPane;
import com.db4o.browser.model.GraphPosition;
import com.db4o.browser.model.IGraphIterator;
import com.db4o.browser.model.nodes.IModelNode;
import com.swtworkbench.community.xswt.metalogger.Logger;

/**
 * DetailController.  A Controller for the detail pane of the browser.
 *
 * @author djo
 */
public class DetailController implements IBrowserController {
	private DbBrowserPane ui;
    private Composite parentComposite;
	private BrowserController parent;
    private IGraphIterator input = null;
    private GraphPosition selection;

	/**
	 * Constructor DetailController.  Create an MVC controller to manage the detail pane.
	 * 
	 * @param parent
	 * @param ui
	 */
	public DetailController(BrowserController parent, DbBrowserPane ui) {
		this.parent = parent;
		this.ui = ui;
        parentComposite = ui.getFieldArea();
		
		parent.getSelectionChangedController().setDetailController(this);
	}

	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.controllers.IBrowserController#setInput(com.db4o.browser.model.IGraphIterator)
	 */
	public void setInput(IGraphIterator input, GraphPosition selection) {
        Composite parent = ui.getFieldArea();
        disposeChildren(parent);
        
        this.input = input;
        this.selection = selection;
		if (selection != null) {
			input.setPath(selection);
            
            // If the current element has children, we actually want to display/
            // edit the children of the current element
            if (input.nextHasChildren()) {
				input.selectNextChild();
                buildUI(input);
				input.selectParent();
			} else {
                buildUI(input);
			}
            
		}
	}

    private static final Color WHITE = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
    private LinkedList objectViewers = new LinkedList();
    
	private void buildUI(IGraphIterator input) {
        objectViewers.clear();
        
        // Get the parent object of the fields that we are editing
        input.selectParent();
        IModelNode parent = (IModelNode) input.next();
        input.previous();
        input.selectNextChild();

        // Create an ObjectViewer on the parent if it can be edited
        IObjectViewer objectViewer = null; 
        if (parent.getEditValue() != null) {
            objectViewer = parent.getDatabase().construct(parent.getEditValue());
            objectViewers.add(objectViewer);
        }
        
        // Build the layout: start with the container Composite
        Composite detailViewHolder = new Composite(parentComposite, SWT.NULL);
        detailViewHolder.setLayout(new GridLayout(2, false));
        detailViewHolder.setBackground(WHITE);
        
        // Build each row by iterating over the fields
        int i=0;
        while (input.hasNext()) {
            IModelNode fieldToDisplay = (IModelNode) input.next();
            
            // The field name...
            Label fieldName = new Label(detailViewHolder, SWT.NULL);
            fieldName.setBackground(WHITE);
            fieldName.setText(fieldToDisplay.getName());
            
            // Create an editor or a Label for the value
            if (fieldToDisplay.isEditable()) {
                Text fieldValue = new Text(detailViewHolder, SWT.BORDER);
                fieldValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
                fieldValue.setBackground(WHITE);
                if (objectViewer.bind(fieldValue, fieldToDisplay.getName()) == null)
                    Logger.log().error(new Exception(), "Unable to bind property: " + fieldToDisplay.getName());
            } else {
                Label fieldValue = new Label(detailViewHolder, SWT.NULL);
                fieldValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
                fieldValue.setBackground(WHITE);
                fieldValue.setText(fieldToDisplay.getValueString());
            }
            
        }
        
        // We have to manually compute and set the size because we're inside
        // a ScrolledComposite here...
        Point parentPreferredSize = detailViewHolder.computeSize(parentComposite.getParent().getSize().x, SWT.DEFAULT, true);
        Point preferredSize = detailViewHolder.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
        if (parentPreferredSize.x > preferredSize.x)
            parentComposite.setBounds(new Rectangle(0, 0, parentPreferredSize.x, parentPreferredSize.y));
        else
            parentComposite.setBounds(new Rectangle(0, 0, preferredSize.x, preferredSize.y));
        parentComposite.layout(true);
    }

    private void disposeChildren(Composite parent) {
        Control[] children = parent.getChildren();
        for (int i = 0; i < children.length; i++) {
            children[i].dispose();
        }
    }

    public void deselectAll() {
        if (input != null)
            setInput(input, null);
    }

    public boolean canSelectionChange() {
        for (Iterator objectViewersIter = objectViewers.iterator(); objectViewersIter.hasNext();) {
            IObjectViewer viewer = (IObjectViewer) objectViewersIter.next();
            try {
                viewer.commit();
            } catch (CannotSaveException e) {
                return false;
            }
        }
        return true;
    }

}


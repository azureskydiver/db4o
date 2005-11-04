/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.gui.controllers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.eclipse.ve.sweet.controllers.RefreshService;
import org.eclipse.ve.sweet.objectviewer.IEditStateListener;
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
	private BrowserTabController parent;
    private IGraphIterator input = null;
    private GraphPosition selection;

	/**
	 * Constructor DetailController.  Create an MVC controller to manage the detail pane.
	 * 
	 * @param parent
	 * @param ui
	 */
	public DetailController(BrowserTabController parent, DbBrowserPane ui) {
		this.parent = parent;
		this.ui = ui;
        parentComposite = ui.getFieldArea();
        parentComposite.addDisposeListener(disposeListener);
        
        ui.getSaveButton().addSelectionListener(saveEdits);
        ui.getCancelButton().addSelectionListener(cancelEdits);
		
		parent.getSelectionChangedController().setDetailController(this);
		
		RefreshService.getDefault().addEditStateListener(refreshListener);
	}

	private SelectionListener saveEdits = new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
			try {
				objectViewer.commit();
			} catch (CannotSaveException e1) {
				// The user has already been informed...
			}
		}
	};
	
	private SelectionListener cancelEdits = new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
			objectViewer.rollback();
		};
	};

	private IEditStateListener refreshListener = new IEditStateListener() {
		public void stateChanged(IObjectViewer sender) {
			refresh(sender);
		}
	};

    protected void refresh(IObjectViewer sender) {
		if (sender != objectViewer && !sender.isDirty()) {
			setInput(input, selection);
		}
	}

	private DisposeListener disposeListener = new DisposeListener() {
		public void widgetDisposed(DisposeEvent e) {
	        if (objectViewer != null) {
	        	objectViewer.removeObjectListener(RefreshService.getDefault());
		        parent.getEditStateController().removeObjectViewer(objectViewer);
	        }
	        RefreshService.getDefault().removeEditStateListener(refreshListener);
	    }
	};

	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.controllers.IBrowserController#setInput(com.db4o.browser.model.IGraphIterator)
	 */
	public void setInput(IGraphIterator input, GraphPosition selection) {
        disposeChildren(parentComposite);
        
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
	private IObjectViewer objectViewer = null;
    
	private void buildUI(IGraphIterator input) {
		if (!input.hasParent()) {
			return;
		}
		
        // Get the parent object of the fields that we are editing
        input.selectParent();
        
        if (!input.hasNext()) {
        	return;
        }
        
        IModelNode parent = (IModelNode) input.next();
        input.previous();
        input.selectNextChild();
        
        // Remove the old state
        if (objectViewer != null) {
        	objectViewer.removeObjectListener(RefreshService.getDefault());
	        this.parent.getEditStateController().removeObjectViewer(objectViewer);
        }

        // Create an ObjectViewer on the parent if it can be edited
        objectViewer = null; 
        if (parent.getEditValue() != null) {
            objectViewer = parent.getDatabase().construct(parent.getEditValue());
            this.parent.getEditStateController().addObjectViewer(objectViewer);
            objectViewer.addObjectListener(RefreshService.getDefault());
        }
        
        // Build the layout: start with the container Composite
        Composite detailViewHolder = new Composite(parentComposite, SWT.NULL);
        detailViewHolder.setLayout(new GridLayout(2, false));
        detailViewHolder.setBackground(WHITE);
        
        // Build each row by iterating over the fields
        while (input.hasNext()) {
            IModelNode fieldToDisplay = (IModelNode) input.next();
            
            // The field name...
            Label fieldName = new Label(detailViewHolder, SWT.NULL);
            fieldName.setBackground(WHITE);
            fieldName.setText(fieldToDisplay.getName());
            
            // Create an editor or a Label for the value
            if (fieldToDisplay.isEditable()) {
            	// If there's a field name, good, bind it.
            	if (!fieldToDisplay.getName().equals("")) {
	                Text fieldValue = new Text(detailViewHolder, SWT.BORDER);
	                fieldValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	                fieldValue.setBackground(WHITE);
	                if (objectViewer.bind(fieldValue, fieldToDisplay.getName()) == null) {
	                    Logger.log().debug(getClass(), "Unable to bind property: " + fieldToDisplay.getName());
	                    fieldValue.dispose();
	                    createReadonlyField(detailViewHolder, fieldToDisplay);
	                }
	            // If there's not a field name, we've got a collection.  Display the field read-only for now.
	            // (The only type we could edit inside a collection is java.lang.String, which won't happen that often.)
            	} else {
                    createReadonlyField(detailViewHolder, fieldToDisplay);
            	}
            } else {
                createReadonlyField(detailViewHolder, fieldToDisplay);
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

	private void createReadonlyField(Composite detailViewHolder, IModelNode fieldToDisplay) {
		Label fieldValue = new Label(detailViewHolder, SWT.NULL);
		fieldValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fieldValue.setBackground(WHITE);
		fieldValue.setText(fieldToDisplay.getValueString());
	}

    private void disposeChildren(Composite parent) {
    	if (objectViewer != null) {
	    	objectViewer.removeObjectListener(RefreshService.getDefault());
	        this.parent.getEditStateController().removeObjectViewer(objectViewer);
	        objectViewer = null;
    	}
        
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
    	if (objectViewer == null) {
    		return true;
    	}
    	
    	try {
    		objectViewer.commit();
    	} catch (CannotSaveException e) {
    		return false;
    	}
        return true;
    }

	public boolean canClose() {
		return canSelectionChange();
	}

}


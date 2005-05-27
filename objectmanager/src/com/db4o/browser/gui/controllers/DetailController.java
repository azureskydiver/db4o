/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.gui.controllers;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.db4o.binding.dataeditors.IObjectEditor;
import com.db4o.browser.gui.controllers.detail.generator.LayoutGenerator;
import com.db4o.browser.gui.controllers.detail.generator.StringInputStreamFactory;
import com.db4o.browser.gui.views.DbBrowserPane;
import com.db4o.browser.model.GraphPosition;
import com.db4o.browser.model.IGraphIterator;
import com.db4o.browser.model.nodes.IModelNode;
import com.swtworkbench.community.xswt.XSWT;
import com.swtworkbench.community.xswt.XSWTException;

/**
 * DetailController.  A Controller for the detail pane of the browser.
 *
 * @author djo
 */
public class DetailController implements IBrowserController {
	private DbBrowserPane ui;
	private BrowserController parent;
	
	/**
	 * Constructor DetailController.  Create an MVC controller to manage the detail pane.
	 * 
	 * @param parent
	 * @param ui
	 */
	public DetailController(BrowserController parent, DbBrowserPane ui) {
		this.parent = parent;
		this.ui = ui;
		
		parent.getSelectionChangedController().setDetailController(this);
	}

	private static final String objectDetailTemplate = LayoutGenerator.resourceFile("objectDetailTemplate.xswt");
    private IGraphIterator input = null;

	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.controllers.IBrowserController#setInput(com.db4o.browser.model.IGraphIterator)
	 */
	public void setInput(IGraphIterator input, GraphPosition selection) {
        this.input = input;
		if (selection != null) {
			input.setPath(selection);
            
            // Create the editor layout
			if (input.nextHasChildren()) {
				input.selectNextChild();
				Map layout = buildUI(LayoutGenerator.fillTemplateString(input, objectDetailTemplate), ui.getFieldArea());
                if (layout != null)
                    bindEditors(layout, input);
				input.selectParent();
			} else {
				Map layout = buildUI(LayoutGenerator.fillTemplateString(input, objectDetailTemplate), ui.getFieldArea());
                if (layout != null)
                    bindEditors(layout, input);
			}
            
		} else {
            buildUI(null, ui.getFieldArea());
		}
	}

	private Map buildUI(String layout, Composite parent) {
		disposeChildren(parent);
        Map result = null;
        if (layout != null) {
    		try {
    			result = XSWT.create(parent, StringInputStreamFactory.construct(layout));
    			// We have to manually compute and set the size because we're inside
    			// a ScrolledComposite here...
    			Point preferredSize = parent.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
    			parent.setBounds(new Rectangle(0, 0, preferredSize.x, preferredSize.y));
    			parent.layout(true);
    		} catch (XSWTException e) {
    			throw new RuntimeException("Unable to create field area layout", e);
    		}
        } else {
            Point preferredSize = parent.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
            parent.setBounds(new Rectangle(0, 0, preferredSize.x, preferredSize.y));
            parent.layout(true);
        }
        return result;
	}

    private void bindEditors(Map layout, IGraphIterator input) {
        // We can only edit fields/properties of an object...
        if (input.hasParent()) {
            // Grab the object to edit
            input.selectParent();
            IModelNode editNode = (IModelNode) input.next();
            input.selectPreviousChild();
            
            Object toEdit = editNode.getEditValue();
            if (toEdit == null) {
                // This must be a ClassNode or something of that ilk
                return;
            }
            
            final IObjectEditor editor = editNode.getDatabase().construct(editNode.getEditValue());
            Composite detailViewHolder = (Composite) layout.get("DetailViewHolder");
            if (detailViewHolder ==null) {
                throw new RuntimeException("Unable to get detail view holder from XSWT");
            }
            detailViewHolder.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent e) {
                    editor.removeListeners();
                }
            });
            
            int fieldNo = 1;
            while (input.hasNext()) {
                IModelNode field = (IModelNode) input.next();
                if (field.isEditable()) {
                    String editorName = "FieldValue" + fieldNo;
                    Control control = (Control) layout.get(editorName);
                    if (control instanceof Text) {
                        editor.bind(control, field.getName());
                    }
                }
                ++fieldNo;
            }
        }
    }

    /**
     * @param parent
     */
    private void disposeChildren(Composite parent) {
        if (parent.getChildren().length > 0) {
            parent.getChildren()[0].dispose();
        }
    }
    

    public void deselectAll() {
        if (input != null)
            setInput(input, null);
    }

}

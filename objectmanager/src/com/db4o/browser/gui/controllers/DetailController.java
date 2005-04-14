/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.gui.controllers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import com.db4o.browser.gui.controllers.detail.generator.LayoutGenerator;
import com.db4o.browser.gui.controllers.detail.generator.StringInputStreamFactory;
import com.db4o.browser.gui.views.DbBrowserPane;
import com.db4o.browser.model.GraphPosition;
import com.db4o.browser.model.IGraphIterator;
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

	private static final String containerDetailTemplate = LayoutGenerator.resourceFile("containerDetailTemplate.xswt");
	private static final String objectDetailTemplate = LayoutGenerator.resourceFile("objectDetailTemplate.xswt");
    private IGraphIterator input = null;

	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.controllers.IBrowserController#setInput(com.db4o.browser.model.IGraphIterator)
	 */
	public void setInput(IGraphIterator input, GraphPosition selection) {
        this.input = input;
		if (selection != null) {
			input.setPath(selection);
			if (input.nextHasChildren()) {
				input.selectNextChild();
				buildUI(LayoutGenerator.fillTemplateString(input, objectDetailTemplate), ui.getFieldArea());
				input.selectParent();
			} else {
				buildUI(LayoutGenerator.fillTemplateString(input, objectDetailTemplate), ui.getFieldArea());
			}
		} else {
            buildUI(null, ui.getFieldArea());
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
	

	private void buildUI(String layout, Composite parent) {
		disposeChildren(parent);
        if (layout != null) {
    		try {
    			XSWT.create(parent, StringInputStreamFactory.construct(layout));
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
	}

    public void deselectAll() {
        if (input != null)
            setInput(input, null);
    }

}

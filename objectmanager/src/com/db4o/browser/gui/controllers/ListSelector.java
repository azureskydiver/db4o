/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.gui.controllers;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

public class ListSelector extends Dialog {

    private List list;
    private IListPopulator listPopulator;

    protected ListSelector(Shell parentShell) {
        super(parentShell);
    }

    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        Composite dialogArea = new Composite(container, SWT.NULL);
        dialogArea.setLayout(new GridLayout());
        list = new List(dialogArea, SWT.BORDER | SWT.V_SCROLL);
        GridData gd = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
        gd.heightHint = 500;
        list.setLayoutData(gd);
        listPopulator.populate(list);
        list.addMouseListener(new MouseAdapter() {
            public void mouseDoubleClick(MouseEvent e) {
                okPressed();
            }
        });
        return container;
    }
    
    protected void okPressed() {
        selection = list.getSelectionIndex();
        super.okPressed();
    }
    
    private int selection = -1;

    /**
     * @return Returns the selection.
     */
    public int getSelection() {
        return selection;
    }

    /**
     * @return Returns the listPopulator.
     */
    public IListPopulator getListPopulator() {
        return listPopulator;
    }
    

    /**
     * @param listPopulator The listPopulator to set.
     */
    public void setListPopulator(IListPopulator listPopulator) {
        this.listPopulator = listPopulator;
    }
    
    
}

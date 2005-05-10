/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.swt;

import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionListener;

/**
 * ICheckboxButtonField. 
 *
 * @author djo
 */
public interface ICheckboxButtonField {
    void setSelection(boolean value);
    boolean getSelection();
    
    void addSelectionListener(SelectionListener l);
    void removeSelectionListener(SelectionListener l);

    void addDisposeListener(DisposeListener l);
    void removeDisposeListener(DisposeListener l);
}

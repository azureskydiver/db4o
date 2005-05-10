/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.swt;

import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionListener;


/**
 * ISetSelectorField. A strict duck interface for things like list boxes or
 * groups of check boxes that allow the user to pick a bunch of choices from
 * a set of valid values
 *
 * @author djo
 */
public interface ICheckboxListField {
    int getSelectionIndex();
    void setSelection(int index);
    
    String [] getSelection();
    int [] getSelectionIndices();
    void setSelection(int[] indices);
    
    void removeAll();
    
    void add(String item);
    
    void addSelectionListener(SelectionListener l);
    void removeSelectionListener(SelectionListener l);

    void addDisposeListener(DisposeListener l);
    void removeDisposeListener(DisposeListener l);
}

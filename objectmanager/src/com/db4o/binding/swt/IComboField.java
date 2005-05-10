/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.swt;

import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;

public interface IComboField {
    Point getSelection();
    int getSelectionIndex();
    
    void select(int index);
    
    String getText();
    void setText(String value);
    
    void removeAll();
    
    void add(String item);
    
    void addSelectionListener(SelectionListener l);
    void removeSelectionListener(SelectionListener l);

    void addDisposeListener(DisposeListener l);
    void removeDisposeListener(DisposeListener l);
}

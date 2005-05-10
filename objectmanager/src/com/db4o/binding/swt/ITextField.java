/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.swt;

import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.VerifyListener;

public interface ITextField {
    void setText(String value);
    String getText();
    
    void addVerifyListener(VerifyListener l);
    void removeVerifyListener(VerifyListener l);
    
    void setSelection(int begin, int end);

    void addDisposeListener(DisposeListener l);
    void removeDisposeListener(DisposeListener l);
    
    void addFocusListener(FocusListener l);
    void removeFocusListener(FocusListener l);
    void setFocus();
}

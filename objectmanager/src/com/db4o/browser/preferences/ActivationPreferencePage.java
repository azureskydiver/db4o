/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class ActivationPreferencePage extends PreferencePage {

	ActivationPreferencePagePanel panel;
	
	protected Control createContents(Composite parent) {
		parent.setLayout(new FillLayout());
		panel = new ActivationPreferencePagePanel(parent, SWT.NULL);
		return panel;
	}

}

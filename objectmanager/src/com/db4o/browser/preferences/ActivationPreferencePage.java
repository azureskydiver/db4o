/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * ActivationPreferencePage.
 *
 * @author djo
 */
public class ActivationPreferencePage extends PreferencePage {

	ActivationPreferencePagePanel panel;
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		panel = new ActivationPreferencePagePanel(parent, SWT.NULL);
		PreferencesCore prefs = PreferencesCore.getDefault();
		
		panel.getInitialActivationDepth().setSelection(prefs.getInitialActivationDepth());
		panel.getSubsequentActivationDepth().setSelection(prefs.getSubsequentActivationDepth());
		
		return panel;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
		PreferencesCore prefs = PreferencesCore.getDefault();
		
		// Global activation depth handling...
		prefs.setInitialActivationDepth(panel.getInitialActivationDepth().getSelection());
		prefs.setSubsequentActivationDepth(panel.getSubsequentActivationDepth().getSelection());
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		PreferencesCore prefs = PreferencesCore.getDefault();
		
		prefs.setInitialActivationDepth(PreferencesCore.DEFAULT_INITIAL_ACTIVATION_DEPTH);
		panel.getInitialActivationDepth().setSelection(prefs.getInitialActivationDepth());
		
		prefs.setSubsequentActivationDepth(PreferencesCore.DEFAULT_SUBSEQUENT_ACTIVATION_DEPTH);
		panel.getSubsequentActivationDepth().setSelection(prefs.getSubsequentActivationDepth());
	}

}

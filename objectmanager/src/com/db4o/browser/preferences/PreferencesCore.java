/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.preferences;

import com.db4o.browser.model.BrowserCore;
import com.db4o.browser.model.Database;

public class PreferencesCore {
	private static PreferencesCore prefs = null;
	
	public static PreferencesCore getDefault() {
		if (prefs == null) {
			prefs = new PreferencesCore();
		}
		return prefs;
	}

	public void registerPreferencesPages() {
		PreferenceUI.registerPreferencePage("Activation", "Object Activation", null, "com.db4o.browser.preferences.ActivationPreferencePage");
	}
	
	// Constants for default preference values...
	public static final int DEFAULT_INITIAL_ACTIVATION_DEPTH = 5;
	public static final int DEFAULT_SUBSEQUENT_ACTIVATION_DEPTH = 2;
	
	private int initialActivationDepth=DEFAULT_INITIAL_ACTIVATION_DEPTH;
	private int subsequentActivationDepth=DEFAULT_SUBSEQUENT_ACTIVATION_DEPTH;

	/**
	 * @return Returns the initialActivationDepth.
	 */
	public int getInitialActivationDepth() {
		return initialActivationDepth;
	}
	

	/**
	 * @param initialActivationDepth The initialActivationDepth to set.
	 */
	public void setInitialActivationDepth(int initialActivationDepth) {
		this.initialActivationDepth = initialActivationDepth;
		Database[] databases = BrowserCore.getDefault().getAllDatabases();
		for (int i = 0; i < databases.length; i++) {
			databases[i].setInitialActivationDepth(initialActivationDepth);
		}
	}
	

	/**
	 * @return Returns the subsequentActivationDepth.
	 */
	public int getSubsequentActivationDepth() {
		return subsequentActivationDepth;
	}
	

	/**
	 * @param subsequentActivationDepth The subsequentActivationDepth to set.
	 */
	public void setSubsequentActivationDepth(int subsequentActivationDepth) {
		this.subsequentActivationDepth = subsequentActivationDepth;
	}
	
	
	
}

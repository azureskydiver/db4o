/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.preferences;

import java.util.Map;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;

import com.swtworkbench.community.xswt.XSWT;

public class ActivationPreferencePagePanel extends Composite {
	private Map contents;

	public ActivationPreferencePagePanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout());
		contents = XSWT.createl(this, "ActivationPreferencePagePanel.xswt", getClass());
	}
	
	public Spinner getInitialActivationDepth() {
		return (Spinner) contents.get("InitialActivationDepth");
	}
	
	public Spinner getSubsequentActivationDepth() {
		return (Spinner) contents.get("SubsequentActivationDepth");
	}
}

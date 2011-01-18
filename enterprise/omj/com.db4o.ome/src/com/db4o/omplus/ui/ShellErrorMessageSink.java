/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.omplus.ui;

import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.swt.widgets.*;

import com.db4o.omplus.*;
import com.db4o.omplus.datalayer.*;

public class ShellErrorMessageSink implements ErrorMessageSink {
	
	private final Shell shell;
	
	public ShellErrorMessageSink(Shell shell) {
		this.shell = shell;
	}

	public void showError(String msg) {
		MessageDialog.openError(shell, OMPlusConstants.DIALOG_BOX_TITLE, msg);
	}

	public void logExc(Throwable exc) {
		IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, exc.getMessage(), exc);
		Activator.getDefault().getLog().log(status);
	}
}
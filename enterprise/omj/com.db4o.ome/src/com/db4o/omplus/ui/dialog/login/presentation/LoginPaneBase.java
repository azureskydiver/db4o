/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.omplus.ui.dialog.login.presentation;

import org.eclipse.jface.dialogs.*;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import com.db4o.foundation.*;
import com.db4o.omplus.datalayer.*;
import com.db4o.omplus.ui.dialog.login.model.*;

public abstract class LoginPaneBase extends Composite {

	private LoginPresentationModel model;
	
	public LoginPaneBase(Shell dialog, Composite parent, String openText, LoginPresentationModel model) {
		super(parent, SWT.NONE);
		this.model = model;
		setLayout(new FormLayout());
		createContents(dialog, parent, openText);
	}

	private void createContents(Shell dialog, Composite parent, String openText) {
		Composite innerComposite = new Composite(this, SWT.BORDER);
		populateInnerComposite(innerComposite, parent, model);
		FormData data = new FormData();
		data.top = new FormAttachment(2, 2);
		data.left = new FormAttachment(2, 2);
		data.right = new FormAttachment(98, -2);
		data.bottom = new FormAttachment(80, -2);
		innerComposite.setLayoutData(data);
		LoginButtonsPane buttonComposite = new LoginButtonsPane(this, dialog, openText, new Closure4<Boolean>() {
			public Boolean run() {
				return connect(model);
			}
		});
		data = new FormData();
		data.top = new FormAttachment(innerComposite, 2);
		data.left = new FormAttachment(2, 2);
		data.right = new FormAttachment(98, -2);
		data.bottom = new FormAttachment(98, -2);
		buttonComposite.setLayoutData(data);

	}

	protected void showErrorMsg(String msg){
		MessageDialog.openError((Shell)getParent(), OMPlusConstants.DIALOG_BOX_TITLE, msg);
	}

	protected abstract void populateInnerComposite(Composite innerComposite, Composite parent, LoginPresentationModel model);
	protected abstract boolean connect(LoginPresentationModel model);
	
}

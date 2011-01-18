/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.omplus.ui.dialog.login.presentation;

import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import com.db4o.foundation.*;
import com.db4o.omplus.connection.*;
import com.db4o.omplus.ui.dialog.login.model.*;

public class LoginPane<P extends ConnectionParams> extends Composite {

	private ConnectionPresentationModel<P> model;
	
	public LoginPane(Shell dialog, Composite parent, String openText, LoginPaneSpec<P> spec) {
		super(parent, SWT.NONE);
		model = spec.model();
		setLayout(new FormLayout());
		createContents(dialog, parent, openText, spec);
	}

	private void createContents(Shell dialog, Composite parent, String openText, LoginPaneSpec<P> spec) {
		Composite innerComposite = new Composite(this, SWT.BORDER);
		spec.create(this, innerComposite);
		FormData data = new FormData();
		data.top = new FormAttachment(2, 2);
		data.left = new FormAttachment(2, 2);
		data.right = new FormAttachment(98, -2);
		data.bottom = new FormAttachment(80, -2);
		innerComposite.setLayoutData(data);
		Closure4<Boolean> openAction = new Closure4<Boolean>() {
			public Boolean run() {
				return model.connect();
			}
		};
		Block4 customAction = new Block4() {
			public void run() {
				model.requestCustomConfig();
			}
		};
		LoginButtonsPane buttonComposite = new LoginButtonsPane(this, dialog, openText, openAction, customAction);
		data = new FormData();
		data.top = new FormAttachment(innerComposite, 2);
		data.left = new FormAttachment(2, 2);
		data.right = new FormAttachment(98, -2);
		data.bottom = new FormAttachment(98, -2);
		buttonComposite.setLayoutData(data);

		pack(true);
	}
	
}

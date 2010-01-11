/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.omplus.ui.dialog.login.presentation;

import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import com.db4o.foundation.*;
import com.db4o.omplus.ui.*;

public class LoginButtonsPane extends Composite {
	public static final String OK_BUTTON_ID = LoginButtonsPane.class.getName() + "$okButton";
	public static final String CANCEL_BUTTON_ID = LoginButtonsPane.class.getName() + "$cancelButton";

	private static final String CANCEL_TEXT = "Cancel";
	private Button cancelBtn;
	private Button openBtn;
	
	public LoginButtonsPane(Composite parent, Composite dialog, String openText, Closure4<Boolean> openAction) {
		super(parent, SWT.NONE);
		createContents(parent, dialog, openText, openAction);
	}

	// TODO register model as action listener and invoke closure from model in response
	private void createContents(Composite parent, final Composite dialog, String openText, final Closure4<Boolean> openAction) {	
		setLayout(new FormLayout());
		
		openBtn = new Button(this, SWT.PUSH);
		OMESWTUtil.assignWidgetId(openBtn, OK_BUTTON_ID);
		openBtn.setText(openText);
		openBtn.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				if(openAction.run()) {
					dialog.dispose();
				}
			}
		});
		
		cancelBtn = new Button(this, SWT.PUSH);
		OMESWTUtil.assignWidgetId(cancelBtn, CANCEL_BUTTON_ID);
		cancelBtn.setText(CANCEL_TEXT);
		cancelBtn.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				dialog.dispose();
			}
		});

		FormData data = new FormData();
		data.top = new FormAttachment(0);
		data.left = new FormAttachment(60);
		data.right = new FormAttachment(cancelBtn , -5);
		openBtn.setLayoutData(data);

		data = new FormData();
		data.top = new FormAttachment(0);
		data.left = new FormAttachment(81);
		data.right = new FormAttachment(100);
		cancelBtn.setLayoutData(data);	
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}
}

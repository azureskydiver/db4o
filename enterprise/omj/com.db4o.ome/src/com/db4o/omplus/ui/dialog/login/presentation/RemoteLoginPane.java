/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.omplus.ui.dialog.login.presentation;

import org.eclipse.jface.layout.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;

import com.db4o.omplus.ui.dialog.login.model.*;
import com.db4o.omplus.ui.dialog.login.model.LoginPresentationModel.*;

public class RemoteLoginPane extends LoginPaneBase {

	private static final String REMOTE_OPEN_TEXT = "Connect";

	private Text hostText;
	private Text portText;
	private Text usernameText;
	private Text passwordText;

	public RemoteLoginPane(Shell dialog, Composite parent, LoginPresentationModel model) {
		super(dialog, parent, REMOTE_OPEN_TEXT, model);
	}

	@Override
	protected void populateInnerComposite(Composite innerComposite, Composite parent, LoginPresentationModel model) {		
		Label recentConnectionsLabel = new Label(innerComposite, SWT.NONE);
		recentConnectionsLabel.setText("Recent Connections: ");
		Composite recentConnectionCombo = LoginDialogUtil.recentConnectionCombo(innerComposite, model, LoginPresentationModel.LoginMode.REMOTE);
		
		Label hostLabel = new Label(innerComposite, SWT.NONE);
		hostLabel.setText("Hostname: ");
		hostText  = new Text(innerComposite, SWT.BORDER);
				
		Label portLabel = new Label(innerComposite, SWT.NONE);
		portLabel.setText("Port: ");
		portText  = new Text(innerComposite, SWT.BORDER);
		
		Label usernameLabel = new Label(innerComposite, SWT.NONE);
		usernameLabel.setText("Username: ");
		usernameText  = new Text(innerComposite, SWT.BORDER);

		Label passwordLabel = new Label(innerComposite, SWT.NONE);
		passwordLabel.setText("Password: ");
		passwordText  = new Text(innerComposite, SWT.BORDER|SWT.PASSWORD);
		
		model.addListener(new RemoteSelectionListener() {
			public void remoteSelection(String host, int port, String user, String pwd) {
				hostText.setText(host);
				portText.setText(String.valueOf(port));
				usernameText.setText(user);
				passwordText.setText("");
			}
		});

		GridLayoutFactory.swtDefaults().numColumns(6).equalWidth(false).applyTo(innerComposite);
		GridDataFactory.swtDefaults().span(2, 1).align(SWT.BEGINNING, SWT.CENTER).applyTo(recentConnectionsLabel);
		GridDataFactory.swtDefaults().span(4, 1).grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(recentConnectionCombo);
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(hostLabel);
		GridDataFactory.swtDefaults().span(3, 1).grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(hostText);
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(portLabel);
		GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(portText);
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(usernameLabel);
		GridDataFactory.swtDefaults().span(2, 1).grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(usernameText);
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(passwordLabel);
		GridDataFactory.swtDefaults().span(2,1).grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(passwordText);
	}

	@Override
	protected boolean connect(LoginPresentationModel model) {
		return model.connect(hostText.getText(), portText.getText(), usernameText.getText(), passwordText.getText());
	}
}

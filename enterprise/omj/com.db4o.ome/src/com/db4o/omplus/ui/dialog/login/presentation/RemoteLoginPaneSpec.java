/* Copyright (C) 2010  Versant Inc.   http://www.db4o.com */

package com.db4o.omplus.ui.dialog.login.presentation;

import org.eclipse.jface.layout.*;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

import com.db4o.omplus.connection.*;
import com.db4o.omplus.ui.dialog.login.model.*;
import com.db4o.omplus.ui.dialog.login.model.RemotePresentationModel.*;

public class RemoteLoginPaneSpec implements LoginPaneSpec<RemoteConnectionParams> {

	private final RemotePresentationModel model;
	
	public RemoteLoginPaneSpec(LoginPresentationModel model) {
		this.model = new RemotePresentationModel(model);
	}

	public ConnectionPresentationModel<RemoteConnectionParams> model() {
		return model;
	}
	
	public void create(Composite parent, Composite innerComposite) {
		Label recentConnectionsLabel = new Label(innerComposite, SWT.NONE);
		recentConnectionsLabel.setText("Recent Connections: ");
		Composite recentConnectionCombo = LoginDialogUtil.recentConnectionCombo(innerComposite, model);
		
		Label hostLabel = new Label(innerComposite, SWT.NONE);
		hostLabel.setText("Hostname: ");
		final Text hostText  = new Text(innerComposite, SWT.BORDER);
				
		Label portLabel = new Label(innerComposite, SWT.NONE);
		portLabel.setText("Port: ");
		final Text portText  = new Text(innerComposite, SWT.BORDER);
		
		Label usernameLabel = new Label(innerComposite, SWT.NONE);
		usernameLabel.setText("Username: ");
		final Text usernameText  = new Text(innerComposite, SWT.BORDER);

		Label passwordLabel = new Label(innerComposite, SWT.NONE);
		passwordLabel.setText("Password: ");
		final Text passwordText  = new Text(innerComposite, SWT.BORDER|SWT.PASSWORD);
		
		StateListener stateListener = new StateListener(model, hostText, portText, usernameText, passwordText);
		hostText.addModifyListener(stateListener);
		portText.addModifyListener(stateListener);
		usernameText.addModifyListener(stateListener);
		passwordText.addModifyListener(stateListener);
		
		model.addListener(new RemoteSelectionListener() {
			public void remoteSelection(String host, String port, String user, String pwd) {
				hostText.setText(host);
				portText.setText(port);
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
	
	private static class StateListener implements ModifyListener {
		private final RemotePresentationModel model;
		private final Text host;
		private final Text port;
		private final Text user;
		private final Text pwd;
		
		public StateListener(RemotePresentationModel model, Text host, Text port, Text user, Text pwd) {
			this.model = model;
			this.host = host;
			this.port = port;
			this.user = user;
			this.pwd = pwd;
		}

		public void modifyText(ModifyEvent e) {
			model.state(host.getText(), port.getText(), user.getText(), pwd.getText());
		}
		
	}
}

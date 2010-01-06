/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.omplus.ui.dialog.login.presentation;

import org.eclipse.jface.layout.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;

import com.db4o.omplus.datalayer.*;
import com.db4o.omplus.ui.dialog.login.model.*;
import com.db4o.omplus.ui.dialog.login.model.LoginPresentationModel.*;

public class LocalLoginPane extends LoginPaneBase {
	public static final String NEW_CONNECTION_TEXT_ID = LocalLoginPane.class.getName() + "$newConnectionText";

	private static final String LOCAL_OPEN_TEXT = "Open";

	private Text newConnectionText;
	
	public LocalLoginPane(Shell dialog, Composite parent, LoginPresentationModel model) {
		super(dialog, parent, LOCAL_OPEN_TEXT, model);
	}
	
	@Override
	protected void populateInnerComposite(Composite innerComposite, final Composite parent, LoginPresentationModel model) {
		Label recentConnectionLabel = new Label(innerComposite, SWT.NONE);
		recentConnectionLabel.setText("Recent connections: ");

		Combo recentConnectionCombo = LoginDialogUtil.recentConnectionCombo(innerComposite, model, LoginPresentationModel.LoginMode.LOCAL);

		Label newConnectionLabel = new Label(innerComposite, SWT.NONE);
		newConnectionLabel.setText("New Connections:    ");

		newConnectionText = new Text(innerComposite, SWT.BORDER);
		newConnectionText.setTextLimit(255);
		newConnectionText.setData(OMPlusConstants.WIDGET_NAME_KEY, NEW_CONNECTION_TEXT_ID);
		model.addListener(new LocalSelectionListener() {
			public void localSelection(String path) {
				newConnectionText(path);
			}
		});
		
		String[] localConnections = model.recentConnections(LoginPresentationModel.LoginMode.LOCAL);
		if(localConnections.length > 0) {
			newConnectionText(localConnections[0]);
		}
		
		Button browseBtn = new Button(innerComposite, SWT.PUSH);
		try {
			browseBtn.setImage(ImageUtility.getImage(OMPlusConstants.BROWSE_ICON));
		}
		catch(Exception exc) {
			// FIXME
		}
		browseBtn.setToolTipText("Browse");

		browseBtn.addListener(SWT.MouseUp, new Listener() {
			public void handleEvent(Event event) {
				FileDialog fileChooser = new FileDialog(parent.getShell(), SWT.OPEN);
				String dbfile = fileChooser.open();
				if(dbfile != null){
					newConnectionText.setText(dbfile);
					newConnectionText.setToolTipText(dbfile);
				}
			}
		});		

		GridLayoutFactory.swtDefaults().numColumns(3).equalWidth(false).applyTo(innerComposite);
		GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(recentConnectionLabel);
		GridDataFactory.swtDefaults().span(2, 1).grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(recentConnectionCombo);
		GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(newConnectionLabel);
		GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(newConnectionText);
		GridDataFactory.swtDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(browseBtn);
	}
	
	private void newConnectionText(String path) {
		newConnectionText.setText(path);
		newConnectionText.setToolTipText(path);
	}

	@Override
	protected boolean connect(LoginPresentationModel model) {
		return model.connect(newConnectionText.getText());
	}

}

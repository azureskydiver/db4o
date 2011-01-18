/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.omplus.ui.dialog.login.presentation;

import org.eclipse.jface.layout.*;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import com.db4o.omplus.connection.*;
import com.db4o.omplus.datalayer.*;
import com.db4o.omplus.debug.*;
import com.db4o.omplus.ui.*;
import com.db4o.omplus.ui.dialog.login.model.*;
import com.db4o.omplus.ui.dialog.login.model.LoginPresentationModel.LocalSelectionListener;

public class LocalLoginPane extends LoginPaneBase {
	public static final String NEW_CONNECTION_TEXT_ID = LocalLoginPane.class.getName() + "$newConnectionText";
	public static final String READ_ONLY_BUTTON_ID = LocalLoginPane.class.getName() + "$readOnlyButton";

	private static final String LOCAL_OPEN_TEXT = "Open";

	private Text newConnectionText;
	private Button readOnlyButton;
	
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
		OMESWTUtil.assignWidgetId(newConnectionText, NEW_CONNECTION_TEXT_ID);
		model.addListener(new LocalSelectionListener() {
			public void localSelection(String path, boolean readOnly) {
				newConnectionText(path);
				readOnlyButton.setSelection(readOnly);
			}
		});
		
		Button browseBtn = new Button(innerComposite, SWT.PUSH);
		try {
			browseBtn.setImage(ImageUtility.getImage(OMPlusConstants.BROWSE_ICON));
		}
		catch(Exception exc) {
			// FIXME
		}
		browseBtn.setToolTipText("Browse");

		browseBtn.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				FileDialog fileChooser = new FileDialog(parent.getShell(), SWT.OPEN);
				String dbfile = fileChooser.open();
				if(dbfile != null){
					newConnectionText(dbfile);
				}
			}
		});		

		readOnlyButton = new Button(innerComposite, SWT.CHECK);
		OMESWTUtil.assignWidgetId(readOnlyButton, READ_ONLY_BUTTON_ID);
		readOnlyButton.setText("read-only");
		
		Button configButton = new Button(innerComposite, SWT.PUSH);
		configButton.setText("Config...");
		
		GridLayoutFactory.swtDefaults().numColumns(4).equalWidth(false).applyTo(innerComposite);
		GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(recentConnectionLabel);
		GridDataFactory.swtDefaults().span(3, 1).grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(recentConnectionCombo);
		GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(newConnectionLabel);
		GridDataFactory.swtDefaults().span(2, 1).grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(newConnectionText);
		GridDataFactory.swtDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(browseBtn);
		GridDataFactory.swtDefaults().span(2, 1).align(SWT.LEFT, SWT.CENTER).applyTo(readOnlyButton);
		GridDataFactory.swtDefaults().span(2, 1).align(SWT.RIGHT, SWT.CENTER).applyTo(configButton);
	}
	
	private void newConnectionText(String path) {
		newConnectionText.setText(path);
		newConnectionText.setToolTipText(path);
	}

	@Override
	protected boolean connect(LoginPresentationModel model) {
		return model.connect(newConnectionText.getText(), readOnlyButton.getSelection());
	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout (new GridLayout());
		new LocalLoginPane(shell, shell, new LoginPresentationModel(new DataStoreRecentConnectionList(new InMemoryOMEDataStore()), null, null));
		shell.setSize(400, 300);
		shell.pack();
		shell.open ();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
}

package com.db4o.omplus.ui.dialog.login;

import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

import com.db4o.omplus.*;
import com.db4o.omplus.connection.*;
import com.db4o.omplus.datalayer.*;
import com.db4o.omplus.ui.dialog.login.model.*;
import com.db4o.omplus.ui.dialog.login.model.LoginPresentationModel.*;
import com.db4o.omplus.ui.dialog.login.presentation.*;

public class LoginDialog {

	public static final String SHELL_ID = LoginDialog.class.getName() + "$shell";
	public static final String TAB_FOLDER_ID = LoginDialog.class.getName() + "$tabFolder";
	public static final String LOCAL_TAB_ID = LoginDialog.class.getName() + "$localTab";
	public static final String REMOTE_TAB_ID = LoginDialog.class.getName() + "$remoteTab";

	private static final String LOCAL_DIALOG_TITLE = "Connect to db4o database";
	private static final String REMOTE_DIALOG_TITLE = "Connect to db4o server";

	private Shell mainCompositeShell;

	private LoginPresentationModel model;

	public LoginDialog(Shell parentShell, OMEDataStore dataStore, final Connector connector) {
		mainCompositeShell = new Shell(parentShell, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		mainCompositeShell.setData(OMPlusConstants.WIDGET_NAME_KEY, SHELL_ID);
		mainCompositeShell.setText("Connection Info");
		// FIXME should be shared
		final ErrorMessageSink err = new ErrorMessageSink() {
			public void error(String msg) {
				MessageDialog.openError(mainCompositeShell, OMPlusConstants.DIALOG_BOX_TITLE, msg);
			}
			
			public void exc(Throwable exc) {
				IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, exc.getMessage(), exc);
				Activator.getDefault().getLog().log(status);
			}
		};
		model = new LoginPresentationModel(new DataStoreRecentConnectionList(dataStore), err, connector);

		createContents();
		
		try {
			mainCompositeShell.setImage(ImageUtility.getImage(OMPlusConstants.DB4O_WIND_ICON));
		}
		catch(Exception exc) {
			// FIXME
		}
		setScreenLocation(parentShell);
	}

	private void setScreenLocation(Shell parentShell) {
		Rectangle parentBounds = parentShell.getBounds();
		Point parentLocation = parentShell.getLocation();
		Rectangle dialogBounds = mainCompositeShell.getBounds();
		int xOff = (parentBounds.width - dialogBounds.width) / 2;
		int yOff = (parentBounds.height - dialogBounds.height) / 2;
		mainCompositeShell.setLocation(parentLocation.x + xOff, parentLocation.y + yOff);
	}

	public void open() {
		mainCompositeShell.open();
	}

	protected Control createContents() {
		TabFolder folder = new TabFolder(mainCompositeShell, SWT.BORDER);
		folder.setData(OMPlusConstants.WIDGET_NAME_KEY, TAB_FOLDER_ID);
		Composite localPane = new LocalLoginPane(mainCompositeShell, folder, model);
		Composite remotePane = new RemoteLoginPane(mainCompositeShell, folder, model);
		addTab(folder, "Local", LOCAL_DIALOG_TITLE, localPane, LOCAL_TAB_ID);
		addTab(folder, "Remote", REMOTE_DIALOG_TITLE, remotePane, REMOTE_TAB_ID);
		folder.pack(true);
		mainCompositeShell.pack(true);
		return mainCompositeShell;
	}
	
	private void addTab(TabFolder folder, String name, String toolTip, Composite content, String id) {
		TabItem item = new TabItem(folder, SWT.NULL);
		item.setToolTipText(toolTip);
		item.setText(name);
		item.setControl(content);
		item.setData(OMPlusConstants.WIDGET_NAME_KEY, id);
	}

}

/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.omplus.ui.actions;

import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;

import com.db4o.*;
import com.db4o.omplus.*;
import com.db4o.omplus.connection.*;
import com.db4o.omplus.ui.*;

public final class DbConnectUtil {

	// TODO progress bar for large databases?
	public static boolean connect(ConnectionParams params, Shell shell) throws DBConnectException {
		ObjectContainer db = params.connect(new BooleanUserCallbackDialog(shell));
		Activator.getDefault().getDatabaseInterface().setDB(db, params.getPath());
		enableDBMaintenanceActions(true);
		shell.setText(params.getPath());
		showPerspective();
		return true;

	}
	
	private static void showPerspective() {
		try {
			// Show the perspective always else views not arranged as needed
			PlatformUI.getWorkbench().showPerspective(OMPlusPerspective.ID, PlatformUI.getWorkbench().getActiveWorkbenchWindow());
			ViewerManager.resetAllViewsToInitialState();

		} catch (WorkbenchException exc) {
			exc.printStackTrace();
		}

	}

	// FIXME should go to application state (see ConnectToDBAction#disable...())
	private static void enableDBMaintenanceActions(boolean enabled) {
		BackupDBAction.enableAction(enabled);
		if (enabled)
			ConnectToDBAction.setStatus("Disconnect");
	}

	private DbConnectUtil() {
	}
	
}

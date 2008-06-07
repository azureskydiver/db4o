package com.db4o.omplus.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import com.db4o.omplus.connection.ConnectionStatus;
import com.db4o.omplus.datalayer.OMPlusConstants;
import com.db4o.omplus.ui.ViewerManager;
import com.db4o.omplus.ui.dialog.LoginDialog;


public class ConnectToDBAction implements IWorkbenchWindowActionDelegate {
	
	private IWorkbenchWindow window;
	
	private static IAction action;
	
	/**
	 * The constructor.
	 */
	public ConnectToDBAction() {
	}

	public void run(IAction action) {
		ConnectionStatus connStatus = new ConnectionStatus();
		boolean connectionClosed = true;
		if(connStatus.isConnected()) { // if connected to database
			connectionClosed = showMessageForConnClose(connStatus.getCurrentDB());
			if(connectionClosed){
				connStatus.closeExistingDB();
				action.setToolTipText("Connect");
				disableDBMaintenanceActions();
				closeOMEPerspective();// FIX for Close db & refresh views
				showOMEPerspective(); 
			}
		}
		else
		{ // FIX for OMJ-61
			LoginDialog myWindow = new LoginDialog(window.getShell());
			myWindow.open();
		}
	}
	private void disableDBMaintenanceActions() {
		BackupDBAction.enableAction(false);
		DefragDBAction.enableAction(false);
	}

	private boolean showMessageForConnClose(String fileName) {
		return MessageDialog.openQuestion(window.getShell(), OMPlusConstants.DIALOG_BOX_TITLE,
									"Close Existing DB Connection "+fileName+ " ?");
	}
	
	private void closeOMEPerspective(){
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//		IPerspectiveDescriptor pers = page.getPerspective();
//		if(pers.getId().equals(OMPlusConstants.OME_PERSPECTIVE_ID)) FIX for OMJ-61
			page.closePerspective(page.getPerspective(), true, true);
			ViewerManager.resetAllViewsToInitialState();
	}
	
	private void showOMEPerspective() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		try {
			workbench.showPerspective(OMPlusConstants.OME_PERSPECTIVE_ID, workbench.getActiveWorkbenchWindow());
		} catch (WorkbenchException e) {}
	}

	public static void enableAction(boolean enabled){
		action.setEnabled(enabled);
	}
	
	public static void setStatus(String status){
		action.setToolTipText(status);
	}
	
	public void selectionChanged(IAction actn, ISelection selection) {
		if(action == null)
			action = actn;
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}
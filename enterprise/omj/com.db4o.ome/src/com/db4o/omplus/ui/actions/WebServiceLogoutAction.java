package com.db4o.omplus.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.db4o.omplus.Activator;
import com.db4o.omplus.datalayer.FileDataStore;
import com.db4o.omplus.datalayer.OMPlusConstants;
import com.db4o.omplus.datalayer.webservices.PermissionValidator;
import com.db4o.omplus.datalayer.webservices.WebServiceConnector;
import com.db4o.omplus.datalayer.webservices.WebServiceManager;

public class WebServiceLogoutAction implements IWorkbenchWindowActionDelegate {
	
	private static final String LOGOUT_MESSAGE = "Are you sure you want to log out of the full functionality mode?";

	private static IAction action;
	private IWorkbenchWindow window;
	
	public void dispose() {
		// Auto-generated method stub
		
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;		
	}

	public void run(IAction action) 
	{
		if(PermissionValidator.checkIfUserHasLoggedIntoWebService())
		{
			//logged in. Now you need to log out.and delete the file
			boolean boolVal  = MessageDialog.openQuestion(null, OMPlusConstants.DIALOG_BOX_TITLE, LOGOUT_MESSAGE);
			if(boolVal)
			{
				String address = Activator.getProxyAddress();
				int port = Activator.getProxyPort();
				try{
					boolVal = WebServiceConnector.logout(address, port);
					//1. reset
					if(boolVal){
						WebServiceManager.resetInstance();
						
						//2. Delete the stored file(if any) for WebService 
//						FileDataStore fileDataStore = new FileDataStore();
						FileDataStore.deleteUserCredentials();
						
						WebServiceLoginAction.enableAction(true);
						StatusAction.setStatus(OMPlusConstants.STATUS_LOGGEDOUT);
						enableAction(false);
					}
					
				}catch(Exception ex){
					showErrorDialog(ex.getMessage());
				}
				
			
			}
		}
	}
	public static void enableAction(boolean enabled){
		action.setEnabled(enabled);
	}
	
	private void showErrorDialog(String message) {
		MessageDialog.openError(window.getShell(), OMPlusConstants.DIALOG_BOX_TITLE, message);
		
	}
	
	public void selectionChanged(IAction actn, ISelection selection) {
		if(action == null){
			action = actn;
			if(PermissionValidator.checkIfUserHasLoggedIntoWebService()){
				WebServiceLogoutAction.enableAction(true);
			}
			else {
				WebServiceLogoutAction.enableAction(false);
			}
		}
	}

}

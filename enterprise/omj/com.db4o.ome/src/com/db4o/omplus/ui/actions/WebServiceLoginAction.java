package com.db4o.omplus.ui.actions;


import java.net.UnknownHostException;

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
import com.db4o.omplus.datalayer.webservices.connection.UserWebServiceCredentials;
import com.db4o.omplus.ui.dialog.WebServiceLoginDialog;


/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class WebServiceLoginAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	
	private static IAction action;
	/**
	 * The constructor.
	 */
	public WebServiceLoginAction() {
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	@SuppressWarnings("static-access")
	public void run(IAction action) 
	{	
	/*	if(PermissionValidator.checkIfUserHasLoggedIntoWebService())
		{
			//logged in. Now you need to log out.and delete the file
			boolean b  = MessageDialog.openQuestion(null, OMPlusConstants.DIALOG_BOX_TITLE, "Are you sure you want to log out of the full functionality mode?");
			if(b)
			{
				//1. reset
				WebServiceManager.resetInstance();
				
				//2. Delete the stored file(if any) for WebService 
				FileDataStore fileDataStore = new FileDataStore();
				fileDataStore.deleteUserCredentials();
			}
		}
		else
		{*/		
			//Use the class to check if username-password stored in file
			boolean dialogShown = false;
			FileDataStore fileDataStore = new FileDataStore();
			if(fileDataStore.isUserCredentialSaved())
			{	
				try
				{
					
					@SuppressWarnings("unused")
					UserWebServiceCredentials credentials = fileDataStore.getCachedUserCredentials();
					String address = Activator.getProxyAddress();
					int port = Activator.getProxyPort();
					boolean value = false;
					try {
						value = WebServiceConnector.connectToWebService(null, credentials.getUsername(),
															credentials.getPassword(), address, port);
					}
				/*	catch (AxisFault ex) {
						String str = ex.toString();
						if(str.contains(UnknownHostException.class.getName())){
							showErrorDialog("No internet connection available.");
							return;
						}
						
					}*/catch (Exception ex) {
						String str = ex.getMessage();
						if(str != null && str.contains(UnknownHostException.class.getName())){
							showErrorDialog(str);
//							return;
						}
						value = false;
					}
					if(!value)
						dialogShown = false;
					else
					{
						dialogShown = true;
						MessageDialog.openInformation(null, OMPlusConstants.DIALOG_BOX_TITLE, "You have been logged in using your saved credentials");
						enableAction(false);
						if(PermissionValidator.checkIfUserHasPermissionForService(OMPlusConstants.WEB_SERVICE_QUERY_BUILDER))
							StatusAction.setStatus(OMPlusConstants.FULL_MODE);
						else
							StatusAction.setStatus(OMPlusConstants.REDUCED_MODE);
						WebServiceLogoutAction.enableAction(true);
					}
					
				}
				catch (Exception e) 
				{
					dialogShown = false;
					e.printStackTrace();
				}
			}
			
			//Show a new Dialog Box only if no user credentials received from file
			//or there was an error reading the file
			if(!dialogShown)
			{
				WebServiceLoginDialog dialog = new WebServiceLoginDialog(window.getShell());
				dialog.open();
			}
//		}

	}

	private void showErrorDialog(String message) {
		MessageDialog.openError(window.getShell(), OMPlusConstants.DIALOG_BOX_TITLE, message);
		
	}
	
	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction actn, ISelection selection)
	{
		if(action == null){
			action = actn;
			if(PermissionValidator.checkIfUserHasLoggedIntoWebService()){
				WebServiceLoginAction.enableAction(false);
			}
			else {
				WebServiceLoginAction.enableAction(true);
			}
				
		}
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public static void enableAction(boolean enabled) {
		if(action != null)
			action.setEnabled(enabled);
	}
}
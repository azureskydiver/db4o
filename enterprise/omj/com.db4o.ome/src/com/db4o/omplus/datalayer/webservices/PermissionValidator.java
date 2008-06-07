package com.db4o.omplus.datalayer.webservices;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import com.db4o.omplus.Activator;
import com.db4o.omplus.datalayer.FileDataStore;
import com.db4o.omplus.datalayer.OMPlusConstants;
import com.db4o.omplus.datalayer.webservices.connection.UserWebServiceCredentials;
import com.db4o.omplus.ui.actions.StatusAction;
import com.db4o.omplus.ui.actions.WebServiceLoginAction;
import com.db4o.omplus.ui.actions.WebServiceLogoutAction;
import com.db4o.omplus.ui.dialog.WebServiceLoginDialog;
import com.db4o.omplus.ws.ArrayOfString;
import com.db4o.omplus.ws.FeaturePermission;

/**
 * This class checks if the user has permissions for a particular service
 */
public class PermissionValidator
{
	private static final String LOOGED_IN_MSG = "You have been logged in using your saved credentials.";
	
	public static boolean checkIfUserHasLoggedIntoWebService()
	{
		FeaturePermission [] permissions = WebServiceManager.getInstance().getFeaturePermissions();
		//If permissions is null OR its length is 0 need to show the web service dialog
		if(permissions == null || permissions.length == 0)
		{
			return false;	
		}
		return true;// should be returned true
	}
	
	/**
	 * 
	 * @param serviceName
	 * @return
	 */
	public static boolean checkIfUserHasPermissionForService(String serviceName)
	{
		serviceName = OMPlusConstants.WEB_SERVICE_QUERY_BUILDER;
	
		FeaturePermission [] permissions = WebServiceManager.getInstance().getFeaturePermissions();
		if(permissions != null)
		{			
			//NOTE: currently since just 3 services which need permissions we are using a loop
			//If the no. of services increase move to switch case
			for(int i = 0; i < permissions.length; i++)
			{
				if(permissions[i].getName().equalsIgnoreCase(serviceName))
				{
					ArrayOfString strArray = permissions[i].getAllow();
					if(strArray != null)
					{
						String []allow = strArray.getAdd();
						if(allow != null && allow.length > 0 && allow[0].equals("Full"))
	        				return true;
					}
				}
			}
		}
		
		return false;
	 }

	public static WebServiceLoginDialog showWebServiceLoginDialog(String serviceName)
	{
		boolean dialogShown = false;
		FileDataStore fileDataStore = new FileDataStore();
		
		WebServiceLoginDialog dialog = null;
		
		//First check if cerdentials saved. if yes, take the saved credentials and login else show
		//The dialog
		if(FileDataStore.isUserCredentialSaved())
		{
			try
			{
				@SuppressWarnings("unused")				
				UserWebServiceCredentials credentials = fileDataStore.getCachedUserCredentials();
				String address = Activator.getProxyAddress();
				int port = Activator.getProxyPort();
				boolean value = false;
				try {
					value = WebServiceConnector.connectToWebService(serviceName, credentials.getUsername(),
							 								credentials.getPassword(), address, port);
				}
				catch (Exception ex) {
					String str = ex.getMessage();
					MessageDialog.openError(null, OMPlusConstants.DIALOG_BOX_TITLE, str);
					value = false;
				}
				if(!value)
					dialogShown = false;
				else
				{
					WebServiceLoginAction.enableAction(false);
					WebServiceLogoutAction.enableAction(true);
					// status shown in toolbar
					if(PermissionValidator.checkIfUserHasPermissionForService(""))
						StatusAction.setStatus(OMPlusConstants.FULL_MODE);
					else
						StatusAction.setStatus(OMPlusConstants.REDUCED_MODE);
					dialogShown = true;
					MessageDialog.openInformation(null, OMPlusConstants.DIALOG_BOX_TITLE, LOOGED_IN_MSG );
				}
			}
			catch (Exception e) 
			{
				//Any error while reading saved field show erroor
				dialogShown = false;
				e.printStackTrace();
			}
		}
		if(!dialogShown)
		{
			dialog = new WebServiceLoginDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
			dialog.open();
		}
		
		return dialog;
		
	}
	
	
}

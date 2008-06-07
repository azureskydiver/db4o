package com.db4o.omplus.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.db4o.omplus.datalayer.OMPlusConstants;
import com.db4o.omplus.datalayer.webservices.PermissionValidator;

public class StatusAction implements IWorkbenchWindowActionDelegate {
	
	private static IAction action;

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub

	}

	public void run(IAction action) {
		// TODO Auto-generated method stub

	}

	public void selectionChanged(IAction actn, ISelection selection) {
		if(action == null){
			action = actn;
			if(PermissionValidator.checkIfUserHasLoggedIntoWebService()){
				if(PermissionValidator.checkIfUserHasPermissionForService("QueryBuilder")){
					setStatus(OMPlusConstants.FULL_MODE);
				}
				else {
					setStatus(OMPlusConstants.REDUCED_MODE);
				}
			} else
				setStatus(OMPlusConstants.STATUS_LOGGEDOUT);			
		}
	}
	
	public static void setStatus(String status){
		action.setText(status);
		action.setToolTipText(status);
	}

}

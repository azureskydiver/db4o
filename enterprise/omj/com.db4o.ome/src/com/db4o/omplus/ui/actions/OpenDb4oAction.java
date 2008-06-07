package com.db4o.omplus.ui.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import com.db4o.omplus.connection.ConnectionStatus;
import com.db4o.omplus.connection.DbConnect;
import com.db4o.omplus.connection.FileConnectionParams;
import com.db4o.omplus.connection.RecentConnectionList;
import com.db4o.omplus.datalayer.OMPlusConstants;
import com.db4o.omplus.ui.OMPlusPerspective;
import com.db4o.omplus.ui.ViewerManager;

public class OpenDb4oAction implements IObjectActionDelegate {
	
	IWorkbenchPart targetPart;
	String filePath;

	public OpenDb4oAction() {
		// TODO Auto-generated constructor stub
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart)
    {
        this.targetPart = targetPart;
    }

    public IWorkbenchPart getTargetPart()
    {
        return targetPart;
    }

	public void run(IAction action) {
//		Need to add in recent Connections.
		StringBuilder str = new StringBuilder(Platform.getLocation().toString());
		System.out.print(str.append(filePath).toString());
		FileConnectionParams params = new FileConnectionParams(str.toString());
		try{
			ConnectionStatus status = new ConnectionStatus();
			if(status.isConnected()){
				boolean open = MessageDialog.openQuestion(null, OMPlusConstants.DIALOG_BOX_TITLE, 
						"Do you want to close the existing db and continue?");
				if(!open)
					return;
				status.closeExistingDB();
			}
			DbConnect connect = new DbConnect();
			if(params instanceof FileConnectionParams)
				connect.connectToFile((FileConnectionParams)params);
			RecentConnectionList list = new RecentConnectionList();
			list.addNewConnection(params);
			showPerspective();
		}/*catch(ClassCastException ex){
			String msg = ex.getMessage();
			if(msg.equals(GENERIC_OBJ))
				msg = "Couldn't open .NET database in OME eclipse plugin";
			MessageDialog.openError(null, OMPlusConstants.DIALOG_BOX_TITLE, msg);
		}*/catch(Exception ex){
			MessageDialog.openError(null, OMPlusConstants.DIALOG_BOX_TITLE, ex.getMessage());
		}
	}
	
	private void  showPerspective() 
	{
		
		try {
			//Show the perspective always else views not arranged as needed
			PlatformUI.getWorkbench().showPerspective(OMPlusPerspective.ID, PlatformUI.getWorkbench().getActiveWorkbenchWindow());
			ViewerManager.resetAllViewsToInitialState();
			
		} catch (WorkbenchException e1) {
			e1.printStackTrace();
		}
		
	}

	public void selectionChanged(IAction action, ISelection selection) {
		 IStructuredSelection sel = (IStructuredSelection)selection;
         Object obj = sel.getFirstElement();
         if(obj instanceof IFile)
        	 filePath = ((IFile)obj).getFullPath().toString();
	}

}

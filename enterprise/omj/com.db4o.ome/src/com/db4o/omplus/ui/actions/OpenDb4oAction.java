package com.db4o.omplus.ui.actions;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.ui.*;

import com.db4o.omplus.*;
import com.db4o.omplus.connection.*;
import com.db4o.omplus.datalayer.*;
import com.db4o.omplus.ui.*;

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
			if(!(params instanceof FileConnectionParams)) {
				return;
			}
			DbConnectUtil.connect(params, getTargetPart().getSite().getShell());
			RecentConnectionList list = new DataStoreRecentConnectionList(Activator.getDefault().getOMEDataStore());
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

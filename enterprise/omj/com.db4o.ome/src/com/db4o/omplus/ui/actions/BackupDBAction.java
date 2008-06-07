package com.db4o.omplus.ui.actions;

import java.io.File;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.db4o.omplus.datalayer.DbMaintenance;
import com.db4o.omplus.datalayer.OMPlusConstants;

public class BackupDBAction implements IWorkbenchWindowActionDelegate {

	private final String BACKUP_REMOTE_MESSAGE = "Backup not possible for remote connection";
	private final String BACKUP_REPLACE_MESSAGE = "Replace the existing file ?";
	private final String BACKUP_SUCCESS_MESSAGE = "Database Backup was successful";
	
	private static IAction action;
	
	public BackupDBAction(){
		
	}
	
	private IWorkbenchWindow window;
	
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void run(IAction action) {
		boolean backupComplete = true;
		DbMaintenance main = new DbMaintenance();
		String backupFile = null;
		if(main.isDBOpened()){
			if(main.isClient())
				showMessage();
			else{
				do{
					FileDialog fDialog = new FileDialog(window.getShell(), SWT.SAVE);
					backupFile = fDialog.open();
				}while( backupFile!= null && !isFileExists(backupFile));
				
				if(backupFile != null){
					try{ 
						main.backup(backupFile);
					}
					catch(Exception ex){
						backupComplete = false;
						showErrorMessageDialog(ex);
					}
					if(backupComplete)
						showInfoDialog();
				}
			}
		}
		action.addPropertyChangeListener(new IPropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent event) {
				System.out.println(event.getProperty());
				
			}
			
		});
	}

	private void showMessage() {
		MessageDialog.openInformation(window.getShell(), OMPlusConstants.DIALOG_BOX_TITLE, 
				BACKUP_REMOTE_MESSAGE);		
	}

	private boolean isFileExists(String backupFile) {
		/*if(backupFile == null)
			return false;*/
		boolean replace = true;
		if(new File(backupFile).exists()){
			replace = MessageDialog.openQuestion(window.getShell(), OMPlusConstants.DIALOG_BOX_TITLE, 
					BACKUP_REPLACE_MESSAGE);
		}
		return replace;
	}

	private void showInfoDialog() {
		MessageDialog.openInformation(window.getShell(), OMPlusConstants.DIALOG_BOX_TITLE, 
				BACKUP_SUCCESS_MESSAGE);
	}

	private void showErrorMessageDialog(Exception e) {
		MessageDialog.openError(window.getShell(), OMPlusConstants.DIALOG_BOX_TITLE
								, e.getMessage());
		
	}
	
	public static void enableAction(boolean enabled){
		action.setEnabled(enabled);
	}
	
	public void selectionChanged(IAction actn, ISelection selection) {
		if(action == null){
			action = actn;
			action.setEnabled(false);
		}
			
	}
}

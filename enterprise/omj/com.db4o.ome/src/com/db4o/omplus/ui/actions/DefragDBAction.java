package com.db4o.omplus.ui.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.progress.IProgressConstants;

import com.db4o.omplus.datalayer.DbInterfaceImpl;
import com.db4o.omplus.datalayer.DbMaintenance;
import com.db4o.omplus.datalayer.OMPlusConstants;
import com.db4o.omplus.ui.ViewerManager;


public class DefragDBAction implements IWorkbenchWindowActionDelegate {
	
		private final String MESSAGE = "Do you want to defragment this database? \n" +
				"This operation will shutdown the currently running database and reopen after the " +
				"operation completes.";
		
		private final String DEFRAG_STATUS = "Defragmenting the database";
		
		private final String DEFRAG_REMOTE = "Defrag not possible for remote connection";
		
		private final String DEFRAG_SUCCESS = "Defragment was successful";
	
		private IWorkbenchWindow window;
		
		private static IAction action;
		
		private boolean successful = true;
		
	
		public DefragDBAction() {
		}

		/**
		 * The action has been activated. The argument of the
		 * method represents the 'real' action sitting
		 * in the workbench UI.
		 * @see IWorkbenchWindowActionDelegate#run
		 */
		public void run(IAction action) {
			
			final DbMaintenance main = new DbMaintenance() ;
			if(main.isDBOpened()) {
				if(main.isClient())
					showInfoDialog(DEFRAG_REMOTE);
				else {
					boolean index = showMessageDialog();
					if(index == true) {
						closeOMEPerspective();
						ViewerManager.resetAllViewsToInitialState();
						showOMEPerspective();
						final String path = DbInterfaceImpl.getInstance().getDbPath();
						try {
//							// background job for derfag
							Job tDefrag=new Job(DEFRAG_STATUS){
								protected  IStatus run(IProgressMonitor monitor){
									try {
										monitor.beginTask("Defrag Task Monitor", 100);
										setProperty(IProgressConstants.KEEP_PROPERTY,Boolean.TRUE);
										setProperty(IProgressConstants.ACTION_PROPERTY,
																defragCompeleteAction());
										// 
//										setProperty(IProgressConstants.PROPERTY_IN_DIALOG,true);
										monitor.worked(30);
										monitor.worked(50);
										main.defrag(path);
										monitor.worked(100);
										monitor.done();
										/*return new Status(IStatus.OK, OMPlusConstants.PLUGIN_ID,
															"Defrag Completed in return", null);   */
										return Status.OK_STATUS;
									} catch(Exception e) {
										showErrorMessageDialog(e);
										return Status.CANCEL_STATUS;   
									}
								}
							};

							tDefrag.setUser(true);
							tDefrag.schedule();
							
						} catch (Exception e) {
							successful = false;
							showErrorMessageDialog(e);
						}
					}
				}
			}
		}

		protected Action defragCompeleteAction() {
			return new Action(OMPlusConstants.DIALOG_BOX_TITLE) {
				public void run() {
					if(successful)
					{
						showInfoDialog(DEFRAG_SUCCESS);
						showOMEPerspective();
					}
				}
			};
		}
		
		private boolean showMessageDialog() {
			return MessageDialog.openQuestion(window.getShell(), OMPlusConstants.DIALOG_BOX_TITLE, MESSAGE);
		}
	
		private void showInfoDialog(String message) {
			MessageDialog.openInformation(window.getShell(), OMPlusConstants.DIALOG_BOX_TITLE,
													message);
		}

		private void showErrorMessageDialog(Exception e) {
			MessageDialog.openError(window.getShell(), OMPlusConstants.DIALOG_BOX_TITLE, e.getMessage());
			
		}

		private void closeOMEPerspective(){
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IPerspectiveDescriptor pers = page.getPerspective();
			if(pers.getId() == OMPlusConstants.OME_PERSPECTIVE_ID);
				page.closePerspective(page.getPerspective(), true, true);
		}
		
		private void showOMEPerspective() {
			IWorkbench workbench = PlatformUI.getWorkbench();
			try {
				workbench.showPerspective(OMPlusConstants.OME_PERSPECTIVE_ID, workbench.getActiveWorkbenchWindow());
			} catch (WorkbenchException e) {
			}
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

		public void dispose() {
		}

		public void init(IWorkbenchWindow window) {
			this.window = window;
		}
	}


/*	To be removed
 	class My extends Thread{
		
		public void run(){
			DbMaintenance main = new DbMaintenance();
			String path = OMEData.getInstance().getDbPath();
			
			DbInterface.getInstance().close();
			
			try {
				main.defrag(path);
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
					My m = new My();
			m.start();
			try {
				m.join();
				System.out.print(" Defrag finished" );
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}*/

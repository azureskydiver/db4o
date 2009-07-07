package com.db4o.omplus;

import java.io.*;

import org.eclipse.jface.resource.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.plugin.*;
import org.osgi.framework.*;

import com.db4o.omplus.connection.*;
import com.db4o.omplus.datalayer.*;
import com.db4o.omplus.ui.*;

public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = OMPlusConstants.PLUGIN_ID;

	private final static String USR_HOME_DIR_PROPERTY = "user.home";
	private final static String OME_DATA_DB = "OMEDATA.db4o";
	
	private final static String settingsFile = new File(new File(System
				.getProperty(USR_HOME_DIR_PROPERTY)), OME_DATA_DB)
				.getAbsolutePath();


	private static Activator plugin;

	public static Activator getDefault() {
		return plugin;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	private OMEDataStore dataStore = null;
	private IDbInterface db = null;
	
	public void start(BundleContext context) throws Exception 
	{
		super.start(context);
		plugin = this;
		
		//OMEDebugItem.createDebugDatabase();
		
		Display.getDefault().asyncExec(new Runnable() 
		{
		    public void run() 
		    {
		    	PlatformUI.getWorkbench().getActiveWorkbenchWindow().
		    				getActivePage().addPartListener(new IPartListener2() 
		    				{
		    					/**
		    					 * Handle part activated
		    					 */
								public void partActivated(
										IWorkbenchPartReference partRef) 
								{
												    				
									if(partRef.getId().equals(OMPlusConstants.CLASS_VIEWER_ID))
									{
										//TODO: Leads to recursion when RunQuery btn fired. 
										//If you have dragged something to QueryBuilder and then start input its value
										//Now the QueryBuilder is activated . When you try dragging another item from Class
										//ClassViewer, it gets activated and QueryBuilder restet to start stat...which is not needed
										
										ViewerManager.classViewActivatetd();
										
										
									}
									else if(partRef.getId().equals(OMPlusConstants.QUERY_BUILDER_ID))
									{
										//System.out.println("querybuilder activated");
										//ViewerManager.queryResultsViewActivatetd();
										
									}
									else if(partRef.getId().equals(OMPlusConstants.QUERY_RESULTS_ID))
									{
										//TODO: Leads to recursion when RunQuery btn fired. QueryBuilder getting
										//updated when Query result is still being updated
										
										//ViewerManager.queryResultsViewActivatetd();
									}
									else if(partRef.getId().equals("org.eclipse.ui.browser.editor"))
									{
										/*System.out.println("Browser accesed");
										IEditorReference[] i = PlatformUI.getWorkbench().getActiveWorkbenchWindow().
												getActivePage().getEditorReferences();
										if(i.length==0)
										{
											System.out.println("No editor refrences");
										}*/
										
									}
									else
									{
										//System.out.println("NO idea what is activated...no part in OME");
									}
									
									
								}

								public void partBroughtToTop(IWorkbenchPartReference partRef) {
									//  Auto-generated method stub
									
								}

								/**
								 * Handle part closed
								 */
								public void partClosed(	IWorkbenchPartReference partRef) 
								{
									if(partRef.getId().equals(OMPlusConstants.BROWSER_EDITOR_ID))
									{
										BrowserEditorManager.closeEditorAreaIfNoEditors();										
									}
									else
									{
										//System.out.println("What part is closed????????????");
									}
								}

								public void partDeactivated(
										IWorkbenchPartReference partRef) {
								}

								public void partHidden(
										IWorkbenchPartReference partRef) {

								}

								public void partInputChanged(
										IWorkbenchPartReference partRef) 
								{

								}

								public void partOpened(
										IWorkbenchPartReference partRef) {
								}

								public void partVisible(
										IWorkbenchPartReference partRef) {

								}
		    					
		    				});
		    }
		
		});
		
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		if(dataStore != null) {
			dataStore.close();
		}
		if(db != null) {
			db.close();
		}
		ConnectionStatus status = new ConnectionStatus();
		if(status.isConnected()){
			status.closeExistingDB();
		}
		super.stop(context);
	}
	
	public OMEDataStore getOMEDataStore() {
		if(dataStore != null) {
			return dataStore;
		}
		dataStore = new OMEDataStore(settingsFile, new DatabasePathPrefixProvider());
		return dataStore;
	}
	
	public IDbInterface getDatabaseInterface() {
		if(db != null) {
			return db;
		}
		db = new DbInterfaceImpl();
		return db;
	}
	
	private static class DatabasePathPrefixProvider implements ContextPrefixProvider {
		public String currentPrefix() {
			String prefix = Activator.getDefault().getDatabaseInterface().getDbPath();
			return prefix == null ? "" : prefix;
		}
	}

}

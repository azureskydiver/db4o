package com.db4o.omplus;

import org.eclipse.jface.resource.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.plugin.*;
import org.osgi.framework.*;

import com.db4o.omplus.connection.*;
import com.db4o.omplus.datalayer.*;
import com.db4o.omplus.ui.*;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = OMPlusConstants.PLUGIN_ID;

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception 
	{
		super.start(context);
		plugin = this;
		
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
		
		});//Thread created as Inner Class ends
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		ConnectionStatus status = new ConnectionStatus();
		if(status.isConnected()){
			status.closeExistingDB();
		}
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
}

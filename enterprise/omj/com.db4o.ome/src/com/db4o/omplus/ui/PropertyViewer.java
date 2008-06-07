package com.db4o.omplus.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.part.ViewPart;


import com.db4o.omplus.datalayer.OMPlusConstants;
import com.db4o.omplus.datalayer.propertyViewer.ConfigureIndex;
import com.db4o.omplus.datalayer.propertyViewer.PropertiesManager;
import com.db4o.omplus.datalayer.propertyViewer.PropertyViewerConstants;
import com.db4o.omplus.datalayer.propertyViewer.classProperties.ClassProperties;
import com.db4o.omplus.datalayer.propertyViewer.dbProperties.DBProperties;
import com.db4o.omplus.datalayer.propertyViewer.objectProperties.ObjectProperties;
import com.db4o.omplus.ui.customisedcontrols.propertyViewer.ClassPropertiesEditor;
import com.db4o.omplus.ui.model.propertyViewer.ClassPropertyContentProvider;
import com.db4o.omplus.ui.model.propertyViewer.ClassPropertyLabelProvider;
import com.db4o.omplus.ui.model.propertyViewer.DBPropertyContentProvider;
import com.db4o.omplus.ui.model.propertyViewer.DBPropertyLabelProvider;
import com.db4o.omplus.ui.model.propertyViewer.ObjectPropertyContentProvider;
import com.db4o.omplus.ui.model.propertyViewer.ObjectPropertyLabelProvider;;

public class PropertyViewer extends ViewPart 
{
	
	private Composite parentComposite;
	private Composite childComposite = null;
	private CTabFolder mainTab;

	private int DB4O_TAB_INDEX = 0;
	private String DB4O_TAB_TITLE = "Database properties";
	private int CLASS_TAB_INDEX = 1;
	private String CLASS_TAB_TITLE = "Class properties";
	private int OBJECT_TAB_INDEX = 2;
	private String OBJECT_TAB_TITLE = "Object properties";
	
	
	private DBProperties dbProperties;
	private Composite dbComposite;
	
	
	private ClassProperties classProperties;
	//private Composite classComposite;
	private Composite mainClassComposite;
	
	private ObjectProperties objectProperties;
	private Composite objectComposite;
	 
	

	@Override
	public void createPartControl(Composite parent)
	{
		parentComposite = parent;
		childComposite = new Composite(parentComposite,SWT.NONE);
		childComposite.setLayout(new FillLayout());
		
		mainTab = new CTabFolder(childComposite,SWT.TOP|SWT.BORDER);
	
			initializeProviders();
			addChildComponents();

	}
	
	/**
	 * Update view when new DB loaded
	 */
	public void newDBUpdate()
	{
		classProperties = null;
		objectProperties = null;
		dbProperties = PropertiesManager.getInstance().getDBProperties();	
		
		
		//dispose all child tabs..reverse order necessary else indexes modified and tabs not deleted
		for(int i = mainTab.getItemCount()-1; i >= 0; i--)
		{	
			mainTab.getItem(i).dispose();
		}
		
		
/*		ObjectContainer oc = DbInterface.getInstance().getDB();
		if(oc != null){*/
			initializeProviders();
			addChildComponents();
//		}
		
	}

	/**
	 * Initialize the providers for various tabs
	 */
	private void initializeProviders()
	{
		dbProperties = PropertiesManager.getInstance().getDBProperties();
		classProperties = null;
		objectProperties = null;
	}
	private void addChildComponents() 
	{
		
		addDB4OPropertiesTab();
		
		if(classProperties!=null)
			addClasspropertiesTab();
		
		if(objectProperties!=null)
			addObjectPropertiesTab();		
	}
	
	private void removeAllComponentsFromClassPropertiesTab()
	{
		if(mainClassComposite!=null)
		{
			for (Control control : mainClassComposite.getChildren()) 
			{
				control.dispose();
			}
			/*CTabItem item = mainTab.getItem(CLASS_TAB_INDEX);
			if(item!=null)
				item.dispose();*/
			for(int i = 0; i< mainTab.getItemCount(); i++)
			{
				CTabItem item = mainTab.getItem(i);
				if(item.getText().equals(CLASS_TAB_TITLE))
					item.dispose();
			}
		}
	}
	

	/**
	 * Reset the class properties tab
	 * @param classname
	 */
	private void resetClassPropertiesTab(String classname)
	{	
		removeAllComponentsFromClassPropertiesTab();
		if(classname != null) {
			classProperties = PropertiesManager.getInstance().getClassProperties(classname);
			addClasspropertiesTab();
			mainTab.setSelection(CLASS_TAB_INDEX);
		}
	}
	
	private void removeAllComponentsFromObjectPropertiesTab()
	{
		if(objectComposite!=null)
		{
			for (Control control : objectComposite.getChildren()) 
			{
				control.dispose();
			}
			
			/*CTabItem item = mainTab.getItem(OBJECT_TAB_INDEX);
			if(item!=null)
				item.dispose();*/
			for(int i = 0; i< mainTab.getItemCount(); i++)
			{
				CTabItem item = mainTab.getItem(i);
				if(item.getText().equals(OBJECT_TAB_TITLE))
					item.dispose();
			}
		}
	}
	
	/**
	 * Reste the object properties tab
	 * @param resultObject
	 */
	private void resetObjectPropertiesTab(Object resultObject)
	{
		removeAllComponentsFromObjectPropertiesTab();
		objectProperties = PropertiesManager.getInstance().getObjectProperties(resultObject);
		addObjectPropertiesTab();
		mainTab.setSelection(OBJECT_TAB_INDEX);
	}

	

	
	private void addDB4OPropertiesTab() 
	{
		CTabItem db4oTabItem  = new CTabItem(mainTab, SWT.NONE,DB4O_TAB_INDEX);
		db4oTabItem.setText(DB4O_TAB_TITLE);
				
		dbComposite = new Composite(mainTab,SWT.NONE);
		//Note: DB4Oproperties just has one row in table .so keep the Layout as form and 
		dbComposite.setLayout(new FormLayout());
		
		Table table = new Table(dbComposite,SWT.V_SCROLL|SWT.H_SCROLL|SWT.BORDER);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableColumn column = null;
		
		for(int i = 0; i<PropertyViewerConstants.DB_PROPERTY_COLUMN_ARRAY.length; i++)
		{
			column = new TableColumn(table, SWT.CENTER);		
			column.setText(PropertyViewerConstants.DB_PROPERTY_COLUMN_ARRAY[i]);
			//column.setWidth(100);
			column.setWidth(PropertyViewerConstants.DB_PROPERTY_COLUMN_WIDTHS[i]);
		}
		
		TableViewer tableViewer = new TableViewer(table);
		
		tableViewer.setContentProvider(new DBPropertyContentProvider());
		tableViewer.setLabelProvider(new DBPropertyLabelProvider());
		tableViewer.setInput(dbProperties);
		
		db4oTabItem.setControl(dbComposite);		
		mainTab.setSelection(0);
		
		//Set Layout
		FormData data = new FormData();
		data.top = new FormAttachment(3,3);
		data.left = new FormAttachment(3,3);		
		table.setLayoutData(data);
		dbComposite.layout(true);		
	}
	

	private void addClasspropertiesTab() 
	{
		//NOTE: Future this will have editing too		
		CTabItem tabItem  = new CTabItem(mainTab, SWT.NONE,CLASS_TAB_INDEX);
		tabItem.setText(CLASS_TAB_TITLE);
		
		mainClassComposite = new Composite(mainTab,SWT.NONE);
		mainClassComposite.setLayout(new FillLayout());
		
		ScrolledComposite scrollComposite = new ScrolledComposite(mainClassComposite,SWT.H_SCROLL|SWT.V_SCROLL);
		Composite classComposite = new Composite(scrollComposite,SWT.NONE);
		classComposite.setLayout(new FormLayout());
		
		scrollComposite.setExpandHorizontal(true);
		scrollComposite.setExpandVertical(true);
		scrollComposite.setContent(classComposite);	
		
		
		Label label = new Label(classComposite, SWT.NONE);
		label.setText(" No. of Objects : "+ classProperties.getNumberOfObjects());
		
		Button saveBtn = new Button(classComposite, SWT.NONE);
		saveBtn.setText(" Save Index ");
//		saveBtn.setEnabled(false);
		
		Table table = new Table(classComposite,SWT.V_SCROLL|SWT.H_SCROLL|SWT.BORDER|SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		
		/*TableColumn column = null;		
		for(int i = 0; i<PropertyViewerConstants.CLASS_PROPERTY_COLUMN_ARRAY.length; i++)
		{
			column = new TableColumn(table, SWT.CENTER);		
			column.setText(PropertyViewerConstants.CLASS_PROPERTY_COLUMN_ARRAY[i]);
			//column.setWidth(100);
			column.setWidth(PropertyViewerConstants.CLASS_PROPERTY_COLUMN_WIDTHS[i]);
			
		}*/
		
		//To allow index editing
		TableViewer tableViewer = new TableViewer(table);
		
		//To allow edition HAVE to set this
		tableViewer.setColumnProperties(PropertyViewerConstants.CLASS_PROPERTY_COLUMN_ARRAY);
		
		
		//Field
		TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setWidth(PropertyViewerConstants.CLASS_PROPERTY_COLUMN_WIDTHS[0]);
		column.getColumn().setText(PropertyViewerConstants.CLASS_PROPERTY_COLUMN_ARRAY[0]);
		//column.setEditingSupport(new ClassPropertiesEditor(tableViewer));
		
		//Datatype
		column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setWidth(PropertyViewerConstants.CLASS_PROPERTY_COLUMN_WIDTHS[1]);
		column.getColumn().setText(PropertyViewerConstants.CLASS_PROPERTY_COLUMN_ARRAY[1]);
		//column.setEditingSupport(new ClassPropertiesEditor(tableViewer));
		
		
		//Indexed
		column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setWidth(PropertyViewerConstants.CLASS_PROPERTY_COLUMN_WIDTHS[2]);
		column.getColumn().setText(PropertyViewerConstants.CLASS_PROPERTY_COLUMN_ARRAY[2]);
		column.setEditingSupport(new ClassPropertiesEditor(tableViewer));
		
		//Access Modifier
		column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setWidth(PropertyViewerConstants.CLASS_PROPERTY_COLUMN_WIDTHS[3]);
		column.getColumn().setText(PropertyViewerConstants.CLASS_PROPERTY_COLUMN_ARRAY[3]);
		//column.setEditingSupport(new ClassPropertiesEditor(tableViewer));
		
		
		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(tableViewer) 
		{
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) 
			{
				return event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION;			
			}
		};
		
		TableViewerEditor.create(tableViewer,actSupport, ColumnViewerEditor.DEFAULT);
				
		
		tableViewer.setContentProvider(new ClassPropertyContentProvider());
		tableViewer.setLabelProvider(new ClassPropertyLabelProvider());
		tableViewer.setInput(classProperties);
		
		tabItem.setControl(mainClassComposite);		
		
		//Set Layout for Table and Label
		
		FormData data = new FormData();
		data.top = new FormAttachment(0,8);
		data.left = new FormAttachment(0,5);
		label.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(0,5);
//		data.left = new FormAttachment(label,0);
		data.right = new FormAttachment(97, 0);
		saveBtn.setLayoutData(data);
		
		if(!ConfigureIndex.isLocal())
			saveBtn.setEnabled(false);
		
		data = new FormData();
		data.top = new FormAttachment(label,8);
		data.left = new FormAttachment(0, 5);
		data.right = new FormAttachment(100, -5);
		table.setLayoutData(data);
		classComposite.layout(true);
		
		scrollComposite.setMinSize(childComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scrollComposite.layout(true);
		
		addListenerForSaveBtn(tableViewer, saveBtn);
	}

	
	private void addListenerForSaveBtn(TableViewer tableViewer, Button saveBtn) {
		final Table table = tableViewer.getTable();
		saveBtn.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				if(classProperties == null)
					return;
				ConfigureIndex indexing = new ConfigureIndex();
//				if(indexing.isLocal()){
					boolean save = showMessage(table.getShell());
					if(save == true) {
						closeOMEPerspective();
						indexing.createIndex(classProperties);
						showOMEPerspective();
					}
//				}
			}
			
		});
		
	}
	
	private void showOMEPerspective() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		try {
			workbench.showPerspective(OMPlusConstants.OME_PERSPECTIVE_ID, workbench.getActiveWorkbenchWindow());
		} catch (WorkbenchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void closeOMEPerspective() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IPerspectiveDescriptor pers = page.getPerspective();
		if(pers.getId() == OMPlusConstants.OME_PERSPECTIVE_ID);
			page.closePerspective(page.getPerspective(), true, true);
	}

	private boolean showMessage(Shell shell) {
		String message = "This will close the database connection and reopen. Do you want to continue?";
		return MessageDialog.openQuestion(shell, OMPlusConstants.DIALOG_BOX_TITLE, message);
	}

	private void addObjectPropertiesTab() 
	{
		CTabItem tabItem  = new CTabItem(mainTab, SWT.NONE,OBJECT_TAB_INDEX);
		tabItem.setText(OBJECT_TAB_TITLE);		
		
		objectComposite = new Composite(mainTab,SWT.NONE);
		//Note: Objectproperties just has one row in table .so keep the Layout as form and set teh size
		objectComposite.setLayout(new FormLayout());
		//objectComposite.setLayout(new FillLayout());
		
		
		Table table = new Table(objectComposite,SWT.V_SCROLL|SWT.H_SCROLL|SWT.BORDER);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableColumn column = null;
		
		for(int i = 0; i<PropertyViewerConstants.OBJECT_PROPERTY_COLUMN_ARRAY.length; i++)
		{
			column = new TableColumn(table, SWT.CENTER);		
			column.setText(PropertyViewerConstants.OBJECT_PROPERTY_COLUMN_ARRAY[i]);
			column.setWidth(100);
		}
		
		TableViewer tableViewer = new TableViewer(table);
		
		tableViewer.setContentProvider(new ObjectPropertyContentProvider());
		tableViewer.setLabelProvider(new ObjectPropertyLabelProvider());
		tableViewer.setInput(objectProperties);
		
		tabItem.setControl(objectComposite);		
		
		//Set Layout
		FormData data = new FormData();
		data.top = new FormAttachment(3,3);
		data.left = new FormAttachment(3,3);
		table.setLayoutData(data);
		objectComposite.layout(true);
		
	}

	@Override
	public void setFocus() 
	{
		// Auto-generated method stub

	}

	

	public void updateClassProperties(String className)
	{
		resetClassPropertiesTab(className);
	}
	
	public void updateClassProperties(String className, boolean isObjectPropetiestabToBeUpdated)
	{
		resetClassPropertiesTab(className);
		if(!isObjectPropetiestabToBeUpdated)
		{
			removeAllComponentsFromObjectPropertiesTab();
		}
	}
	
	public void updateObjectproperties(Object resultObject)
	{
		resetObjectPropertiesTab(resultObject);
	}
	
	/**
	 * The ClassViewer view has been activated.
	 * Remove the Object properties tab if any
	 */
	public void classViewerActivated() 
	{
		removeAllComponentsFromObjectPropertiesTab();
		
	}
	public void classViewerActivated(String classname) 
	{
		removeAllComponentsFromObjectPropertiesTab();
		resetClassPropertiesTab(classname);
	}

}

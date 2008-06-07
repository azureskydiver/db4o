package com.db4o.omplus.ui.customisedcontrols.queryBuilder;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchActionConstants;

import com.db4o.omplus.datalayer.ImageUtility;
import com.db4o.omplus.datalayer.OMPlusConstants;
import com.db4o.omplus.datalayer.queryBuilder.QueryBuilderConstants;
import com.db4o.omplus.datalayer.queryBuilder.QueryGroup;
import com.db4o.omplus.ui.interfaces.IChildObserver;
import com.db4o.omplus.ui.interfaces.IDropValidator;
import com.db4o.omplus.ui.interfaces.IObservable;
import com.db4o.omplus.ui.listeners.queryBuilder.TableComboSelectionListener;
import com.db4o.omplus.ui.listeners.queryBuilder.TableDropAdapter;
import com.db4o.omplus.ui.model.queryBuilder.TableContentProvider;
import com.db4o.omplus.ui.model.queryBuilder.TableLabelProvider;

public class QueryGroupComposite extends Composite implements IObservable
{
	//Index should only be used while notifying parent about this object removal...nowhere else
	private int index;
	
	private IChildObserver observer;
	private IDropValidator dropValidator;
	private QueryGroup queryGroup;
	
	//UI elements
	private Label label;
	private Combo operatorCombo;
	private Button closeButton;
	private Table table;
	private TableViewer tableViewer;
	private Action deleteAction;
	
	/**
	 * Correct combination
	 * private int TABLE_HEIGHT = 50;
		private int TABLE_WIDTH = 350;
		private int COMPOSITE_HEIGHT = 50;
		private int COMPOSITE_WIDTH = 450;
	 */
	
	private int TABLE_HEIGHT = 40;
	//private int TABLE_WIDTH = 350;
	
	private int COLUMN_NAME_WIDTH = 100;
	private int COLUMN_CONSTRAINT_WIDTH = 75;
	private int COLUMN_VALUE_WIDTH = 90;
	private int COLUMN_OPERATOR_WIDTH = 75;
	
	
	/*private int COMPOSITE_HEIGHT = 50;
	private int COMPOSITE_WIDTH = 450;
	*/
	private int COMPOSITE_HEIGHT = 30;
	//private int COMPOSITE_WIDTH = 380;
	
	private int tableStyle = SWT.BORDER | SWT.H_SCROLL | 
							 SWT.FULL_SELECTION | SWT.HIDE_SELECTION | SWT.MULTI;

	//private int INCREMENT_COMPOSITE_HEIGHT = 60;
	private int INCREMENT_COMPOSITE_HEIGHT = 50;
	private int INCREMENT_TABLE_HEIGHT = 20;

	private int LABEL_WIDTH = 100;
	
	
	


	public QueryGroupComposite(Composite queryBuilderComnposite, int style,
								int idx, QueryGroup queryGroup, 
								IChildObserver observer, IDropValidator dropValidator) 
	{
		super(queryBuilderComnposite, style|SWT.SHADOW_ETCHED_IN);	
		index = idx;
		this.queryGroup = queryGroup;
		this.observer = observer;
		this.dropValidator = dropValidator;
		
		
		//setBackground(new Color(null, 255, 255, 255));
		setSize(getParentWidth(), COMPOSITE_HEIGHT);
		setLayout(new FormLayout());

		setComponents();		
		this.layout(true,true);
		//this.setFocus();
	}	
	
	private int getParentWidth()
	{
		Rectangle rectangle = this.getParent().getBounds();
		return rectangle.width;
	}
	
	/**
	 * Add child components
	 */
	private void setComponents() 
	{
		
		addLabel();
		//Combo and close button not to be added for idnex 0
		if(index!=0)
		{
			addCombo();
			addButton();
		}
		makeActions();
		addTable();
		addTableViewer();
		addContextMenu();
		setLayoutForComposite();
		setProviders();
		addDropInfo();
	}

	



	private void addLabel() 
	{
		label = new Label(this, SWT.SHADOW_IN);
		label.setText("Expression Group "+index);		
	}


	private void addCombo()
	{
		operatorCombo = new Combo(this,SWT.READ_ONLY|SWT.DROP_DOWN);
		operatorCombo.setItems(QueryBuilderConstants.OPERATOR_ARRAY);
		
		//Refreshing the QueryBuilder, set teh operator combo according to whats comes from the query group
		if(queryGroup.getGroupOperator()!= null && queryGroup.getGroupOperator().trim().length()>0)
		{
			for(int i = 0; i < QueryBuilderConstants.OPERATOR_ARRAY.length; i++ )
			{
				if(queryGroup.getGroupOperator().equals(QueryBuilderConstants.OPERATOR_ARRAY[i]))
				{
					operatorCombo.select(i);
				}
			}
		}
		else //When a new query group is being formed initalize the combo and Querygroup to 0th position 
		{	
			operatorCombo.select(0);
			queryGroup.setGroupOperator(QueryBuilderConstants.OPERATOR_ARRAY[operatorCombo.getSelectionIndex()]);
		}
		
		TableComboSelectionListener listener = new TableComboSelectionListener(index,operatorCombo,queryGroup);
		operatorCombo.addSelectionListener(listener);
	
		
		
	}
	
	private void addButton() 
	{
		closeButton = new Button(this,SWT.PUSH);
		closeButton.setToolTipText("Delete");
		//closeButton.setText("Close Group");
		closeButton.setImage(ImageUtility.getImage(OMPlusConstants.CLOSE_GROUP_ICON));
		closeButton.addListener(SWT.MouseUp,new Listener()
		{

			public void handleEvent(Event event) 
			{
				//have to call parents close
				observer.close(index);
			}
			
		});
		
	}
	
	private void addTable() 
	{
		table = new Table(this, tableStyle);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);		
	}

	private void addTableViewer()
	{
		tableViewer = new TableViewer(table);
		tableViewer.setUseHashlookup(true);
		
		
		//To allow edition HAVE to set this
		tableViewer.setColumnProperties(QueryBuilderConstants.columnNames);
		
		TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setWidth(COLUMN_NAME_WIDTH);
		column.getColumn().setText(QueryBuilderConstants.columnNames[0]);

		
		column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setWidth(COLUMN_CONSTRAINT_WIDTH);
		column.getColumn().setText(QueryBuilderConstants.columnNames[1]);
		column.setEditingSupport(new QueryBuilderComboEditor(tableViewer,queryGroup,
																QueryBuilderConstants.CONDITION));
		
		
		column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setWidth(COLUMN_VALUE_WIDTH);
		column.getColumn().setText(QueryBuilderConstants.columnNames[2]);
//		column.setEditingSupport(new QueryBuilderTextEditor(tableViewer, queryGroup, 
//															QueryBuilderConstants.VALUE));
		column.setEditingSupport(new QueryBuilderValueEditor(tableViewer, queryGroup));
		
		
		column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setWidth(COLUMN_OPERATOR_WIDTH);
		column.getColumn().setText(QueryBuilderConstants.columnNames[3]);
		column.setEditingSupport(new QueryBuilderComboEditor(tableViewer,queryGroup,
															QueryBuilderConstants.OPERATOR));
		
		
		//ADDED the editing activation startegy
		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(tableViewer) 
		{
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) 
			{
				return event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION;
				//|| event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED 
				//					  && event.keyCode == SWT.CR;
			}
		};
		TableViewerEditor.create(tableViewer, actSupport, ColumnViewerEditor.DEFAULT);		

		
	}
	
	private void setLayoutForComposite() 
	{
		
		//For the Label 
		FormData labelData = new FormData();
		labelData.top = new FormAttachment(1,1);
		labelData.left = new FormAttachment(1,1);
		labelData.width = LABEL_WIDTH;
		label.setLayoutData(labelData);	
		
		if(index == 0)
		{
			FormData tableFormData = new FormData();
			tableFormData.top = new FormAttachment(label,5);
			tableFormData.left = new FormAttachment(1,1);
			tableFormData.right = new FormAttachment(100,-5);
			tableFormData.height = TABLE_HEIGHT;
			//tableFormData.width = TABLE_WIDTH;
			table.setLayoutData(tableFormData);	
		}
		else
		{
			FormData comboData = new FormData();
			comboData.top = new FormAttachment(1,1);
			//comboData.left = new FormAttachment(label,165);
			comboData.right = new FormAttachment(closeButton, -5);
			comboData.width = 25;
			operatorCombo.setLayoutData(comboData);
			
			FormData closeBtnData = new FormData();
			closeBtnData.top = new FormAttachment(1,1);
			//closeBtnData.left = new FormAttachment(operatorCombo,15);
			closeBtnData.right = new FormAttachment(100,-5);
			//closeBtnData.width = 70;
			closeBtnData.height = 20;
			closeButton.setLayoutData(closeBtnData);
			
			
			
			FormData tableFormData = new FormData();
			tableFormData.top = new FormAttachment(operatorCombo,5);
			tableFormData.left = new FormAttachment(1,1);
			tableFormData.right = new FormAttachment(100,-5);
			tableFormData.height = TABLE_HEIGHT;
			//tableFormData.width = TABLE_WIDTH;
			table.setLayoutData(tableFormData);	
		}
				
		setSize();
		
	}
	
	private void setSize() 
	{
		int cHeight = operatorCombo!=null ? operatorCombo.getClientArea().height: 0;
		int tHeight ;
		if(table.getItemCount()==0)
		{
			tHeight = table.getItemHeight()+table.getHeaderHeight();
		}
		else
			tHeight = table.getItemHeight()* table.getItemCount() +table.getHeaderHeight();
		
		COMPOSITE_HEIGHT = cHeight + 5 + tHeight + INCREMENT_COMPOSITE_HEIGHT;
		setSize(getParentWidth(), COMPOSITE_HEIGHT);
		
	}
	
	
	

	private void makeActions() 
	{
		deleteAction = new Action() 
		{
			public void run() 
			{
				//showMessage("Delete Action executed for "+index);
				int indices [] = table.getSelectionIndices();
				queryGroup.deleteClause(indices);
				try 
				{
					table.remove(indices);
					resetTableHeight();
				} 
				catch (RuntimeException e) 
				{
					e.printStackTrace();
				}
				
			}
		};
		deleteAction.setText("Delete");
		deleteAction.setToolTipText("Delete Action");
//		deleteAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
//			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
	}
	
	
	private void addContextMenu() 
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() 
		{
			public void menuAboutToShow(IMenuManager manager) 
			{
				fillContextMenu(manager,index);
			}
		});
		Menu menu = menuMgr.createContextMenu(tableViewer.getControl());
		tableViewer.getControl().setMenu(menu);
		//getSite().registerContextMenu(menuMgr, tableViewer);
	}
	
	private void fillContextMenu(IMenuManager manager, int index)
	{
		manager.add(deleteAction);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}	
	

	private void setProviders() 
	{
		tableViewer.setContentProvider(new TableContentProvider(queryGroup,index));
		tableViewer.setLabelProvider(new TableLabelProvider(queryGroup,index));
		tableViewer.setInput(queryGroup);
		
	}

	private void addDropInfo() 
	{
		int ops = DND.DROP_COPY | DND.DROP_MOVE;
				
		Transfer[] transfers = new Transfer[] { TextTransfer.getInstance()};
		TableDropAdapter adapter = new TableDropAdapter(index,queryGroup,tableViewer,this,dropValidator);
		tableViewer.addDropSupport(ops, transfers, adapter);			
	}
	
	public void resetTableHeight()
	{
		int tHeight ;
		if(table.getItemCount()==0)
		{
			tHeight = table.getItemHeight()+table.getHeaderHeight();
		}
		else
			tHeight = table.getItemHeight()* table.getItemCount() +table.getHeaderHeight();
		
		TABLE_HEIGHT = tHeight + INCREMENT_TABLE_HEIGHT;
		setLayoutForComposite();
		refresh();
		observer.resized(index);
	}
	
	private void refresh()
	{
		this.layout(true, true);
	}
	
	public int getHeight()
	{
		return COMPOSITE_HEIGHT;
	}
	
	public int getWidth()
	{
		return getParentWidth();
	}
	/**
	 * Change index associated with this composite.
	 * Needed when A child group is deleted 
	 * @param i
	 */
	
	public void modifyIndex(int i) 
	{
		index = i;
		label.setText("Expression Group "+index);		
	}	
	
	
	public String getQueryGroupClass()
	{
		String clsName = null;
		if(table.getItemCount() == 0)
			return null;
		clsName = table.getItem(0).getText(0);
		if(clsName != null){
			return clsName.split(OMPlusConstants.REGEX)[0];
		}
		return clsName;
	}
	
}

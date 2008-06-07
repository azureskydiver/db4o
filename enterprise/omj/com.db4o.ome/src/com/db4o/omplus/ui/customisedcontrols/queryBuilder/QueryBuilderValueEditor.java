package com.db4o.omplus.ui.customisedcontrols.queryBuilder;

import java.util.Date;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTimeCellEditor;
import org.eclipse.swt.SWT;

import com.db4o.omplus.datalayer.OMPlusConstants;
import com.db4o.omplus.datalayer.ReflectHelper;
import com.db4o.omplus.datalayer.queryBuilder.QueryBuilderConstants;
import com.db4o.omplus.datalayer.queryBuilder.QueryClause;
import com.db4o.omplus.datalayer.queryBuilder.QueryGroup;

public class QueryBuilderValueEditor extends EditingSupport 
{
	/**
	 * Text editor
	 */	
	private CellEditor textEditor;
	
	private CDateTimeCellEditor dateEditor;
	
	private String[] booleanArrayItems = {"True", "False"};
	
	/**
	 * ComboBox editor
	 */
	private ComboBoxCellEditor booleanComboBoxCellEditor;
	
	
	
	
	private QueryGroup queryGroup;
	private TableViewer tableViewer;
	
	
	public QueryBuilderValueEditor(TableViewer viewer, QueryGroup group) 
	{
		super(viewer);
		this.queryGroup = group;
		this.tableViewer = viewer;
		
		
		booleanComboBoxCellEditor = new ComboBoxCellEditor(viewer.getTable(), booleanArrayItems,SWT.READ_ONLY);		
		textEditor = new TextCellEditor(viewer.getTable());
		
	}

	protected boolean canEdit(Object element) 
	{
		return true;
	}

	protected CellEditor getCellEditor(Object element) 
	{
		QueryClause query = ((QueryClause) element);
		int datatype = ReflectHelper.getFieldTypeClass(query.getField());
		
		switch(datatype)
		{
			case QueryBuilderConstants.DATATYPE_BOOLEAN:
			{
				booleanComboBoxCellEditor.setItems(booleanArrayItems);
				return booleanComboBoxCellEditor;
			}
				
			case QueryBuilderConstants.DATATYPE_DATE_TIME:
				return getDateEditor();
			
			default:
				return textEditor;				
		}
	}

	protected Object getValue(Object element) 
	{
		QueryClause query = ((QueryClause) element);
		int datatype = ReflectHelper.getFieldTypeClass(query.getField());
		
		switch(datatype)
		{
			case QueryBuilderConstants.DATATYPE_BOOLEAN:
			{
				String[] items = booleanComboBoxCellEditor.getItems(); 
								
				Object searchVal = null;
				searchVal = query.getValue();
				if(searchVal ==null || searchVal.toString().trim().length() ==0)
					return new Integer(0);
				else
				{
					int i;
					for(i = 0; i < items.length; i++ ) 
					{
				        if( items[i].equals(searchVal) ) 
				        {
				            break;
				        }
					}
					return new Integer(i);
				}				
			}	
				
			case QueryBuilderConstants.DATATYPE_DATE_TIME:
				Object val = query.getValue();
				if( val == null)
					return new Date();
				return val;
				
			default:
				return query.getValue().toString();
								
		}
	}

	protected void setValue(Object element, Object value) 
	{
		QueryClause query = ((QueryClause) element);
		int datatype = ReflectHelper.getFieldTypeClass(query.getField());
		
		switch(datatype)
		{
			case QueryBuilderConstants.DATATYPE_BOOLEAN:
				if(((Integer)value).intValue() <0)//If unecessary data typed
					return;
				query.setValue(booleanArrayItems[((Integer)value).intValue()]);	
				break;
			
			case QueryBuilderConstants.DATATYPE_NUMBER:
				if(!validateNumericFields(element, value))
					return;
				query.setValue(((String)value).trim());
			 	break;		
			 	
			case QueryBuilderConstants.DATATYPE_CHARACTER:
				if(!validateCharacter( value))
					return;
				query.setValue(((String)value).trim());
			 	break;			
				
			case QueryBuilderConstants.DATATYPE_DATE_TIME:
				//TODO:change this
//				if(!validateDate( value))
				if( !(value instanceof Date) )
					query.setValue(new Date());			
				query.setValue(value);
			 	break;				
			default:
				query.setValue(((String)value).trim());
		 	break;	
		}
		
		int objectIndex = queryGroup.getQueryList().indexOf(query);
		queryGroup.updateData(query,objectIndex);
		//Important else display not updated
		tableViewer.refresh();
		
	}

	private CellEditor getDateEditor(){
		if(dateEditor == null)
			dateEditor = new CDateTimeCellEditor( tableViewer.getTable(), 
					CDT.DROP_DOWN | CDT.DATE_MEDIUM | CDT.TIME_MEDIUM );
		return dateEditor;
	}
	
/*	private boolean validateDate(Object value)
	{
		//If empty string dont throw any error message allow this
		if(value.toString().trim().length()<=0)
			return true; 
		
		SimpleDateFormat sdf = new SimpleDateFormat(OMPlusConstants.DATE_FORMAT);
		
		try 
		{
			sdf.parse(value.toString());
		}
		catch (ParseException e) 
		{
			showMessage("Invalid Date. Date should be in the format dd/mm/yyyy");
			return false;
		}
		
		//cHECK FOR VALID DATES
		if (value.toString().matches(OMPlusConstants.DATE_REG_EX) ) 
		{
			return true;
		}
		else
			return false;
		
	}*/

	/**
	 * This function converts the newly enterd value into corr Wrapper Datatype object 
	 * and shows an error if it is not of invalid format
	 * @param element
	 * @param value
	 * @return
	 */
	
	@SuppressWarnings("unused")
	private boolean validateNumericFields(Object element, Object value)
	{
		//If empty string dont throw any error message allow this
		if(value.toString().trim().length()<=0)
			return true; 
		
		QueryClause query = ((QueryClause) element);
		int datatype = ReflectHelper.getFieldTypeClass(query.getField());	
		
		int numericDatatype = ReflectHelper.getNumberType(query.getField());
		switch (numericDatatype)
		{
			case QueryBuilderConstants.DATATYPE_BYTE:
				try
				{
					Byte byte1 = new Byte(value.toString());
				}
				catch(NumberFormatException e)
				{
					showMessage("Invalid byte value");
					return false;
				}
				break;
	
			case QueryBuilderConstants.DATATYPE_SHORT:
				try
				{
					Short byte1 = new Short(value.toString());
				}
				catch(NumberFormatException e)
				{
					showMessage("Invalid short value");
					return false;
				}
				break;
			case QueryBuilderConstants.DATATYPE_INT:
				try
				{
					Integer byte1 = new Integer(value.toString());
				}
				catch(NumberFormatException e)
				{
					showMessage("Invalid integer value");
					return false;
				}
				break;
			case QueryBuilderConstants.DATATYPE_LONG:
				try
				{
					Long byte1 = new Long(value.toString());
				}
				catch(NumberFormatException e)
				{
					showMessage("Invalid long value");
					return false;
				}
				break;
			case QueryBuilderConstants.DATATYPE_DOUBLE:
				try
				{
					Double byte1 = new Double(value.toString());
				}
				catch(NumberFormatException e)
				{
					showMessage("Invalid double value");
					return false;
				}
			case QueryBuilderConstants.DATATYPE_FLOAT:	
				try
				{
					Float byte1 = new Float(value.toString());
				}
				catch(NumberFormatException e)
				{
					showMessage("Invalid float value");
					return false;
				}						
		}	
		return true;
	}
	
	/**
	 * Validate character values
	 * @param value
	 * @return
	 */
	private boolean validateCharacter(Object value)
	{
		
		if (value.toString().length()==1)
		{
			return true;
		}
		else
		{
			//Bug:21870 fix
			showMessage("Invalid character value");
			return false;
		}			
	}
	
	/**
	 * Displays error message on screen
	 * @param msg
	 */
	private void showMessage(String msg)
	{
		MessageDialog.openError(null, OMPlusConstants.DIALOG_BOX_TITLE, msg);
	}
	
}
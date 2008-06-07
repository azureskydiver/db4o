package com.db4o.omplus.ui.model.queryResults;

import java.util.Date;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTimeCellEditor;
import org.eclipse.swt.SWT;

import com.db4o.omplus.datalayer.Converter;
import com.db4o.omplus.datalayer.OMPlusConstants;
import com.db4o.omplus.datalayer.ReflectHelper;
import com.db4o.omplus.datalayer.queryBuilder.QueryBuilderConstants;
import com.db4o.omplus.datalayer.queryresult.ObjectTreeNode;
import com.db4o.omplus.datalayer.queryresult.QueryResultRow;
import com.db4o.omplus.ui.customisedcontrols.queryResult.ObjectViewer;
import com.db4o.omplus.ui.customisedcontrols.queryResult.ObjectViewerTab;
import com.db4o.omplus.ui.interfaces.IChildModifier;


public class ObjectTreeEditor extends  EditingSupport
{	
	private final String BOOL = "boolean"; 
	private String[] BOOL_VALUES = {Boolean.TRUE.toString(), Boolean.FALSE.toString()};
	
	private TreeViewer treeViewer;
	private TextCellEditor textCellEditor;
	private ComboBoxCellEditor comboCellEditor;
	
	private CDateTimeCellEditor dateCellEditor;
	private ObjectViewer objectViewer;
	private ObjectViewerTab tabItem;
	private IChildModifier childModifier;
	private String[] fieldNames;
	private QueryResultRow row;
	
	//TODO: check if this can be obtained from some other place rather than being passed in constructor
	@SuppressWarnings("unused")
	private String classname;

	public ObjectTreeEditor(ObjectViewer folder,ObjectViewerTab tab, TreeViewer tv, 
							TextCellEditor tc, String classname, String[] fieldNames, 
							QueryResultRow row, IChildModifier childModifier)
	{
		super(tv);
		treeViewer = tv;
		textCellEditor = tc;
		objectViewer = folder;
		tabItem = tab;
		this.fieldNames = fieldNames;
		this.classname = classname;
		this.row = row;
		this.childModifier = childModifier;
	}
	
	private CellEditor getBooleanEditor(){
		if(comboCellEditor == null)
			comboCellEditor = new ComboBoxCellEditor(treeViewer.getTree(), BOOL_VALUES, SWT.READ_ONLY);
		return comboCellEditor;
	}
	
	private CellEditor getDateEditor(){
		if(dateCellEditor == null)
			dateCellEditor = new CDateTimeCellEditor(treeViewer.getTree(), 
					CDT.DROP_DOWN | CDT.DATE_MEDIUM | CDT.TIME_MEDIUM);
		return dateCellEditor;
	}

	protected boolean canEdit(Object element) 
	{
		if(((ObjectTreeNode)element).isPrimitive())
			return true;
		else
			return false;
	}

	protected CellEditor getCellEditor(Object element)
	{
		String nodeType = ((ObjectTreeNode)element).getType();
		if( isBooleanDataType( nodeType ) ){
			return getBooleanEditor();
		}else if( isDate( nodeType ) ){
			return getDateEditor();
		}
		return textCellEditor;
	}

	private boolean isBooleanDataType(String type) {
		if(type.equals(Boolean.class.getName()) || type.equals(BOOL))
			return true;
		return false;
	}
	
	private boolean isDate(String type) {
		if(type.equals(Date.class.getName()))
			return true;
		return false;
	}

	protected Object getValue(Object element) 
	{
		ObjectTreeNode node = (ObjectTreeNode)element;
		Object value = node.getValue();
		String type = node.getType();
		if( isBooleanDataType(type) ){
			return getIndex(value);
		}else if( isDate( type ) ){
			return value;
		}
		return value.toString();
	}

	private Object getIndex(Object value){
		if(value.equals(BOOL_VALUES[1]))
			value = 1;
		return 0;
	}
	
	protected void setValue(Object element, Object value)
	{
		ObjectTreeNode node = (ObjectTreeNode)element;
		if(isBooleanDataType(node.getType())){
			Integer i = 0;
			try {
				i = new Integer(value.toString());
			}catch(NumberFormatException ex){
			}
			value = BOOL_VALUES[i];
		}
		Object nodeVal = node.getValue();
		if(nodeVal == null || nodeVal.toString().equals(value.toString()))
		{
			return;//if Old value == new value do nothing
		}
		else
		{
			//Check if new value of correct datatype;
			int datatype = getDataType(node.getType());
			if(!validateEditing(element, value, datatype) || 
					( datatype != QueryBuilderConstants.DATATYPE_STRING && 
						value.toString().equals(OMPlusConstants.NULL_VALUE) ) )
				return;
						
			node.setOldValue(node.getValue());
			tabItem.setIsEdited(true);
			node.setValue(value);
			if(node.getType().equals(Date.class.getName()))
				objectViewer.getObjectTreeBuilder().addToModifiedList(node,
						fieldNames, row.getValues());
			else
				objectViewer.getObjectTreeBuilder().addToModifiedList(node,
						null, null);
						
			//Ask parent to enable the Save button
			childModifier.objectTreeModified(true);
		}

		treeViewer.update(element, null);

	}
	
	/**
	 * Validate if editing is to be allowed
	 * @param element
	 * @param value
	 * @return
	 */
	private boolean validateEditing(Object element, Object value, int datatype)
	{
//		ObjectTreeNode node = (ObjectTreeNode)element;
//		 For items in an array or collection the node.getName is'[index]'
//		 type. So it should check on node.getType(). To be checked
		
		switch(datatype)
		{
			case QueryBuilderConstants.DATATYPE_CHARACTER:
				return validateCharacter(value);
			case QueryBuilderConstants.DATATYPE_BOOLEAN:
				return validateBoolean(value);			
			case QueryBuilderConstants.DATATYPE_NUMBER:
				return validateNumber(element,value);					
			case QueryBuilderConstants.DATATYPE_DATE_TIME:
//				return validateDate(value);
				return (value instanceof Date);
			default://Everything else is String
				return true;		 	
		}		
	}
	
	public int getDataType(String type){
		Converter converter = new Converter();
		return ReflectHelper.getFieldType(converter.getType(type));

	}

/*	private boolean validateDate(Object value) 
	{
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
	 * Validate Numeric values acc to their correct datatype
	 * 
	 * @param element
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean validateNumber(Object element, Object value)
	{
		ObjectTreeNode node = (ObjectTreeNode)element;
		int numericDatatype = new Converter().getType(node.getType());
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
	 * Displays error message on screen
	 * @param msg
	 */
	private void showMessage(String msg)
	{
		MessageDialog.openError(null, OMPlusConstants.DIALOG_BOX_TITLE, msg);
	}

	/**
	 * Validate boolean values
	 * @param value
	 * @return
	 */
	private boolean validateBoolean(Object value)
	{
		//LOGIC: Boolean doesnt throw parseException if string is not true/false. Hence check it yourself
		if ("true".equalsIgnoreCase(value.toString()) ||
			"false".equalsIgnoreCase(value.toString()))
		{
			return true;
		}
		else
		{
			showMessage("Invalid boolean string");
			return false;
		}
			
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
			showMessage("Not a character");
			return false;
		}
			
	}
	
}
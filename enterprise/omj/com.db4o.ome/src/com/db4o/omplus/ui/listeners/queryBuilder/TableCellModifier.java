package com.db4o.omplus.ui.listeners.queryBuilder;

import java.util.Arrays;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;

import com.db4o.omplus.datalayer.OMPlusConstants;
import com.db4o.omplus.datalayer.ReflectHelper;
import com.db4o.omplus.datalayer.queryBuilder.QueryBuilderConstants;
import com.db4o.omplus.datalayer.queryBuilder.QueryClause;
import com.db4o.omplus.datalayer.queryBuilder.QueryGroup;


//TODO: Class no loner used as Customized Cell editors written acc to 3.3 API
//Delete after verification

public class TableCellModifier implements ICellModifier
{
	private QueryGroup queryGroup;
	private TableViewer tableViewer;
	
	public TableCellModifier(QueryGroup c, TableViewer t)
	{
		queryGroup = c;
		tableViewer = t;
	}
	

	public boolean canModify(Object element, String property) 
	{
		return true;
	}

	public Object getValue(Object element, String property) 
	{
		int columnIndex = Arrays.asList(QueryBuilderConstants.columnNames).indexOf(property);
		
		QueryClause query = ((QueryClause) element);
		Object result = null;
		
		switch(columnIndex)
		{
			case QueryBuilderConstants.FIELD: 
				result = query.getField();
				break;
			case QueryBuilderConstants.CONDITION:
				String stringValue = query.getCondition();
				// get the field name and ask ofr the datatype;
				int datatype = ReflectHelper.getFieldTypeClass(query.getField());
				String[] choices = null;
				switch(datatype)
				{
					case QueryBuilderConstants.DATATYPE_BOOLEAN:
						choices = QueryBuilderConstants.BOOLEAN_CONDITION_ARRAY;
						break;
					case QueryBuilderConstants.DATATYPE_CHARACTER:
						choices = QueryBuilderConstants.CHARACTER_CONDITION_ARRAY;
						break;
					case QueryBuilderConstants.DATATYPE_DATE_TIME:
						choices = QueryBuilderConstants.DATE_TIME_CONDITION_ARRAY;
						break;
					case QueryBuilderConstants.DATATYPE_NUMBER:
						choices = QueryBuilderConstants.NUMERIC_CONDITION_ARRAY;
						break;
					case QueryBuilderConstants.DATATYPE_STRING:
						choices = QueryBuilderConstants.STRING_CONDITION_ARRAY;
						break;						
				}
				int i = choices.length - 1;
				while (!stringValue.equals(choices[i]) && i > 0)
					--i;
				result = new Integer(i);						
				break;
			case QueryBuilderConstants.VALUE:
				result = query.getValue();	
				break;
			case QueryBuilderConstants.OPERATOR:
				stringValue = query.getOperator();
				choices = QueryBuilderConstants.OPERATOR_ARRAY;
				 i = choices.length - 1;
				while (!stringValue.equals(choices[i]) && i > 0)
					--i;
				result = new Integer(i);						
				break;							
			default:
				break;
		}
		return result;
	}

	public void modify(Object element, String property, Object value) 
	{
		//Implement this to change the data in the viewer else its lost after the cell loses focus
		int columnIndex	= Arrays.asList(QueryBuilderConstants.columnNames).indexOf(property);
		
		TableItem item = (TableItem) element;
		QueryClause query = (QueryClause) item.getData();
		switch(columnIndex)
		{
			case QueryBuilderConstants.FIELD: 
				query.setField(((String)value).trim());
				break;
			case QueryBuilderConstants.CONDITION://combo box
				int datatype = ReflectHelper.getFieldTypeClass(query.getField());
				switch(datatype)
				{
					case QueryBuilderConstants.DATATYPE_BOOLEAN:
						query.setCondition(QueryBuilderConstants.BOOLEAN_CONDITION_ARRAY[((Integer)value).intValue()]);
						break;
					case QueryBuilderConstants.DATATYPE_CHARACTER:
						query.setCondition(QueryBuilderConstants.CHARACTER_CONDITION_ARRAY[((Integer)value).intValue()]);
						break;
					case QueryBuilderConstants.DATATYPE_DATE_TIME:
						query.setCondition(QueryBuilderConstants.DATE_TIME_CONDITION_ARRAY[((Integer)value).intValue()]);
						break;
					case QueryBuilderConstants.DATATYPE_NUMBER:
						query.setCondition(QueryBuilderConstants.NUMERIC_CONDITION_ARRAY[((Integer)value).intValue()]);
						break;
					case QueryBuilderConstants.DATATYPE_STRING:
						int i = ((Integer)value).intValue();
						if(i > QueryBuilderConstants.STRING_CONDITION_ARRAY.length)
						{
							MessageDialog.openError(null, OMPlusConstants.DIALOG_BOX_TITLE, "Invalid index");
							return;
						}
						query.setCondition(QueryBuilderConstants.STRING_CONDITION_ARRAY[((Integer)value).intValue()]);
						break;						
				}
				
				
				
				break;
			case QueryBuilderConstants.VALUE:
				query.setValue(((String)value).trim());
				break;
			case QueryBuilderConstants.OPERATOR://combo box
				//returns the index selected from the combo
				query.setOperator(QueryBuilderConstants.OPERATOR_ARRAY[((Integer)value).intValue()]);
				break;				
			default:
				break;
		}				
		//constraintList.updateQuery(query, tableIndex);
		queryGroup.updateData(query,100);		
		tableViewer.refresh(query);//Important else display not updated	

	}

}

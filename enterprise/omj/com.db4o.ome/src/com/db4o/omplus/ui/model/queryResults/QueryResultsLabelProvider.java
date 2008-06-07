package com.db4o.omplus.ui.model.queryResults;

import java.util.Collection;
import java.util.Map;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.db4o.omplus.datalayer.ReflectHelper;
import com.db4o.omplus.datalayer.queryresult.QueryResultRow;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.generic.GenericObject;



public class QueryResultsLabelProvider extends LabelProvider implements ITableLabelProvider
{
	public Image getColumnImage(Object element, int columnIndex) {
		// Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	public String getColumnText(Object element, int columnIndex)
	{
		if (element instanceof QueryResultRow) 
		{
			if(columnIndex == 0)//Deafult columnidentifier added
			{
				QueryResultRow row = (QueryResultRow) element;
				//return "Row "+row.getId();
				return ""+row.getId();
			}
			else
			{
				//NOTE: 0th column is identifier
				QueryResultRow row = (QueryResultRow) element;
				Object obj = row.getValue(columnIndex-1);
				
				if(obj == null)
				{
//					System.out.println("Error:QueryResultsLabelProvider->getColumnText() " + columnIndex);
				}
				else
				{					
					boolean isEditable = row.getIsCellModifiable(columnIndex -1);
					if( isEditable || (obj instanceof GenericObject))
						return obj.toString();
					else{
						if(obj instanceof Collection)
							return getString( ((Collection)obj).size() );
						else if(obj instanceof Map)
							return getString( ((Map)obj).size() );
						else {
							ReflectClass clazz = ReflectHelper.getReflectClazz(obj);
							if(clazz.isArray())
								return getString( ReflectHelper.getArraySize(obj));
						}
							return (obj!=null)?obj.toString():"";
					}
				}
				
			}
		}
		return null;
	}
	
	private String getString(int size){
		StringBuilder sb = new StringBuilder();
		sb.append(size);
		sb.append(" items");
		return sb.toString();
	}
}

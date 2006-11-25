package com.db4o.objectManager.v2;

import com.spaceprogram.db4o.sql.*;
import com.spaceprogram.db4o.sql.parser.SqlParseException;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;
import com.db4o.reflect.Reflector;
import com.db4o.ObjectContainer;
import com.db4o.objectManager.v2.util.Log;
import com.db4o.objectmanager.api.helpers.ReflectHelper2;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.*;
import java.util.*;

/**
 * User: treeder
 * Date: Aug 20, 2006
 * Time: 3:29:02 PM
 */
public class QueryResultsTableModel extends AbstractTableModel implements TableModel {
	private ObjectSetWrapper results;
	private String query;
	private QueryResultsPanel queryResultsPanel;

	List topResults = new ArrayList();
	List resultWindow = new ArrayList();
	private static final int NUM_IN_TOP = 100;
	private static final int NUM_IN_WINDOW = 100;
	private int windowStartIndex = -1;
	private int windowEndIndex = -1;
	private int extraColumns = 2; // for row counter
	private static final int COL_ROW_NUMBER = 1;
	static final int COL_TREE = 0;
	private Icon treeIcon = ResourceManager.createImageIcon(ResourceManager.ICONS_16X16 + "text_tree.png", "View Object Graph");

	public QueryResultsTableModel(String query, QueryResultsPanel queryResultsPanel) throws Exception {
		this.query = query;
		this.queryResultsPanel = queryResultsPanel;
		// get first X rows right off the bat
		try {
			long startTime = System.currentTimeMillis();
			results = (ObjectSetWrapper) Sql4o.execute(queryResultsPanel.getObjectContainer(), query);
			long duration = System.currentTimeMillis() - startTime;
			queryResultsPanel.setStatusMessage("Returned " + results.size() + " results in " + duration + "ms");
			initTop(results);
		} catch (SqlParseException e) {
			queryResultsPanel.setErrorMessage("Error executing query!  " + e.getMessage());
			Log.addException(e);
			throw e;
		} catch (Sql4oException e) {
			queryResultsPanel.setErrorMessage("Error executing query!  " + e.getMessage());
			Log.addException(e);
			throw e;
		}
	}

	private void initTop(ObjectSetWrapper results) {
		for (int i = 0; i < NUM_IN_TOP && i < results.size(); i++) {
			Result result = (Result) results.get(i);
			topResults.add(result);
		}
	}

	public int getRowCount() {
		return results.size();
	}

	public int getColumnCount() {
		return results.getMetaData().getColumnCount() + extraColumns;
	}

	public Object getValueAt(int row, int column) {
		//if(row > 0) System.out.println("getting row: " + row);
		if (column == COL_TREE) return treeIcon;
		if (column == COL_ROW_NUMBER) return row;
		Result result;
		if (row < NUM_IN_TOP) {
			result = (Result) topResults.get(row);
		} else {
			int index = rowInCurrentWindow(row);
			//System.out.println("got index " + index + " for row " + row);
			if (index != -1) {
				result = (Result) resultWindow.get(index);
			} else {
				index = loadWindow(row);
				result = (Result) resultWindow.get(index);
			}
		}
		Object ret = null;
		try {
			ret = result.getObject(column - extraColumns);
			if (ret == null) return ret;
			Reflector reflector = queryResultsPanel.getObjectContainer().ext().reflector();
			ReflectClass rc = reflector.forObject(ret);
			/*if(rc.getName().contains("Collection")){
						todo: waiting for this fix: http://tracker.db4o.com/jira/browse/COR-245
						// .NET collections are not working properly
							System.out.println("class: " + rc + " is col: " + rc.isCollection() + " " + reflector.isCollection(rc));
						}*/
			if (reflector.isCollection(rc)) {
				// reflector.isCollection returns true for Maps too I guess
				if (ret instanceof Map) {
					Map m = (Map) ret;
					return new MapValue("Map: " + m.size());
				} else {
					Collection c = (Collection) ret;
					return new CollectionValue("Collection: " + c.size() + " items");
				}
			}
			if(rc.isArray()){
				Object[] arr = (Object[]) ret;
				return new CollectionValue("Array: " + arr.length + " items");
			}
		} catch (Sql4oException e) {
			queryResultsPanel.setErrorMessage("Error occurred: " + e.getMessage());
		}

		return ret;
	}

	private int loadWindow(int row) {
		// go forward and back X rows
		int ret = NUM_IN_WINDOW;
		resultWindow.clear(); // maybe don't need this
		int startIndex = row - NUM_IN_WINDOW;
		if (startIndex < NUM_IN_TOP) {
			ret = startIndex;
			startIndex = NUM_IN_TOP;
		}
		int endIndex = row + NUM_IN_WINDOW;
		if (endIndex >= results.size()) endIndex = results.size();
		//System.out.println("Loading window: " + startIndex + " to " + endIndex);
		for (int i = startIndex; i < endIndex; i++) {
			Result result = (Result) results.get(i);
			resultWindow.add(result);
		}
		windowStartIndex = startIndex;
		windowEndIndex = endIndex;
		return ret;
	}

	private int rowInCurrentWindow(int row) {
		if (row >= windowStartIndex && row < windowEndIndex) {
			return row - windowStartIndex;
		}
		return -1;
	}

	public boolean isCellEditable(int row, int col) {
		if (col < extraColumns) return false;
		Class c = getColumnClass(col);
		//System.out.println("iseditable? " + ReflectHelper2.isEditable(c) + " " + c.getName());
		return ReflectHelper2.isEditable(c);
	}

	public void setValueAt(Object value, int row, int col) {
		// System.out.println("setValue at " + row + "," + col + ": " + value);
		//if (value != null) System.out.println("value class: " + value.getClass());
		// apply to base object and then save
		Result result = (Result) results.get(row);
		Object o = result.getBaseObject(0);
		//System.out.println("base object: " + o);
		ReflectClass rc = results.getReflector().forObject(o);
		ReflectField[] rfs = ReflectHelper.getDeclaredFieldsInHeirarchy(rc);
		if (rfs.length > col - extraColumns) {
			ReflectField rf = rfs[col - extraColumns];
			rf.setAccessible();
			rf.set(o, value);
			//System.out.println("Set value on field: " + rf.getName() + " " + rf.getFieldType() + " new value: " + rf.get(o));
			queryResultsPanel.addObjectToBatch(o);
		}
		super.setValueAt(value, row, col - extraColumns);
		fireTableCellUpdated(row, col);
	}

	public String getColumnName(int column) {
		if (column == COL_TREE) return " "; // tree icons
		if (column == COL_ROW_NUMBER) return "Row";
		return results.getMetaData().getColumnName(column - extraColumns);
	}

	public Class getColumnClass(int c) {
		if (c == COL_TREE) return Icon.class;
		if (c == COL_ROW_NUMBER) return Number.class;
		for (int i = 0; i < results.size(); i++) {
			Object o = getValueAt(0, c);
			//System.out.println("class: " + o.getClass());
			if (o != null) return o.getClass();
			// todo: should i get this from the reflector?
		}
		return super.getColumnClass(c);
	}

	public ObjectContainer getObjectContainer() {
		return queryResultsPanel.getObjectContainer();
	}

	public Object getRowObject(int row) {
		Result wrapper = (Result) results.get(row);
		return wrapper.getBaseObject(0);
	}
}

package com.db4o.objectManager.v2;

import com.db4o.objectmanager.api.DatabaseInspector;
import com.db4o.ObjectContainer;
import com.db4o.Db4o;
import com.db4o.ext.StoredClass;
import com.db4o.ext.StoredField;

import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;

/**
 * User: treeder
 * Date: Sep 11, 2006
 * Time: 3:02:44 PM
 */
public class FieldInfoTableModel extends DefaultTableModel implements TableModel, TableModelListener {
	private UISession session;
	private String className;
	static String columns[] = new String[]{
			"Name",
			"Type",
			"Indexed?",
	};
	private boolean editMode;
	private boolean working;


	public FieldInfoTableModel(UISession session, String className) {
		super(columns, 0);
		this.session = session;

		this.className = className;

		// not storing these as class fields so that if reopen is called, this stuff won't be disconnected.
		StoredClass storedClass = session.getObjectContainer().ext().storedClass(className);
		StoredField[] fields = storedClass.getStoredFields();
		super.setRowCount(fields.length);
		int r = 0, c = 0;
		for (int i = 0; i < fields.length; i++) {
			StoredField field = fields[i];
			c = 0;
			setValueAt(field.getName(), r, c++);
			setValueAt(field.getStoredType(), r, c++);
			setValueAt(new Boolean(field.hasIndex()), r, c++);
			r++;
		}
		addTableModelListener(this);
	}


	public Class getColumnClass(int column) {
		if(column == 2){
			return Boolean.class;
		}
		return super.getColumnClass(column);
	}

	/**
	 * Unfortunately, none of this schema evolution stuff works in c/s mode.
	 * @param row
	 * @param column
	 * @return
	 */
	public boolean isCellEditable(int row, int column) {
		if (!working && editMode) {
			if (column == 0) {
				// name
				return true;
			} else if (column == 2) {
				// indexes
				return true;
			}
		}
		return false;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public void tableChanged(TableModelEvent e) {
		working = true; // just so it will only do one thing at a time
		int row = e.getFirstRow();
		int column = e.getColumn();
		TableModel model = (TableModel) e.getSource();
		Object aValue = model.getValueAt(row, column);
		if (column == 1) {
			if (aValue instanceof String) {
				StoredClass storedClass = session.getObjectContainer().ext().storedClass(className);
				StoredField[] fields = storedClass.getStoredFields();
				renameField(fields[row], (String) aValue);
			} else {
				System.err.println("Invalid type for renaming!!! Report bug at http://tracker.db4o.com/jira");
			}
		} else if (column == 2) {
			System.out.println("aValue: " + aValue);
			Boolean b = (Boolean) aValue;
			// for indexes, we'll have to reopen the objectcontainer and turn on the index before opening
			StoredClass storedClass = session.getObjectContainer().ext().storedClass(className);
			StoredField[] fields = storedClass.getStoredFields();
			StoredField field = fields[row];
			Db4o.configure().objectClass(storedClass.getName()).objectField(field.getName()).indexed(b.booleanValue());
			session.reopen();
		}
		working = false;
	}

	public void setValueAt(Object aValue, int row, int column) {
		super.setValueAt(aValue, row, column);
	}

	/**
	 * This will do a schema change in db4o.
	 *
	 * @param field
	 * @param newName
	 */
	private void renameField(StoredField field, String newName) {
		field.rename(newName);
		session.getObjectContainer().commit();
		// need to reopen ObjectContainer here to make it stick
		session.reopen();
	}
}

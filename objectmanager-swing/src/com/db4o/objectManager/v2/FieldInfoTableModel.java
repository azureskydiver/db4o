package com.db4o.objectManager.v2;

import com.db4o.objectmanager.api.DatabaseInspector;
import com.db4o.ObjectContainer;
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
			setValueAt(false, r, c++);
			r++;
		}
		addTableModelListener(this);
	}

	public boolean isCellEditable(int row, int column) {
		if (editMode && column == 0) {
			return true;
		}
		return false;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public void tableChanged(TableModelEvent e) {
		System.out.println("table changed");
		int row = e.getFirstRow();
		int column = e.getColumn();
		TableModel model = (TableModel) e.getSource();
		Object aValue = model.getValueAt(row, column);
		if (aValue instanceof String) {
			StoredClass storedClass = session.getObjectContainer().ext().storedClass(className);
			StoredField[] fields = storedClass.getStoredFields();
			renameField(fields[row], (String) aValue);
		} else {
			System.err.println("Invalid type for renaming!!! Report bug at http://tracker.db4o.com/jira");
		}
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
		System.out.println("Renaming field: " + field.getName() + " to " + newName);
		field.rename(newName);
		session.getObjectContainer().commit();
		// need to reopen ObjectContainer here to make it stick
		session.reopen();
	}
}

package com.db4o.objectManager.v2;

import com.db4o.objectmanager.api.DatabaseInspector;
import com.db4o.reflect.ReflectClass;
import com.db4o.ObjectContainer;
import com.db4o.ext.StoredClass;
import com.db4o.ext.StoredField;

import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableModel;
import java.util.List;

/**
 * User: treeder
 * Date: Sep 11, 2006
 * Time: 3:02:44 PM
 */
public class FieldInfoTableModel extends DefaultTableModel implements TableModel {
    private ObjectContainer objectContainer;
    private DatabaseInspector databaseInspector;
    private String className;
    static String columns[] = new String[]{
            "Name",
            "Type",
            "Indexed?",
    };
    private StoredClass storedClass;

    public FieldInfoTableModel(ObjectContainer objectContainer, DatabaseInspector databaseInspector, String className) {
        super(columns, 0);
        this.objectContainer = objectContainer;

        this.databaseInspector = databaseInspector;

        this.className = className;
        storedClass = objectContainer.ext().storedClass(className);

        StoredField[] fields = storedClass.getStoredFields();
        super.setRowCount(fields.length);
        int r=0, c=0;
        for (int i = 0; i < fields.length; i++) {
            StoredField field = fields[i];
            c=0;
            setValueAt(field.getName(),r,c++);
            setValueAt(field.getStoredType(),r,c++);
            setValueAt(field.hasIndex(),r,c++);
            r++;
        }
    }
}

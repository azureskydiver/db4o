package com.db4o.objectManager.v2;

import com.db4o.objectmanager.api.DatabaseInspector;
import com.db4o.reflect.ReflectClass;

import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableModel;
import java.util.List;

/**
 * User: treeder
 * Date: Sep 11, 2006
 * Time: 3:02:44 PM
 */
public class FieldInfoTableModel extends DefaultTableModel implements TableModel {
     private DatabaseInspector databaseInspector;
    static String columns[] = new String[]{
            "Name",
            "Type",
            "Indexed?",
    };

    public FieldInfoTableModel(DatabaseInspector databaseInspector) {
        super(columns, 0);

        this.databaseInspector = databaseInspector;
        /*List<ReflectClass> classesStored = databaseInspector.getClassesStored();
        super.setRowCount(classesStored.size());
        this.databaseInspector = databaseInspector;
        int r=0,c=0;
        for (int i = 0; i < classesStored.size(); i++) {
            ReflectClass storedClass = classesStored.get(i);
            c=0;
            setValueAt(storedClass.getName(),r,c++);
            setValueAt(databaseInspector.getNumberOfObjectsForClass(storedClass.getName()),r,c++);
            setValueAt(databaseInspector.getSpaceUsedByClass(storedClass.getName()),r,c++);
            setValueAt(databaseInspector.getSpaceUsedByClassIndexes(storedClass.getName()), r, c++);
            r++;
        }*/
    }
}

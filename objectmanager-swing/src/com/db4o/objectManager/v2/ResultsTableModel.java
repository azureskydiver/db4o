package com.db4o.objectManager.v2;

import com.spaceprogram.db4o.sql.ObjectSetWrapper;
import com.spaceprogram.db4o.sql.Result;
import com.spaceprogram.db4o.sql.ReflectHelper;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.util.List;
import java.util.Date;

/**
 * User: treeder
 * Date: Aug 20, 2006
 * Time: 3:29:02 PM
 */
public class ResultsTableModel extends AbstractTableModel implements TableModel {
    private ObjectSetWrapper results;
    private QueryResultsPanel queryResultsPanel;

    public ResultsTableModel(List<Result> results, QueryResultsPanel queryResultsPanel) {
        this.queryResultsPanel = queryResultsPanel;
        this.results = (ObjectSetWrapper) results;
    }

    public int getRowCount() {
        return results.size();
    }

    public int getColumnCount() {
        return results.getMetaData().getColumnCount();
    }

    public Object getValueAt(int row, int column) {
        Result result = (Result) results.get(row);
        //System.out.println("getting value at: " + row + "," + column + ": " + result.getBaseObject(0));
        Object ret = result.getObject(column);
        //if (ret != null)System.out.println("RET: " + ret.getClass() + " - " + ret);
        return ret;
    }

    public boolean isCellEditable(int row, int col) {
        Class c = getColumnClass(col);
        return c.isPrimitive() || String.class.isAssignableFrom(c) || Number.class.isAssignableFrom(c) || Date.class.isAssignableFrom(c);
    }

    public void setValueAt(Object value, int row, int col) {
       // System.out.println("setValue at " + row + "," + col + ": " + value);
        //if (value != null) System.out.println("value class: " + value.getClass());
        // apply to base object and then save
        Result result = (Result) results.get(row);
        Object o = result.getBaseObject(0);
        //System.out.println("base object: " + o);
        ReflectClass rc = results.getReflector().forObject(o);
        ReflectField[] rfs = ReflectHelper.getDeclaredFields(rc);
        if (rfs.length > col) {
            ReflectField rf = rfs[col];
            rf.setAccessible();
            rf.set(o, value);
            //System.out.println("Set value on field: " + rf.getName() + " " + rf.getFieldType() + " new value: " + rf.get(o));
            queryResultsPanel.addObjectToBatch(o);
        }
        super.setValueAt(value, row, col);
        fireTableCellUpdated(row, col);
    }

    public String getColumnName(int column) {
        return results.getMetaData().getColumnName(column);
    }

    public Class getColumnClass(int c) {
        for (int i = 0; i < results.size(); i++) {
            Object o = getValueAt(0, c);
            if (o != null) return o.getClass();
            // todo: can i get this from the reflector?
        }
        return super.getColumnClass(c);
    }

}

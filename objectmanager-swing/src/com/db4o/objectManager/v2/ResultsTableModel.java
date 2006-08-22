package com.db4o.objectManager.v2;

import com.spaceprogram.db4o.sql.ObjectSetWrapper;
import com.spaceprogram.db4o.sql.Result;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.util.List;

/**
 * User: treeder
 * Date: Aug 20, 2006
 * Time: 3:29:02 PM
 */
public class ResultsTableModel extends AbstractTableModel implements TableModel {
    private ObjectSetWrapper results;

    public ResultsTableModel(List<Result> results) {
        this.results = (ObjectSetWrapper) results;
    }

    public int getRowCount() {
        return results.size();
    }

    public int getColumnCount() {
        return results.getMetaData().getColumnCount();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Result result = (Result) results.get(rowIndex);
        /*Object base = result.getBaseObject(0);
        System.out.println("base: " + base);
        */
        return result.getObject(columnIndex);
    }

    public boolean isCellEditable(int row, int col) {
        return true;
    }

    public void setValueAt(Object value, int row, int col) {
        // todo: set and commit
        fireTableCellUpdated(row, col);
    }

    public String getColumnName(int column) {
        return results.getMetaData().getColumnName(column);
    }
}

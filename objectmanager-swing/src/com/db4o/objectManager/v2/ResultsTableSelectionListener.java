package com.db4o.objectManager.v2;

import com.db4o.ext.Db4oUUID;

import javax.swing.*;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Collection;

/**
 * User: treeder
 * Date: Sep 7, 2006
 * Time: 10:42:29 PM
 */
public class ResultsTableSelectionListener extends MouseAdapter {
    JTable table;
    private QueryResultsPanel queryResultsPanel;

    // It is necessary to keep the table since it is not possible
    // to determine the table from the event's source
    public ResultsTableSelectionListener(JTable table, QueryResultsPanel queryResultsPanel) {
        this.table = table;
        this.queryResultsPanel = queryResultsPanel;
    }

    public void mousePressed(MouseEvent e) {
        JTable table = (JTable) e.getSource();
        Point p = e.getPoint();
        if (e.getClickCount() == 2)
            System.out.println("table.isEditing = " + table.isEditing());
        int row = table.rowAtPoint(p);
        int col = table.columnAtPoint(p);
        Object value =  table.getValueAt(row, col);
        System.out.println(value + " class:" + value.getClass());
        ResultsTableModel model = (ResultsTableModel) table.getModel();
        /*Db4oUUID uuid = model.getObjectContainer().ext().getObjectInfo(value).getUUID();
        System.out.println("object uuid: " + uuid);
        */
        if(e.getClickCount() == 2 && value instanceof CollectionValue){
            // show collection on double click
            queryResultsPanel.showObjectTree(model.getRowObject(row));

        }
    }


}

package com.db4o.objectManager.v2;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.util.List;
import java.util.ArrayList;

/**
 * User: treeder
 * Date: Aug 26, 2006
 * Time: 11:56:52 AM
 */
public class QueryHistoryComboBoxModel extends AbstractListModel implements ComboBoxModel {

    String topItem = ("Query history...");

    Object selectedItem;
    private List queryHistory;

    public QueryHistoryComboBoxModel(List queryHistory) {
        this.queryHistory = queryHistory;
        if(this.queryHistory == null) this.queryHistory = new ArrayList();
        setSelectedItem(topItem);
    }

    public void setSelectedItem(Object anItem) {
        this.selectedItem = anItem;
    }

    public Object getSelectedItem() {
        return selectedItem;
    }

    public int getSize() {
        return queryHistory.size() + 1; // + 1 for topItem
    }

    public Object getElementAt(int index) {
        if(index > 0 && index <= queryHistory.size()) return queryHistory.get(index - 1);
        else return topItem;
    }

}

package com.db4o.objectManager.v2;

import com.db4o.objectmanager.api.prefs.PreferencesCore;
import com.db4o.objectmanager.model.Db4oConnectionSpec;

import javax.swing.*;
import java.awt.Component;
import java.awt.BorderLayout;
import java.util.List;
import java.util.ArrayList;

/**
 * User: treeder
 * Date: Aug 8, 2006
 * Time: 11:50:33 PM
 */
public class RecentConnectionList extends JPanel{
    private JList list;
    private DefaultListModel listModel;
    private static final String RECENT_CONNECTIONS = "recentConnections";


    public RecentConnectionList() {
        super(new BorderLayout());
        List<Db4oConnectionSpec> connections = getRecentConnectionSpecsFromDb();
        listModel = new DefaultListModel();
        for (int i = 0; i < connections.size(); i++) {
            Db4oConnectionSpec db4oConnectionSpec = connections.get(i);
            listModel.addElement(db4oConnectionSpec);
        }
        list = new JList(listModel);
        list.setVisibleRowCount(5);
        JScrollPane listScroller = new JScrollPane(list);
        this.add(listScroller);
    }

    private List<Db4oConnectionSpec> getRecentConnectionSpecsFromDb() {
        List<Db4oConnectionSpec> connections = (List<Db4oConnectionSpec>) PreferencesCore.getDefault().getPreference(RECENT_CONNECTIONS);
        if(connections == null) connections = new ArrayList<Db4oConnectionSpec>();        
        return connections;
    }

    public void addNewConnectionSpec(Db4oConnectionSpec connectionSpec) {
        // make sure it's not already here
        for(int i = 0; i < listModel.getSize(); i++){
            Db4oConnectionSpec spec = (Db4oConnectionSpec) listModel.get(i);
            if(spec.getPath().equals(connectionSpec.getPath())){
                return;
            }
        }
        List<Db4oConnectionSpec> connections = getRecentConnectionSpecsFromDb();
        connections.add(connectionSpec);
        PreferencesCore.getDefault().setPreference(RECENT_CONNECTIONS, connections);
        listModel.addElement(connectionSpec);
    }

    public Db4oConnectionSpec getSelected() {
        return (Db4oConnectionSpec) listModel.get(list.getSelectedIndex());
    }

    public Component getList() {
        return list;
    }
}

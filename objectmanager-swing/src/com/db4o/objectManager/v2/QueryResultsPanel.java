/*
 * Copyright (c) 2001-2006 JGoodies Karsten Lentzsch. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer. 
 *     
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *     
 *  o Neither the name of JGoodies Karsten Lentzsch nor the names of 
 *    its contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */

package com.db4o.objectManager.v2;

import com.db4o.ext.StoredClass;
import com.db4o.ext.StoredField;
import com.db4o.objectManager.v2.uif_lite.component.Factory;
import com.db4o.objectManager.v2.uif_lite.panel.SimpleInternalFrame;
import com.db4o.objectmanager.api.DatabaseInspector;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.looks.Options;
import com.spaceprogram.db4o.sql.Result;
import com.spaceprogram.db4o.sql.ReflectHelper;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.util.List;

final class QueryResultsPanel extends JPanel {
    private MainPanel mainPanel;
    private JTable resultsTable;
    private TableModel tableModel;
    private SimpleInternalFrame resultsFrame;

    public QueryResultsPanel(MainPanel mainPanel) {
        super(new BorderLayout());
        this.mainPanel = mainPanel;
        setOpaque(false);
        setBorder(Borders.DIALOG_BORDER);
        add(buildMainRightPanel());
    }



    private JComponent buildMainRightPanel() {
        Component table = buildResultsTable();
        resultsFrame = new SimpleInternalFrame("Results");
        resultsFrame.setPreferredSize(new Dimension(300, 100));
        resultsFrame.add(table);
        return resultsFrame;
    }

    private JScrollPane buildResultsTable() {
        resultsTable = new JTable();
        resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane scrollpane = new JScrollPane(resultsTable);
        return scrollpane;
    }


    /**
     * After a query executes, this will setup the table to display results.
     * @param results
     */
    public void displayResults(List<Result> results) {
        tableModel = new ResultsTableModel(results, this);
        resultsTable.setModel(tableModel);
    }

    /**
     * This method will batch up any changed objects until the user closes this panel, or clicks the Commit/Apply button
     * @param o
     */
    public void addObjectToBatch(Object o) {
        // todo: implement the batch with button
        mainPanel.getObjectContainer().set(o);
        mainPanel.getObjectContainer().commit();
    }
}
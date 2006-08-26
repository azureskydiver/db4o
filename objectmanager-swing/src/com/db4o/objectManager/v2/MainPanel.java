package com.db4o.objectManager.v2;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.objectmanager.api.DatabaseInspector;
import com.db4o.objectmanager.api.impl.DatabaseInspectorImpl;
import com.db4o.objectmanager.api.prefs.Preferences;
import com.db4o.objectmanager.model.Db4oConnectionSpec;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.windows.WindowsLookAndFeel;
import com.spaceprogram.db4o.sql.Result;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * User: treeder
 * Date: Aug 18, 2006
 * Time: 1:12:30 PM
 */
public class MainPanel extends JPanel {
    private MainFrame mainFrame;
    private Settings settings;
    private ObjectContainer objectContainer;
    private Db4oConnectionSpec connectionInfo;
    private DatabaseInspector databaseInspector;
    private QueryResultsPanel queryResultsPanel;
    private QueryBarPanel queryBarPanel;


    public MainPanel(MainFrame mainFrame, Settings settings) {
        super(new BorderLayout());
        this.mainFrame = mainFrame;
        this.settings = settings;
        build();
    }

    public void build() {
        add(buildMainPanel());
    }

    private Component buildMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(buildToolBar(), BorderLayout.NORTH);
        panel.add(buildQueryPanel(), BorderLayout.CENTER);
        return panel;
    }

    private Component buildQueryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(buildQueryBar(), BorderLayout.NORTH);
        panel.add(buildTabbedPane(), BorderLayout.CENTER);
        return panel;
    }

    private Component buildQueryBar() {
        queryBarPanel = new QueryBarPanel(this);
        return queryBarPanel;
    }


    private Preferences getPreferences() {
        return Preferences.getDefault();
    }
    public void setPreference(String key, Object pref) {
        getPreferences().setPreference(key, pref);
    }
    public Object getPreference(String key){
        return getPreferences().getPreference(key);
    }

    ObjectContainer getObjectContainer() {
        if (objectContainer == null) {
            objectContainer = Db4o.openFile(connectionInfo.getPath());
        }
        return objectContainer;
    }

    private Component buildTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
        //tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        addTabs(tabbedPane);

        tabbedPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        return tabbedPane;
    }

    private void addTabs(JTabbedPane tabbedPane) {
        queryResultsPanel = new QueryResultsPanel(this);
        tabbedPane.addTab("Home", queryResultsPanel);
        queryResultsPanel.addClassTreeListener(new ClassTreeListener(queryBarPanel));
    
        tabbedPane.addTab("Query 1", new JPanel());
        tabbedPane.addTab("Query 2", new JPanel());
    }

 
    private Component buildToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(true);
        toolBar.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
        // Swing
        toolBar.putClientProperty(
                Options.HEADER_STYLE_KEY,
                settings.getToolBarHeaderStyle());
        toolBar.putClientProperty(
                PlasticLookAndFeel.BORDER_STYLE_KEY,
                settings.getToolBarPlasticBorderStyle());
        toolBar.putClientProperty(
                WindowsLookAndFeel.BORDER_STYLE_KEY,
                settings.getToolBarWindowsBorderStyle());
        toolBar.putClientProperty(
                PlasticLookAndFeel.IS_3D_KEY,
                settings.getToolBar3DHint());

        AbstractButton button;

        toolBar.add(createToolBarButton("backward.gif", "Back"));
        button = createToolBarButton("forward.gif", "Next");
        button.setEnabled(false);
        toolBar.add(button);
        toolBar.add(createToolBarButton("home.gif", "Home"));
        toolBar.addSeparator();

        ActionListener openAction = new OpenFileActionListener();
        button = createToolBarButton("open.gif", "Open", openAction, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        button.addActionListener(openAction);
        toolBar.add(button);
        toolBar.add(createToolBarButton("print.gif", "Print"));
        toolBar.add(createToolBarButton("refresh.gif", "Update"));
        toolBar.addSeparator();

        button = createToolBarButton("help.gif", "Open Help");
        button.addActionListener(createHelpActionListener());
        toolBar.add(button);

        return toolBar;
    }

    protected AbstractButton createToolBarButton(String iconName, String toolTipText) {
        JButton button = new JButton(ResourceManager.createImageIcon(iconName));
        button.setToolTipText(toolTipText);
        button.setFocusable(false);
        return button;
    }

    private AbstractButton createToolBarButton(String iconName, String toolTipText, ActionListener action, KeyStroke keyStroke) {
        AbstractButton button = createToolBarButton(iconName, toolTipText);
        button.registerKeyboardAction(action, keyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        return button;
    }

    protected AbstractButton createToolBarRadioButton(String iconName, String toolTipText) {
        JToggleButton button = new JToggleButton(ResourceManager.createImageIcon(iconName));
        button.setToolTipText(toolTipText);
        button.setFocusable(false);
        return button;
    }

    public void setConnectionInfo(Db4oConnectionSpec connectionInfo) {
        this.connectionInfo = connectionInfo;
        queryResultsPanel.init();
    }

    public DatabaseInspector getDatabaseInspector() {
        if(databaseInspector == null){
            databaseInspector = new DatabaseInspectorImpl(getObjectContainer());
        }
        return databaseInspector;
    }

    public void displayResults(List<Result> results) {
        queryResultsPanel.displayResults(results);
    }


    private final class OpenFileActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            new JFileChooser().showOpenDialog(MainPanel.this);
        }
    }
    protected ActionListener createHelpActionListener() {
        return null;
    }
}

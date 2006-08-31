package com.db4o.objectManager.v2;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;
import com.db4o.objectManager.v2.uif_lite.component.Factory;
import com.db4o.objectManager.v2.uif_lite.panel.SimpleInternalFrame;
import com.db4o.objectmanager.api.DatabaseInspector;
import com.db4o.objectmanager.api.impl.DatabaseInspectorImpl;
import com.db4o.objectmanager.api.prefs.Preferences;
import com.db4o.objectmanager.model.Db4oConnectionSpec;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.windows.WindowsLookAndFeel;
import com.jgoodies.forms.factories.Borders;
import com.spaceprogram.db4o.sql.Result;
import com.spaceprogram.db4o.sql.ReflectHelper;

import javax.swing.*;
import javax.swing.tree.TreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
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
    private Db4oConnectionSpec connectionSpec;
    private DatabaseInspector databaseInspector;
    //2private QueryResultsPanel queryResultsPanel;
    private QueryBarPanel queryBarPanel;
    private JTabbedPane tabbedPane;
    private int queryCounter;

    private TreeModel classTreeModel;
    private JTree classTree;
    private DatabaseStatsPanel databaseStatsPanel;


    public MainPanel(MainFrame mainFrame, Settings settings, Db4oConnectionSpec connectionSpec) {
        super(new BorderLayout());
        this.mainFrame = mainFrame;
        this.settings = settings;
        this.connectionSpec = connectionSpec;
        build();
        initClassTree();
        mainFrame.addKeyListener(new ShortcutsListener());

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
        JSplitPane splitPane = Factory.createStrippedSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                buildMainLeftPanel(),
                buildTabbedPane(),
                0.2f);

        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }


    private Component buildQueryBar() {
        queryBarPanel = new QueryBarPanel(this);
        return queryBarPanel;
    }


    private JComponent buildMainLeftPanel() {
        JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.BOTTOM);
        tabbedPane.putClientProperty(Options.EMBEDDED_TABS_KEY, Boolean.TRUE);
        tabbedPane.addTab("Tree", buildTree());
        tabbedPane.addTab("Help", Factory.createStrippedScrollPane(buildHelp()));

        SimpleInternalFrame sif = new SimpleInternalFrame("Tree View");
        sif.setPreferredSize(new Dimension(150, 100));
        sif.setBorder(Borders.DIALOG_BORDER);    
        sif.add(tabbedPane);
        return sif;
    }


    private JScrollPane buildTree() {
        classTree = new JTree(createClassTreeModel());
        classTree.putClientProperty(Options.TREE_LINE_STYLE_KEY,
                Options.TREE_LINE_STYLE_NONE_VALUE);
        classTree.setToggleClickCount(2);
        return new JScrollPane(classTree);

    }


    private JComponent buildHelp() {
        JTextArea area = new JTextArea("\n Some help info could go here.");
        return area;
    }

    private TreeModel createClassTreeModel() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Stored Classes");
        classTreeModel = new DefaultTreeModel(root);
        return classTreeModel;
    }

    public void initClassTree() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) classTreeModel.getRoot();
        DefaultMutableTreeNode parent;

        DatabaseInspector inspector = getDatabaseInspector();
        List<ReflectClass> classesStored = inspector.getClassesStored();
        for (int i = 0; i < classesStored.size(); i++) {
            ReflectClass storedClass = classesStored.get(i);
            parent = new DefaultMutableTreeNode(storedClass.getName());
            root.add(parent);
            ReflectField[] fields = ReflectHelper.getDeclaredFields(storedClass);
            for (int j = 0; j < fields.length; j++) {
                ReflectField field = fields[j];
                parent.add(new DefaultMutableTreeNode(field.getName()));
            }
        }
        classTree.expandRow(0);

        addClassTreeListener(new ClassTreeListener(queryBarPanel));
    }




    public void addClassTreeListener(MouseListener classTreeListener) {
        classTree.addMouseListener(classTreeListener);
    }


    private Preferences getPreferences() {
        return Preferences.getDefault();
    }

    public void setPreference(String key, Object pref) {
        getPreferences().setPreference(key, pref);
    }

    public Object getPreference(String key) {
        return getPreferences().getPreference(key);
    }

    ObjectContainer getObjectContainer() {
        if (objectContainer == null) {
            objectContainer = Db4o.openFile(connectionSpec.getPath());
        }
        return objectContainer;
    }

    private Component buildTabbedPane() {

        tabbedPane = new JTabbedPane(SwingConstants.TOP);
        //tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        tabbedPane.addKeyListener(new TabShortCutsListener(tabbedPane));

        addTabs(tabbedPane);

        return tabbedPane;
    }

    private void addTabs(JTabbedPane tabbedPane) {
        databaseStatsPanel = new DatabaseStatsPanel(getDatabaseInspector());
        tabbedPane.addTab("Home", databaseStatsPanel);
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

    public void setConnectionSpec(Db4oConnectionSpec connectionSpec) {
        this.connectionSpec = connectionSpec;
        initClassTree();        
    }

    public DatabaseInspector getDatabaseInspector() {
        if (databaseInspector == null) {
            databaseInspector = new DatabaseInspectorImpl(getObjectContainer());
        }
        return databaseInspector;
    }

    public void displayResults(String query) {
        QueryResultsPanel p = new QueryResultsPanel(this);
        tabbedPane.add("Query " + (++queryCounter), p);
        tabbedPane.setSelectedComponent(p);
        p.displayResults(query);

        //queryResultsPanel.displayResults(results);
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

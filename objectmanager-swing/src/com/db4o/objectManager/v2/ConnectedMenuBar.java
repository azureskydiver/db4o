package com.db4o.objectManager.v2;

import com.jgoodies.looks.Options;
import com.jgoodies.looks.windows.WindowsLookAndFeel;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * User: treeder
 * Date: Aug 21, 2006
 * Time: 6:07:00 PM
 */
public class ConnectedMenuBar extends BaseMenuBar {
    public ConnectedMenuBar(Settings settings, ActionListener helpActionListener, ActionListener aboutActionListener) {
        super(settings, helpActionListener, aboutActionListener);

        add(buildFileMenu());
        add(buildHelpMenu(helpActionListener, aboutActionListener));
    }

    private JMenu buildFileMenu() {
        JMenuItem item;

        JMenu menu = createMenu("File", 'F');

        // Build a submenu that has the noIcons hint set.
        JMenu submenu = createMenu("New", 'N');
        submenu.putClientProperty(Options.NO_ICONS_KEY, Boolean.TRUE);
        submenu.add(createMenuItem("Connection\u2026", 'P', KeyStroke.getKeyStroke("ctrl F8")));

        menu.add(submenu);
        menu.addSeparator();
        item = createMenuItem("Close", 'C', KeyStroke.getKeyStroke("ctrl F4"));
        menu.add(item);
        item = createMenuItem("Close All", 'o', KeyStroke.getKeyStroke("ctrl shift F4"));
        menu.add(item);
        menu.addSeparator();
        item = createMenuItem("Save Query", ResourceManager.createImageIcon("save_edit.gif"), 'd', KeyStroke.getKeyStroke("ctrl S"));
        item.setEnabled(false);
        menu.add(item);
        item = createMenuItem("Save Query As\u2026", ResourceManager.createImageIcon("saveas_edit.gif"), 'e');
        menu.add(item);
        item = createMenuItem("Save All", 'A', KeyStroke.getKeyStroke("ctrl shift S"));
        item.setEnabled(false);
        menu.add(item);
        menu.addSeparator();
        item = createMenuItem("Print", ResourceManager.createImageIcon("print.gif"), 'P', KeyStroke.getKeyStroke("ctrl P"));
        menu.add(item);
        menu.addSeparator();
        menu.add(createMenuItem("Recent connection 1", '1'));
        menu.add(createMenuItem("Recent connection 2", '2'));
        menu.addSeparator();
        menu.add(createMenuItem("Exit", 'E'));

        return menu;
    }


}

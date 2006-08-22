package com.db4o.objectManager.v2;

import com.jgoodies.looks.Options;
import com.jgoodies.looks.windows.WindowsLookAndFeel;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * User: treeder
 * Date: Aug 21, 2006
 * Time: 6:07:00 PM
 */
public class ConnectedMenuBar extends JMenuBar {
    public ConnectedMenuBar(Settings settings, ActionListener helpActionListener, ActionListener aboutActionListener) {
        super();

        putClientProperty(Options.HEADER_STYLE_KEY,
                settings.getMenuBarHeaderStyle());
        putClientProperty(PlasticLookAndFeel.BORDER_STYLE_KEY,
                settings.getMenuBarPlasticBorderStyle());
        putClientProperty(WindowsLookAndFeel.BORDER_STYLE_KEY,
                settings.getMenuBarWindowsBorderStyle());
        putClientProperty(PlasticLookAndFeel.IS_3D_KEY,
                settings.getMenuBar3DHint());

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

    private JMenu buildHelpMenu(ActionListener helpActionListener, ActionListener aboutActionListener) {

        JMenu menu = createMenu("Help", 'H');

        JMenuItem item;
        item = createMenuItem("Help Contents", ResourceManager.createImageIcon("help.gif"), 'H');
        if (helpActionListener != null) {
            item.addActionListener(helpActionListener);
        }
        menu.add(item);

        menu.addSeparator();
        item = createMenuItem("About", 'a');
        item.addActionListener(aboutActionListener);
        menu.add(item);

        return menu;
    }

    protected JMenu createMenu(String text, char mnemonic) {
        JMenu menu = new JMenu(text);
        menu.setMnemonic(mnemonic);
        return menu;
    }


    protected JMenuItem createMenuItem(String text) {
        return new JMenuItem(text);
    }


    protected JMenuItem createMenuItem(String text, char mnemonic) {
        return new JMenuItem(text, mnemonic);
    }

    protected JMenuItem createMenuItem(String text, char mnemonic, KeyStroke key) {
        JMenuItem menuItem = new JMenuItem(text, mnemonic);
        menuItem.setAccelerator(key);
        return menuItem;
    }


    protected JMenuItem createMenuItem(String text, Icon icon) {
        return new JMenuItem(text, icon);
    }


    protected JMenuItem createMenuItem(String text, Icon icon, char mnemonic) {
        JMenuItem menuItem = new JMenuItem(text, icon);
        menuItem.setMnemonic(mnemonic);
        return menuItem;
    }


    protected JMenuItem createMenuItem(String text, Icon icon, char mnemonic, KeyStroke key) {
        JMenuItem menuItem = createMenuItem(text, icon, mnemonic);
        menuItem.setAccelerator(key);
        return menuItem;
    }
}

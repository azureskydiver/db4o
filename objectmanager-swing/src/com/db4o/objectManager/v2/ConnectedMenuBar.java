package com.db4o.objectManager.v2;

import com.jgoodies.looks.Options;
import com.jgoodies.looks.windows.WindowsLookAndFeel;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.db4o.objectmanager.api.util.Defragment;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

/**
 * User: treeder
 * Date: Aug 21, 2006
 * Time: 6:07:00 PM
 */
public class ConnectedMenuBar extends BaseMenuBar {
    private MainFrame frame;

    public ConnectedMenuBar(MainFrame mainFrame, Settings settings, ActionListener helpActionListener, ActionListener aboutActionListener) {
        super(settings, helpActionListener, aboutActionListener);
        frame = mainFrame;

        add(buildFileMenu());
        add(buildManageMenu());
        add(buildHelpMenu(helpActionListener, aboutActionListener));
    }

    private JMenu buildManageMenu() {
        JMenuItem item;

        JMenu menu = createMenu("Manage", 'M');

        // Build a submenu that has the noIcons hint set.

        item = createMenuItem("Backup\u2026", ResourceManager.createImageIcon("saveas_edit.gif"), 'e');
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int response = fileChooser.showSaveDialog(frame);
                if(response == JFileChooser.APPROVE_OPTION){
                    File f = fileChooser.getSelectedFile();
                    try {
                        frame.getObjectContainer().ext().backup(f.getAbsolutePath());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Error during backup! " + e1.getMessage(), "Error during backup", JOptionPane.ERROR_MESSAGE, ResourceManager.createImageIcon("icons/32x32/warning.png"));
                    }
                }
            }
        });
        menu.add(item);
        item = createMenuItem("Defragment", 'd', KeyStroke.getKeyStroke("ctrl shift D"));
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showConfirmDialog(frame, "Are you sure you want to defragment this database?" +
                        "Please be aware of the side effects of running defragment. See db4o manual.");
                new Defragment().run(frame.getConnectionSpec().getFullPath(), true);
            }
        });
        item.setEnabled(true);
        menu.add(item);

        return menu;
    }

    private JMenu buildFileMenu() {
        JMenuItem item;

        JMenu menu = createMenu("File", 'F');

        item = createMenuItem("Close", 'C', KeyStroke.getKeyStroke("ctrl F4"));
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();

            }
        });
        menu.add(item);
        //menu.add(createMenuItem("Exit", 'E'));

        return menu;
    }


}

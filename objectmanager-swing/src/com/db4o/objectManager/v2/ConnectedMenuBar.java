package com.db4o.objectManager.v2;

import com.db4o.objectManager.v2.uiHelper.OptionPaneHelper;
import com.db4o.defragment.Defragment;
import com.db4o.defragment.AvailableClassFilter;
import com.db4o.defragment.DefragmentConfig;

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
		if (!frame.getConnectionSpec().isRemote()) {
			add(buildManageMenu());
		}
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
				if (response == JFileChooser.APPROVE_OPTION) {
					File f = fileChooser.getSelectedFile();
					try {
						frame.getObjectContainer().ext().backup(f.getAbsolutePath());
						OptionPaneHelper.showSuccessDialog(frame, "Backup completed successfully.", "Backup Successful");
					} catch (IOException e1) {
						e1.printStackTrace();
						OptionPaneHelper.showErrorMessage(frame, "Error during backup! " + e1.getMessage(), "Error during backup");
					}
				}
			}
		});
		menu.add(item);
		item = createMenuItem("Defragment", 'd', KeyStroke.getKeyStroke("ctrl shift D"));
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to defragment this database?\n" +
						"This operation will shutdown the currently running database and reopen after the operation completes.\n" +
						"Please be aware of the side effects of running defragment. See db4o manual.");
				if (result == JOptionPane.YES_OPTION) {
					frame.closeObjectContainer();
					try {
						/*DefragmentConfig config = new DefragmentConfig(frame.getConnectionSpec().getFullPath());
						config.forceBackupDelete(true);
						Defragment.defrag(config);
						OptionPaneHelper.showSuccessDialog(frame, "Defragment was successful!", "Defragment Complete");*/
						OptionPaneHelper.showErrorMessage(frame, "Defrag broken", "Error during Defragment");
						// todo: progress bar
					} catch (Exception e1) {
						OptionPaneHelper.showErrorMessage(frame, e1.toString() + "\n\n" + e1.getMessage(), "Error during Defragment");
						e1.printStackTrace();
					}
				}
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
				frame.close();

			}
		});
		menu.add(item);
		menu.addSeparator();

		item = createMenuItem("Exit", 'E');
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.exit();
			}
		});
		menu.add(item);

		return menu;
	}


}

/*
 * This file is part of com.db4o.browser.
 *
 * com.db4o.browser is free software; you can redistribute it and/or modify
 * it under the terms of version 2 of the GNU General Public License
 * as published by the Free Software Foundation.
 *
 * com.db4o.browser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with com.swtworkbench.ed; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.db4o.browser.gui.standalone;

import java.io.*;
import java.util.*;

import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import com.db4o.*;
import com.db4o.browser.gui.controllers.*;
import com.db4o.browser.gui.views.*;
import com.db4o.browser.model.*;
import com.db4o.browser.prefs.*;
import com.db4o.reflect.generic.*;
import com.db4o.reflect.jdk.*;
import com.swtworkbench.community.xswt.*;

/**
 * Class StandaloneBrowser.
 * 
 * @author djo
 */
public class StandaloneBrowser implements IControlFactory {
    
    public static final String appName = "explorer4objects";
    
    private Shell shell;
    private DbBrowserPane ui;
    private BrowserController controller;
    
    /* (non-Javadoc)
	 * @see com.db4o.browser.gui.standalone.IControlFactory#createContents(org.eclipse.swt.widgets.Composite)
	 */
	public void createContents(Composite parent) {
        shell = (Shell) parent;
        shell.setLayout(new FillLayout());
        shell.setText(appName);
        
        ui = new DbBrowserPane(shell, SWT.NULL);
        buildMenuBar((Shell)parent);
        
        controller = new BrowserController(ui);
		
		// FIXME: hard-coding initial open...
		String testFile=getClass().getResource("formula1.yap").getFile();
		controller.open(testFile);
	}
    
	/**
	 * Build the application menu bar
	 */
	private void buildMenuBar(final Shell shell) {
        Map choices = XSWT.createl(shell, "menu.xswt", getClass());
		
        MenuItem open = (MenuItem) choices.get("Open");
        MenuItem search = (MenuItem) choices.get("Search");
        MenuItem newWindow = (MenuItem) choices.get("NewWindow");
        MenuItem close = (MenuItem) choices.get("Close");
		MenuItem preferences = (MenuItem) choices.get("Preferences");
		MenuItem adddirtoclasspath = (MenuItem) choices.get("AddDirToClasspath");
		MenuItem addfiletoclasspath = (MenuItem) choices.get("AddFileToClasspath");
        
        open.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(shell, SWT.OPEN);
				dialog.setFilterExtensions(new String[]{"*.yap"});
                String file = dialog.open();
                if (file != null) {
                    controller.open(file);
                }
            }
        });
        
        newWindow.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
                Shell shell = SWTProgram.newShell(Display.getCurrent());
                new StandaloneBrowser().createContents(shell);
                shell.open();
			}
        });
		
		preferences.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				PreferenceUI.getDefault().showPreferencesDialog(shell);
			}
		});
        
        close.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                shell.close();
            }
        });

        adddirtoclasspath.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog dialog = new DirectoryDialog(shell, SWT.OPEN);
                String file = dialog.open();
                if (file != null) {
					controller.addToClasspath(new File(file));
                }
            }
        });
        addfiletoclasspath.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(shell, SWT.OPEN);
				dialog.setFilterExtensions(new String[]{"*.jar","*.zip"});
                String file = dialog.open();
                if (file != null) {
					controller.addToClasspath(new File(file));
                }
            }
        });
	}

	public static void main(String[] args) {
		Db4o.configure().reflectWith(new GenericReflector(new JdkReflector(StandaloneBrowser.class.getClassLoader())));
        SWTProgram.registerCloseListener(BrowserCore.getDefault());
        SWTProgram.runWithLog(new StandaloneBrowser());
	}
}



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

import java.io.File;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import com.db4o.browser.gui.controllers.BrowserController;
import com.db4o.browser.gui.controllers.QueryController;
import com.db4o.browser.gui.views.DbBrowserPane;
import com.db4o.browser.model.BrowserCore;
import com.db4o.browser.prefs.PreferenceUI;
import com.db4o.reflect.ReflectClass;
import com.swtworkbench.community.xswt.XSWT;

/**
 * Class StandaloneBrowser.
 * 
 * @author djo
 */
public class StandaloneBrowser implements IControlFactory {
    
    public static final String appName = "Object Manager";
    
    private Shell shell;
    private CTabFolder folder;
    private CTabItem mainTab;
    
    private DbBrowserPane ui;
    private BrowserController browserController;
    private QueryController queryController;

    private Color title_background;
    private Color title_background_gradient;
    private Color title_foreground;
    private Color title_inactive_background;
    private Color title_inactive_background_gradient;
    private Color title_inactive_foreground;

    public static final String STATUS_BAR = "StatusBar";
    
    /* (non-Javadoc)
	 * @see com.db4o.browser.gui.standalone.IControlFactory#createContents(org.eclipse.swt.widgets.Composite)
	 */
	public void createContents(Composite parent) {
        title_background = Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_BACKGROUND);
        title_background_gradient = Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT);
        title_foreground = Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_FOREGROUND);
        title_inactive_background = Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND);
        title_inactive_background_gradient = Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT);
        title_inactive_foreground = Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND);
                
        shell = (Shell) parent;
        shell.setLayout(new GridLayout());
        shell.setText(appName);
        buildMenuBar(shell);
        
        folder = new CTabFolder(shell, SWT.NULL);
        folder.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
        folder.setBorderVisible(true);
        folder.setSimple(false);
        folder.setSelectionBackground(new Color[] {title_background, title_background_gradient}, new int[] { 75 }, true);
        folder.setSelectionForeground(title_foreground);
        
        StatusBar statusBar = new StatusBar(shell, SWT.NULL);
        statusBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        shell.setData(STATUS_BAR, statusBar);
        
        shell.addShellListener(new ShellAdapter() {
            public void shellActivated(ShellEvent e) {
                folder.setSelectionBackground(new Color[] {title_background, title_background_gradient}, new int[] { 75 }, true);
                folder.setSelectionForeground(title_foreground);
            }

            public void shellDeactivated(ShellEvent e) {
                folder.setSelectionBackground(new Color[] {title_inactive_background, title_inactive_background_gradient}, new int[] { 75 }, true);
                folder.setSelectionForeground(title_inactive_foreground);
            }
        });
        
        ui = new DbBrowserPane(folder, SWT.NULL);
        mainTab = new CTabItem(folder, SWT.NULL);
        mainTab.setControl(ui);
        
        queryController = new QueryController(folder);
        browserController = new BrowserController(ui, queryController);
        queryController.setBrowserController(browserController);
		
		// FIXME: hard-coding initial open...
		String testFile=getClass().getResource("formula1.yap").getFile();
		browserController.open(testFile);
        setTabText(testFile);
	}
    
    private void setTabText(String fileName) {
        File tabFile = new File(fileName);
        mainTab.setText(tabFile.getName());
    }
    
	/**
	 * Build the application menu bar
	 */
	private void buildMenuBar(final Shell shell) {
        Map choices = XSWT.createl(shell, "menu.xswt", getClass());
		
        MenuItem open = (MenuItem) choices.get("Open");
        MenuItem query = (MenuItem) choices.get("Query");
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
                setTabText(file);
                if (file != null) {
                    browserController.open(file);
                }
            }
        });
        
        query.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                ReflectClass toOpen = browserController.chooseClass();
                if (toOpen != null) {
                    queryController.open(toOpen);
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
					browserController.addToClasspath(new File(file));
                }
            }
        });
        addfiletoclasspath.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(shell, SWT.OPEN);
				dialog.setFilterExtensions(new String[]{"*.jar","*.zip"});
                String file = dialog.open();
                if (file != null) {
					browserController.addToClasspath(new File(file));
                }
            }
        });
	}

	public static void main(String[] args) {
//		Db4o.configure().reflectWith(new GenericReflector(new JdkReflector(StandaloneBrowser.class.getClassLoader())));
        SWTProgram.registerCloseListener(BrowserCore.getDefault());
        SWTProgram.runWithLog(new StandaloneBrowser());
	}
}



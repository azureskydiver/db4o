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
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Map;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import com.db4o.Db4o;
import com.db4o.browser.gui.controllers.BrowserController;
import com.db4o.browser.gui.controllers.QueryController;
import com.db4o.browser.gui.dialogs.SelectServer;
import com.db4o.browser.gui.views.DbBrowserPane;
import com.db4o.browser.model.BrowserCore;
import com.db4o.browser.model.IBrowserCoreListener;
import com.db4o.browser.prefs.PreferenceUI;
import com.swtworkbench.community.xswt.XSWT;
import com.swtworkbench.community.xswt.metalogger.FileLogger;
import com.swtworkbench.community.xswt.metalogger.Logger;
import com.swtworkbench.community.xswt.metalogger.StdLogger;
import com.swtworkbench.community.xswt.metalogger.TeeLogger;

/**
 * Class StandaloneBrowser.
 * 
 * @author djo
 */
public class StandaloneBrowser implements IControlFactory {
    
    public static final String appName = "Object Manager";
    public static final String LOGFILE = ".objectmanager.log";
    private static final String LOGCONFIG = ".objectmanager.logconfig";
    
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
    protected String fileName;
    
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
        mainTab.setImage(new Image(Display.getCurrent(),
                DbBrowserPane.class.getResourceAsStream("icons/etool16/database2.gif")));
        mainTab.setControl(ui);
        
        queryController = new QueryController(folder);
        browserController = new BrowserController(ui, queryController);
        queryController.setBrowserController(browserController);
        
        BrowserCore.getDefault().addBrowserCoreListener(browserCoreListener);
		
		// FIXME: hard-coding initial open...
//		String testFile=getClass().getResource("formula1.yap").getFile();
//		browserController.open(testFile);
//        setTabText(testFile);
	}
    
    private void setTabText(String fileName) {
        this.fileName = fileName;
        File tabFile = new File(fileName);
        mainTab.setText(tabFile.getName());
    }
    
	/**
	 * Build the application menu bar
	 */
	private void buildMenuBar(final Shell shell) {
        Map choices = XSWT.createl(shell, "menu.xswt", getClass());
		
        MenuItem open = (MenuItem) choices.get("Open");
        MenuItem openServer = (MenuItem) choices.get("OpenServer");
        MenuItem query = (MenuItem) choices.get("Query");
        MenuItem search = (MenuItem) choices.get("Search");
        MenuItem newWindow = (MenuItem) choices.get("NewWindow");
        MenuItem close = (MenuItem) choices.get("Close");
		MenuItem preferences = (MenuItem) choices.get("Preferences");
		MenuItem adddirtoclasspath = (MenuItem) choices.get("AddDirToClasspath");
		MenuItem addfiletoclasspath = (MenuItem) choices.get("AddFileToClasspath");
        MenuItem helpAbout = (MenuItem)choices.get("HelpAbout");
        
        open.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(shell, SWT.OPEN);
				dialog.setFilterExtensions(new String[]{"*.yap", "*"});
                String file = dialog.open();
                if (file != null) {
                    setTabText(file);
                    browserController.open(file);
                }
            }
        });
        
        openServer.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                SelectServer dialog = new SelectServer(shell);
                if (dialog.open() == Window.OK) {
                    String host = dialog.getHostName();
                    int port = dialog.getPort();
                    String user = dialog.getUser();
                    String password = dialog.getPassword();
                    if (browserController.open(host, port, user, password)) {
                        setTabText(host + ":" + port);
                    }
                }
            }
        });
        
        query.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                browserController.newQuery();
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

//        adddirtoclasspath.addSelectionListener(new SelectionAdapter() {
//            public void widgetSelected(SelectionEvent e) {
//                DirectoryDialog dialog = new DirectoryDialog(shell, SWT.OPEN);
//                String file = dialog.open();
//                if (file != null) {
//					browserController.addToClasspath(new File(file));
//                }
//            }
//        });
//        addfiletoclasspath.addSelectionListener(new SelectionAdapter() {
//            public void widgetSelected(SelectionEvent e) {
//                FileDialog dialog = new FileDialog(shell, SWT.OPEN);
//				dialog.setFilterExtensions(new String[]{"*.jar","*.zip"});
//                String file = dialog.open();
//                if (file != null) {
//					browserController.addToClasspath(new File(file));
//                }
//            }
//        });

        helpAbout.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                new AboutBox(shell).open();
            }
        });
    }

    private IBrowserCoreListener browserCoreListener = new IBrowserCoreListener() {
        public void classpathChanged(BrowserCore browserCore) {
            CTabItem[] openedViews = folder.getItems();
            
            // Close all query tabs
            for (int view = 0; view < openedViews.length; view++) {
                if (openedViews[view] == mainTab) {
                    continue;
                }
                openedViews[view].getControl().dispose();
                openedViews[view].dispose();
            }
            
            // Refresh the browser
            if (browserController.getInput() != null) {
                browserController.reopen();//setInput(browserController.getInput(), 
                        //browserController.getInitialSelection());
            }
        }
    };
    
	public static void main(String[] args) throws IOException {
        PrintStreamLogger db4ologger = new PrintStreamLogger();
        Logger.setLogger(new TeeLogger(new StdLogger(), new FileLogger(getLogPath(LOGFILE), getLogPath(LOGCONFIG))));
        Db4o.configure().setOut(new PrintStream(db4ologger, true));
        Logger.log().setDebug(SWTProgram.class, true);
        Logger.log().debug(SWTProgram.class, new Date().toString() + ": Application startup");
        
        SWTProgram.registerCloseListener(BrowserCore.getDefault());
        SWTProgram.runWithLog(new StandaloneBrowser());
        
        //TODO: Getting a multiple-close exception from db4o?
        //      Race condition with db4o shutdown?  error came after App shutdown message.
        db4ologger.close();
        Logger.log().debug(SWTProgram.class, new Date().toString() + ": Application shutdown");
	}

    private static String getLogPath(String fileName) {
        return new File(new File(System.getProperty("user.home", ".")),fileName).getAbsolutePath();
    }
}



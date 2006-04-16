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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ve.sweet.hinthandler.HintHandler;
import org.eclipse.ve.sweet.hinthandler.IHintHandler;

import com.db4o.Db4o;
import com.db4o.browser.gui.controllers.BrowserTabController;
import com.db4o.browser.gui.controllers.QueryController;
import com.db4o.browser.gui.dialogs.OpenFile;
import com.db4o.browser.gui.dialogs.SelectServer;
import com.db4o.browser.gui.views.DbBrowserPane;
import com.db4o.browser.model.BrowserCore;
import com.db4o.browser.model.IBrowserCoreListener;
import com.db4o.browser.model.IGraphIterator;
import com.db4o.browser.model.TheNullGraphIterator;
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
    
    public static final String APPNAME = "db4o Object Manager";
    public static final String VERSION = "1.7";
    public static final String LOGFILE = ".objectmanager.log";
    public static final String LOGCONFIG = ".objectmanager.logconfig";
    
    private Shell shell = null;
    private CTabFolder folder;
    private CTabItem mainTab;
    
    private DbBrowserPane ui;
    private BrowserTabController browserController;
    private QueryController queryController;

    private Color title_background;
    private Color title_background_gradient;
    private Color title_foreground;
    private Color title_inactive_background;
    private Color title_inactive_background_gradient;
    private Color title_inactive_foreground;

    public static final String STATUS_BAR = "StatusBar";
    protected String fileName;
    
    private String commandLineFileName = null;
    
    public StandaloneBrowser() {
    	// Nothing needed here
    }
    
    public StandaloneBrowser(String[] args) {
    	if (args.length > 0) {
    		commandLineFileName = args[0];
    		if(commandLineFileName==null||commandLineFileName.trim().length()<1) {
    			commandLineFileName=null;
    		}
    	}
	}

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
        
        /*
         * parent is an instanceof Shell when running as a standalone application.
         * When running as an RCP application parent is an instanceof Composite.
         * 
         * In the latter case, the shell branding elements ane menu bar are controlled 
         * by the RCP framework.
         */
        if (!(parent instanceof Shell)) {
        	shell = parent.getShell();
        } else {
        	shell = (Shell) parent;
        }
        shell.setText(APPNAME);
        shell.setImage(new Image(Display.getCurrent(),
                DbBrowserPane.class.getResourceAsStream("icons/etool16/database2.gif")));
        buildMenuBar(shell);
        parent.setLayout(new GridLayout());
        
        folder = new CTabFolder(parent, SWT.NULL);
        folder.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
        folder.setBorderVisible(true);
        folder.setSimple(false);
        folder.setSelectionBackground(new Color[] {title_background, title_background_gradient}, new int[] { 75 }, true);
        folder.setSelectionForeground(title_foreground);
        
        final StatusBar statusBar;
        
        statusBar = new StatusBar(parent, SWT.NULL);
        statusBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        shell.setData(STATUS_BAR, statusBar);
        
        HintHandler.setHintHandler(new IHintHandler() {
            public void setMessage(String message) {
                statusBar.setMessage(message);
            }
            public void clearMessage() {
                statusBar.setMessage("");
            }
        });
        
        shell.addShellListener(new ShellAdapter() {
            public void shellActivated(ShellEvent e) {
                folder.setSelectionBackground(new Color[] {title_background, title_background_gradient}, new int[] { 75 }, true);
                folder.setSelectionForeground(title_foreground);
            }

            public void shellDeactivated(ShellEvent e) {
                folder.setSelectionBackground(new Color[] {title_inactive_background, title_inactive_background_gradient}, new int[] { 75 }, true);
                folder.setSelectionForeground(title_inactive_foreground);
            }
            
            public void shellClosed(ShellEvent e) {
            	if (!browserController.canClose()) {
            		e.doit = false;
            		return;
            	}
            	if (!queryController.canClose()) {
            		e.doit = false;
            		return;
            	}
            }
        });
        
        ui = new DbBrowserPane(folder, SWT.NULL);
        mainTab = new CTabItem(folder, SWT.NULL);
        mainTab.setImage(new Image(Display.getCurrent(),
                DbBrowserPane.class.getResourceAsStream("icons/etool16/database2.gif")));
        mainTab.setControl(ui);
        
        queryController = new QueryController(folder);
        browserController = new BrowserTabController(ui, queryController);
        queryController.setBrowserController(browserController);
        
        BrowserCore.getDefault().addBrowserCoreListener(browserCoreListener);
		
        if (commandLineFileName != null) {
        	if(browserController.open(commandLineFileName)) {
        		setTabText(commandLineFileName);
        		setOpenedDatabaseMenuChoiceState();
        	}
        }
	}
    
    private void setTabText(String fileName) {
        this.fileName = fileName;
        File tabFile = new File(fileName);
        mainTab.setText(tabFile.getName());
    }
    
    private MenuItem open;
    private MenuItem openScrambledFile;
    private MenuItem openServer;
    private MenuItem closeAll;
    private MenuItem xmlExport;
    private MenuItem query;
    private MenuItem newWindow;
    private MenuItem closeWindow;
    private MenuItem preferences;
    private MenuItem helpAbout;
    
    private void setOpenedDatabaseMenuChoiceState() {
        xmlExport.setEnabled(true);
        closeAll.setEnabled(true);
        query.setEnabled(true);
    }
    
    private void setClosedDatabaseMenuChoiceState() {
        xmlExport.setEnabled(false);
        closeAll.setEnabled(false);
        query.setEnabled(false);
    }
    
	/**
	 * Build the application menu bar
	 */
	private void buildMenuBar(final Shell shell) {
        Map choices = XSWT.createl(shell, "menu.xswt", getClass());
		
        open = (MenuItem) choices.get("Open");
        openServer = (MenuItem) choices.get("OpenServer");
        closeAll = (MenuItem) choices.get("CloseAllDatabases");
        xmlExport = (MenuItem) choices.get("XMLExport");
        query = (MenuItem) choices.get("Query");
        newWindow = (MenuItem) choices.get("NewWindow");
        closeWindow = (MenuItem) choices.get("Close");
		preferences = (MenuItem) choices.get("Preferences");
        helpAbout = (MenuItem)choices.get("HelpAbout");
        
//        open.addSelectionListener(new SelectionAdapter() {
//            public void widgetSelected(SelectionEvent e) {
//                FileDialog dialog = new FileDialog(shell, SWT.OPEN);
//                dialog.setFilterExtensions(new String[]{"*.yap", "*"});
//                String file = dialog.open();
//                if (file != null) {
//                    setTabText(file);
//                    browserController.open(file);
//                    setOpenedDatabaseMenuChoiceState();
//                }
//            }
//        });
        
        open.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                OpenFile dialog = new OpenFile(shell);
                if (dialog.open() == Window.OK) {
                    String file = dialog.getFileName();
                    String password = dialog.getPassword();
                    boolean readOnly=dialog.getReadOnly();
                    try {
                    	if(password!=null&&password.length()>0) {
	                        Db4o.configure().encrypt(true);
	                        Db4o.configure().password(password);
                    	}
                        try {
                            if (browserController.open(file,readOnly)) {
                                setTabText(file);
                                setOpenedDatabaseMenuChoiceState();
                            }
                        } catch (Throwable ex) {
                            MessageBox messageBox = new MessageBox(ui.getShell(), SWT.ICON_ERROR);
                            messageBox.setText("Error");
                            messageBox.setMessage(ex.getMessage() + "\n\nPossibly incorrect password?");
                            messageBox.open();
                        }
                    } finally {
                        Db4o.configure().encrypt(false);
                        Db4o.configure().password(null);
                    }
                    
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
                    boolean readOnly=dialog.getReadOnly();
                    if (browserController.open(host, port, user, password,readOnly)) {
                        setTabText(host + ":" + port);
                    }
                    setOpenedDatabaseMenuChoiceState();
                }
            }
        });
        
        closeAll.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		MessageBox areYouSure = new MessageBox(shell, SWT.YES | SWT.NO | SWT.ICON_QUESTION);
        		areYouSure.setText("Question");
        		areYouSure.setMessage("Are you sure you want to close all open databases?");
        		int userIsSure = areYouSure.open();
        		if (userIsSure == SWT.YES) {
        			closeAllQueries();
        			BrowserCore.getDefault().closeAllDatabases();
        			IGraphIterator nullGraphIterator = TheNullGraphIterator.getDefault();
        			browserController.setInput(nullGraphIterator, nullGraphIterator.getPath());
        			setTabText("");
        			setClosedDatabaseMenuChoiceState();
        		}
        	}
        });
        
        xmlExport.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(shell, SWT.SAVE);
                dialog.setFilterExtensions(new String[]{"*.xml", "*"});
                String file = dialog.open();
                if (file != null) {
                	browserController.xmlExport(file);
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
        
        closeWindow.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                shell.close();
            }
        });

        helpAbout.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                new AboutBox(shell).open();
            }
        });
    }

    /**
	 * Close all open user interface views
	 */
	private void closeAllQueries() {
		CTabItem[] openedViews = folder.getItems();
		
		// Close all query tabs
		for (int view = 0; view < openedViews.length; view++) {
		    if (openedViews[view] == mainTab) {
		        continue;
		    }
		    queryController.close(openedViews[view]);
		}
	}

	private IBrowserCoreListener browserCoreListener = new IBrowserCoreListener() {
        public void classpathChanged(BrowserCore browserCore) {
            closeAllQueries();
            // Refresh the browser
    		if (browserController.getInput() != null) {
    		    browserController.reopen();//setInput(browserController.getInput(), 
    		            //browserController.getInitialSelection());
    		}        
    	}
        public void closeEditors(BrowserCore core) {
        	closeAllQueries();
			IGraphIterator nullGraphIterator = TheNullGraphIterator.getDefault();
			browserController.setInput(nullGraphIterator, nullGraphIterator.getPath());
			setTabText("");
			setClosedDatabaseMenuChoiceState();
        }
    };
    
	public static void main(String[] args) throws IOException {
        PrintStreamLogger db4ologger = new PrintStreamLogger();
        Logger.setLogger(new TeeLogger(new StdLogger(), new FileLogger(getLogPath(LOGFILE), getLogPath(LOGCONFIG))));
        Db4o.configure().setOut(new PrintStream(db4ologger, true));
        Logger.log().setDebug(SWTProgram.class, true);
        String startupDate = new Date().toString();
        Logger.log().debug(SWTProgram.class, startupDate + ": " + APPNAME + " " + VERSION + " startup");
        Logger.log().debug(SWTProgram.class, APPNAME + ": Initializing " + Db4o.version() + " library");
        
        SWTProgram.registerCloseListener(BrowserCore.getDefault());
        SWTProgram.runWithLog(new StandaloneBrowser(args));
        
        // NPE from finalizer thread
        // db4ologger.close();
        Logger.log().debug(SWTProgram.class, new Date().toString() + ": Application shutdown");
	}

    public static String getLogPath(String fileName) {
        return new File(new File(System.getProperty("user.home", ".")),fileName).getAbsolutePath();
    }
}



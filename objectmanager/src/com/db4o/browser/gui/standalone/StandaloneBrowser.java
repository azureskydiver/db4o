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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import com.db4o.browser.gui.views.DbBrowserPane;

/**
 * Class StandaloneBrowser.
 * 
 * @author djo
 */
public class StandaloneBrowser extends Snippet {
    
    /* (non-Javadoc)
	 * @see com.swtworkbench.swtutils.framework.SWTSnippet#setupUI(org.eclipse.swt.widgets.Shell)
	 */
	protected void constructUI(Shell parent) {
		parent.setLayout(new FillLayout());
        ui = new DbBrowserPane(parent, SWT.NULL);
        
        buildMenuBar();
	}
    
    private DbBrowserPane ui;
    
	/**
	 * Build the application menu bar
	 */
	private void buildMenuBar() {
		Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
        
        MenuItem item = new MenuItem(fileMenu, SWT.NULL);
        item.setText("&Open...");
        item.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(shell, SWT.OPEN);
                String file = dialog.open();
                if (file != null) {
                    ui.setInput(new Model(file));
                }
            }
        });
        
        new MenuItem(fileMenu, SWT.SEPARATOR);
        item = new MenuItem(fileMenu, SWT.NULL);
        item.setText("&Exit");
        item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
                shell.close();
			}
        });
        
        Menu menuBar = new Menu(shell, SWT.BAR);
        item = new MenuItem(menuBar, SWT.CASCADE);
        item.setMenu(fileMenu);
        item.setText("&File");
        
        shell.setMenuBar(menuBar);
	}

	public static void main(String[] args) {
        new StandaloneBrowser();
	}
}

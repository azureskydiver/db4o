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

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import com.db4o.browser.gui.views.DbBrowserPane;
import com.swtworkbench.community.xswt.XSWT;

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
        Map contents = XSWT.createl(shell, "menu.xswt", getClass());
        MenuItem open = (MenuItem) contents.get("open");
        MenuItem exit = (MenuItem) contents.get("exit");
        
        open.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(shell, SWT.OPEN);
                String file = dialog.open();
                if (file != null) {
                    Model model = new Model();
                    ui.setInput(model);
                    model.open(file);
                }
            }
        });
        
        exit.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                shell.close();
            }
        });
	}

	public static void main(String[] args) {
        new StandaloneBrowser();
	}
}

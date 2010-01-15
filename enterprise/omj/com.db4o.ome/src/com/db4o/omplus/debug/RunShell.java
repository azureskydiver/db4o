/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.omplus.debug;

import org.eclipse.swt.widgets.*;

import com.db4o.omplus.*;
import com.db4o.omplus.connection.*;
import com.db4o.omplus.ui.dialog.login.*;
import com.db4o.omplus.ui.dialog.login.model.*;

public class RunShell {
	public static void main(String[] args) {
	    Display display = new Display();
	    final Shell shell = new Shell(display);
	    ErrorMessageSink err = new ErrorMessageSink() {
			public void error(String msg) {
				System.err.println(msg);
			}

			public void exc(Throwable exc) {
				exc.printStackTrace();
			}
		};
		Connector connector = new Connector() {
			public boolean connect(ConnectionParams params) throws DBConnectException {
				System.out.println(params);
				return true;
			}
		};
	    //LoginPresentationModel model = new LoginPresentationModel(new PresetRecentConnectionList(), err, connector);
	    //LoginPaneBase pane = new RemoteLoginPane(shell, shell, model);

	    LoginDialog dialog = new LoginDialog(shell, new InMemoryOMEDataStore(), connector, err);
	    dialog.open();
	    //shell.setSize(430, 230);
	    //pane.setSize(430, 230);
	    
	    shell.open();
	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch())
	        display.sleep();
	    }
	    display.dispose();

	}

}

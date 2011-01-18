/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.omplus.ui.dialog.login.debug;

import org.eclipse.swt.widgets.*;

import com.db4o.omplus.*;
import com.db4o.omplus.connection.*;
import com.db4o.omplus.debug.*;
import com.db4o.omplus.ui.dialog.login.*;
import com.db4o.omplus.ui.dialog.login.model.*;

public class ShowLoginDialogMain {
	public static void main(String[] args) {
	    Display display = new Display();
	    final Shell shell = new Shell(display);
		ErrorMessageSink errSink = new ErrorMessageSink() {
			public void showError(String msg) {
				System.err.println(msg);
			}
			
			public void showExc(String msg, Throwable exc) {
				System.err.println("ERR: " + msg);
				exc.printStackTrace();
			}

			public void logWarning(String msg, Throwable exc) {
				System.err.println("WARN: " + msg);
				exc.printStackTrace();
			}
		};
		ErrorMessageHandler err = new ErrorMessageHandler(errSink);
		Connector connector = new Connector() {
			public boolean connect(ConnectionParams params) throws DBConnectException {
				System.out.println(params);
				return true;
			}
		};

	    LoginDialog dialog = new LoginDialog(shell, new InMemoryOMEDataStore(), connector, err);
	    dialog.open();
	    
	    shell.open();
	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch())
	        display.sleep();
	    }
	    display.dispose();

	}

}

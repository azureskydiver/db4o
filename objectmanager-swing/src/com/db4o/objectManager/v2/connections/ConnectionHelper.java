package com.db4o.objectManager.v2.connections;

import java.awt.*;
import java.io.*;

import com.db4o.*;
import com.db4o.config.Configuration;
import com.db4o.config.DotnetSupport;
import com.db4o.ext.*;
import com.db4o.objectManager.v2.uiHelper.*;
import com.db4o.objectmanager.model.*;

/**
 * User: treeder
 * Date: Sep 17, 2006
 * Time: 10:42:31 PM
 */
public class ConnectionHelper {
	public static ObjectContainer connect(Component frame, Db4oConnectionSpec connectionSpec) throws Exception {
		Configuration configuration = configureDb4o();
		if (connectionSpec instanceof Db4oFileConnectionSpec) {
			try {
				// make sure file exists before opening
				File f = new File(connectionSpec.getFullPath());
				if (!f.exists() || f.isDirectory()) {
					throw new FileNotFoundException("File not found: " + f.getAbsolutePath());
				}
				return Db4o.openFile(configuration, connectionSpec.getFullPath());
			} catch (DatabaseFileLockedException e) {
				OptionPaneHelper.showErrorMessage(frame, "Database file is locked. Another process must be using it.", "Database File Locked");
				throw e;
			} catch (Db4oException e) {
				// todo: finish this up after http://tracker.db4o.com/jira/browse/COR-234 is fixed
				if (e.getMessage().contains("Old database file format detected")) { // this is bad, would be nice to have a more concrete exception
					OptionPaneHelper.showConfirmWarning(frame, "Old database file format detected. Would you like to upgrade?\n" +
							"WARNING: This operation is irreversible and your application may not operate unless you update your db4o jar file to the latest version.", "Upgrade Database?");
				} else {
					throw e;
				}
			} catch (Exception e) {
				OptionPaneHelper.showErrorMessage(frame, "Could not open database! " + e.getMessage(), "Error Opening Database");
				throw e;
			}
		} else if (connectionSpec instanceof Db4oSocketConnectionSpec) {
			Db4oSocketConnectionSpec spec = (Db4oSocketConnectionSpec) connectionSpec;
			return Db4o.openClient(configuration, spec.getHost(), spec.getPort(), spec.getUser(), spec.getPassword());
		}
		return null;
	}

	private static Configuration configureDb4o() {
		//Db4o.configure().allowVersionUpdates(true);
		//Db4o.configure().readOnly(readOnly);
		Configuration config = Db4o.newConfiguration();
		config.activationDepth(10);
		config.updateDepth(10);
		config.add(new DotnetSupport());
		return config;
	}
}

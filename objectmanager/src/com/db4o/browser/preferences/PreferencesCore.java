/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.preferences;

import java.io.File;
import java.io.IOException;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.browser.model.BrowserCore;
import com.db4o.browser.model.Database;
import com.db4o.query.Query;

public class PreferencesCore {
	private static PreferencesCore prefs = null;
	private static ObjectContainer db;

	
	private static final String preferencesFile = 
			new File(new File(System.getProperty("user.home")), ".explorer4objects.yap").getAbsolutePath();
	
	public static PreferencesCore getDefault() {
		if (prefs == null) {
			loadOrCreatePreferences();
		}
		return prefs;
	}

	public static void commit() {
		db.set(prefs);
		db.commit();
	}
	
	public static void rollback() {
		db.ext().refresh(prefs, Integer.MAX_VALUE);
	}
	
	public static void close() {
		db.close();
		db=null;
		prefs=null;
	}
	
	private static void loadOrCreatePreferences() {
		db=Db4o.openFile(preferencesFile);
		Query query=db.query();
		query.constrain(PreferencesCore.class);
		ObjectSet result=query.execute();

		if(result.hasNext()) {
			prefs=(PreferencesCore)result.next();
		} else {
			prefs = new PreferencesCore();
		}
		
		if(result.size()>1) {
			rebuildCorruptDatabase(result.size());
		}
	}

	private static void rebuildCorruptDatabase(int resultsize) {
		System.err.println(resultsize+" instances of PreferencesCore found in the database.");
		String backupFile = preferencesFile+".bkp";
		System.err.println("Backing up database to "+backupFile);
		try {
			db.ext().backup(backupFile);
		} catch (IOException e) {
			System.err.println("Couldn't create backup file.");
			e.printStackTrace();
		}
		db.close();
		new File(preferencesFile).delete();
		db=Db4o.openFile(preferencesFile);
		db.set(prefs);
		db.commit();
	}

	// --------------------------------------------------
	
	public void registerPreferencesPages() {
		PreferenceUI.registerPreferencePage("Activation", "Object Activation", null, "com.db4o.browser.preferences.ActivationPreferencePage");
	}
	
	// Constants for default preference values...
	public static final int DEFAULT_INITIAL_ACTIVATION_DEPTH = 5;
	public static final int DEFAULT_SUBSEQUENT_ACTIVATION_DEPTH = 2;
	
	private int initialActivationDepth=DEFAULT_INITIAL_ACTIVATION_DEPTH;
	private int subsequentActivationDepth=DEFAULT_SUBSEQUENT_ACTIVATION_DEPTH;

	/**
	 * @return Returns the initialActivationDepth.
	 */
	public int getInitialActivationDepth() {
		return initialActivationDepth;
	}
	

	/**
	 * @param initialActivationDepth The initialActivationDepth to set.
	 */
	public void setInitialActivationDepth(int initialActivationDepth) {
		this.initialActivationDepth = initialActivationDepth;
		Database[] databases = BrowserCore.getDefault().getAllDatabases();
		for (int i = 0; i < databases.length; i++) {
			databases[i].setInitialActivationDepth(initialActivationDepth);
		}
	}
	

	/**
	 * @return Returns the subsequentActivationDepth.
	 */
	public int getSubsequentActivationDepth() {
		return subsequentActivationDepth;
	}
	

	/**
	 * @param subsequentActivationDepth The subsequentActivationDepth to set.
	 */
	public void setSubsequentActivationDepth(int subsequentActivationDepth) {
		this.subsequentActivationDepth = subsequentActivationDepth;
	}

	
	
}

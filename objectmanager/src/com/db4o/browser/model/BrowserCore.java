/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import com.db4o.browser.gui.standalone.ICloseListener;
import com.db4o.browser.preferences.PreferenceUI;

/**
 * BrowserCore.  The root of the model hierarchy in the browser.
 *
 * @author djo
 */
public class BrowserCore implements ICloseListener {
    private static BrowserCore model = null;
    
	public static BrowserCore getDefault() {
        if (model == null) {
            model = new BrowserCore();
			registerPreferencePages();
        }
        return model;
    }
    
    private static void registerPreferencePages() {
		PreferenceUI.registerPreferencePage("Activation", "Object Activation", null, "com.db4o.browser.preferences.ActivationPreferencePage");
	}

	private LinkedList databases = new LinkedList();
    private HashMap dbMap = new HashMap();  // Maps path/filename to database
    
    /**
     * @param databasePath
     * @return
     */
    private Database getDatabase(String databasePath) {
        Database requested = (Database) dbMap.get(databasePath);
        if (requested == null) {
            requested = new Db4oDatabase();
            requested.open(databasePath);
            dbMap.put(databasePath, requested);
            databases.addLast(requested);
        }
        return requested;
    }

    /* (non-Javadoc)
	 * @see com.db4o.browser.gui.standalone.ICloseListener#closing()
	 */
	public void closing() {
		for (Iterator i = databases.iterator(); i.hasNext();) {
			Database database = (Database) i.next();
			database.close();
		}
	}
    
    /**
     * Method iterator.  Returns an IGraphIterator on the most recently opened
     * database.  Returns null if there is no open database.
     * 
     * @return IGraphIterator an iterator on the current open database; null if
     * no database is open.
     */
    public IGraphIterator iterator() {
        if (databases.isEmpty()) {
            return null;
        }
        Database current = (Database) databases.getLast();
        return current.graphIterator();
    }
    
    /**
     * Method iterator.  Returns an IGraphIterator on the specified database
     * file.  If the specified database file is not open, it will be opened.
     * 
     * @param databasePath The platform-specific path string.
     * @return IGraphIterator.  An Iterator on the contents of the specified database.
     */
    public IGraphIterator iterator(String databasePath) {
        Database requested = getDatabase(databasePath);
        return requested.graphIterator();
    }
    
    /**
     * Method iterator.  Returns an IGraphIterator on the specified class in
     * the specified database file.  If the specified database file is not 
     * open, it will be opened.
     * 
     * @param databasePath The platform-specific path string.
     * @param selectedClass The name of the class.
     * @return IGraphIterator.  An Iterator on the contents of the specified database.
     */
    public IGraphIterator iterator(String databasePath, String selectedClass) {
        Database requested = getDatabase(databasePath);
        return requested.graphIterator(selectedClass);
    }

}

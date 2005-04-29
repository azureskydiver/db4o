/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import com.db4o.browser.gui.standalone.ICloseListener;
import com.db4o.browser.prefs.PreferencesCore;

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
			PreferencesCore.initialize();
        }
        return model;
    }
    
	private LinkedList databases = new LinkedList();
    private HashMap dbMap = new HashMap();  // Maps path/filename to database
    
    /**
     * @param databasePath
     * @return the database corresponding to databasePath
     */
    public Database getDatabase(String databasePath) {
        Database requested = (Database) dbMap.get(databasePath);
        if (requested == null) {
            requested = new Db4oDatabase();
            requested.open(databasePath);
            dbMap.put(databasePath, requested);
            databases.addLast(requested);
        }
        return requested;
    }
	
    private Database getDatabase(String host, int port, String user, String password) throws Exception {
        String key = "db4o://" + host + ":" + port;
        Database requested = (Database) dbMap.get(key);
        if (requested == null) {
            requested = new Db4oDatabase();
            requested.open(host, port, user, password);
            dbMap.put(key, requested);
            databases.addLast(requested);
        }
        return requested;
    }

	/**
	 * Gets an array of all open databases
	 * 
	 * @return Database[] all open databases
	 */
	public Database[] getAllDatabases() {
		return (Database[]) dbMap.values().toArray(new Database[dbMap.size()]);
	}

    /* (non-Javadoc)
	 * @see com.db4o.browser.gui.standalone.ICloseListener#closing()
	 */
	public void closing() {
		for (Iterator i = databases.iterator(); i.hasNext();) {
			Database database = (Database) i.next();
			database.close();
		}
		PreferencesCore.close();
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


    public IGraphIterator iterator(String host, int port, String user, String password) throws Exception {
        Database requested = getDatabase(host, port, user, password);
        return requested.graphIterator();
    }
    
    /**
     * @return true if at least one database is open; false otherwise.
     */
    public boolean isOpen() {
        return databases.size() > 0;
    }

    public void updateClasspath() {
        // TODO Auto-generated method stub
        
    }

}

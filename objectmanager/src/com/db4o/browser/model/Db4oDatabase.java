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
package com.db4o.browser.model;

import com.db4o.*;
import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.browser.prefs.activation.ActivationPreferences;
import com.db4o.query.Query;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.Reflector;

/**
 * Class Database.  A wrapper for a db4o database that adds convenience
 * methods.
 * 
 * @author djo
 */
public class Db4oDatabase implements Database {
    
    
    private String currentPath="";
    
    /* (non-Javadoc)
     * @see com.db4o.browser.model.Database#open(java.lang.String)
     */
    public void open(String path) {
		Db4o.configure().activationDepth(ActivationPreferences.getDefault().getInitialActivationDepth());
        if (!path.equals(currentPath)) {
            close();
            container = Db4o.openFile(path);
            if (container == null)
                throw new IllegalArgumentException("Could not open: " + path);
            currentPath = path;
        }
    }
    
    /* (non-Javadoc)
     * @see com.db4o.browser.model.Database#close()
     */
    public void close() {
        if (container != null)
            container.close();
        container = null;
		currentPath="";
    }
	
	/* (non-Javadoc)
	 * @see com.db4o.browser.model.Database#setInitialActivationDepth(int)
	 */
	public void setInitialActivationDepth(int initialActivationDepth) {
		Db4o.configure().activationDepth(initialActivationDepth);
	}

    ObjectContainer container = null;
    

    public DatabaseGraphIterator graphIterator() {
        return new DatabaseGraphIterator(this, container.ext().knownClasses());
    }
    
    public DatabaseGraphIterator graphIterator(String name) {
        ReflectClass result = container.ext().reflector().forName(name);
        return new DatabaseGraphIterator(this, new ReflectClass[] {result});
    }
    
    public ObjectSet instances(ReflectClass clazz) {
        Query q = container.query();
        q.constrain(clazz);
        return q.execute();
    }
	
	/* (non-Javadoc)
	 * @see com.db4o.browser.model.Database#getId(java.lang.Object)
	 */
	public long getId(Object object) {
		return container.ext().getID(object);
	}
	
	/* (non-Javadoc)
	 * @see com.db4o.browser.model.Database#activate(java.lang.Object, int)
	 */
	public void activate(Object object) {
		container.activate(object, ActivationPreferences.getDefault().getSubsequentActivationDepth());
	}
    
    public Reflector reflector() {
        return container.ext().reflector();
    }
	
}

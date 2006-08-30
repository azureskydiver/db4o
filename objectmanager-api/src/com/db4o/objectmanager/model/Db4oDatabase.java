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
package com.db4o.objectmanager.model;

import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

//import org.eclipse.ve.sweet.objectviewer.IObjectViewer;
//import org.eclipse.ve.sweet.objectviewer.IObjectViewerFactory;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.objectmanager.api.prefs.ActivationPreferences;
import com.db4o.objectmanager.api.helpers.ReflectHelper;
import com.db4o.ext.StoredClass;
import com.db4o.query.Query;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.Reflector;

/**
 * Class Database.  A wrapper for a db4o database that adds convenience
 * methods.
 * 
 * @author djo
 */
public class Db4oDatabase implements IDatabase {    
    private Db4oConnectionSpec spec;
    
    public void open(Db4oConnectionSpec spec) {
        if (this.spec==null||!spec.getPath().equals(this.spec.getPath())) {
            this.spec=spec;
            reopen();
        }
    }

    public void reopen() {
        closeIfOpen();
        container = spec.connect();
        if (container == null)
            throw new IllegalArgumentException("Could not open: " + spec.getPath());
    }
    
    /* (non-Javadoc)
     * @see com.db4o.browser.model.Database#close()
     */
    public void closeIfOpen() {
        if (container != null)
            container.close();
        container = null;
    }
	
	/* (non-Javadoc)
	 * @see com.db4o.browser.model.Database#setInitialActivationDepth(int)
	 */
	public void setInitialActivationDepth(int initialActivationDepth) {
		Db4o.configure().activationDepth(initialActivationDepth);
	}

    ObjectContainer container = null;
    

    public DatabaseGraphIterator graphIterator() {

        List filteredList = ReflectHelper.getUserStoredClasses(container);

        // Return the iterator
        return new DatabaseGraphIterator(this, (ReflectClass[])filteredList.toArray(new ReflectClass[filteredList.size()]));
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
	
	public long[] instanceIds(ReflectClass clazz) {
//		StoredClass stored=container.ext().storedClass(clazz.getName());
//		if(stored!=null) {
//            return stored.getIDs();
//		}
		return instances(clazz).ext().getIDs();
	}
	
	/* (non-Javadoc)
	 * @see com.db4o.browser.model.Database#getId(java.lang.Object)
	 */
	public long getId(Object object) {
		return container.ext().getID(object);
	}
	
	public Object byId(long id) {
		Object obj=container.ext().getByID(id);
		//container.ext().deactivate(obj,Integer.MAX_VALUE);
		//container.ext().refresh(obj, Integer.MAX_VALUE);
		container.ext().activate(obj,1);
		return obj;
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
    
    public Query query() {
        return container.query();
    }

	public void delete(Object obj) {
		container.delete(obj);
	}

	public void rollback() {
		container.rollback();
	}

	public void commit() {
		container.commit();
	}

    public ObjectContainer getObjectContainer() {
        return container;
    }
}




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
import com.db4o.browser.prefs.activation.*;
import com.db4o.ext.*;
import com.db4o.query.*;
import com.db4o.reflect.db.*;
import com.db4o.reflect.jdk.*;
import com.swtworkbench.community.xswt.metalogger.*;
import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.browser.prefs.activation.ActivationPreferences;
import com.db4o.ext.ExtObjectSet;
import com.db4o.ext.StoredClass;
import com.db4o.query.Query;
import com.swtworkbench.community.xswt.metalogger.Logger;

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
		DBReflector reflector=new DBReflector();
		reflector.setDelegate(new JdkReflector(Db4o.class.getClassLoader()));
		Db4o.configure().reflectWith(reflector);
		Db4o.configure().activationDepth(ActivationPreferences.getDefault().getInitialActivationDepth());
        if (!path.equals(currentPath)) {
            close();
            container = Db4o.openFile(path);
            if (container == null)
                throw new IllegalArgumentException("Could not open: " + path);
            currentPath = path;
        }
		reflector.setDatabase(container);
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
    	return new DatabaseGraphIterator(this, container.ext().storedClasses());
    }
    
    public DatabaseGraphIterator graphIterator(String name) {
        StoredClass result = container.ext().storedClass(name);
        return new DatabaseGraphIterator(this, new StoredClass[] {result});
    }
    
    public ObjectSet instances(String clazzname) {
		StoredClass clazz=container.ext().storedClass(clazzname);
		final long[] ids=clazz.getIDs();
		return new ObjectSet() {
			private int idx=0;
			public ExtObjectSet ext() {
				throw new UnsupportedOperationException("TODO: Implement me");
			}

			public boolean hasNext() {
				return idx<ids.length;
			}

			public Object next() {
				if(!hasNext()) {
					return null;
				}
				Object next=container.ext().getByID(ids[idx]);
				idx++;
				return next;
			}

			public void reset() {
				idx=0;
			}

			public int size() {
				return ids.length;
			}
			
		};
//        Query q = container.query();
////        IClass toReturn = null;
////        try {
////            toReturn = CReflect.getDefault().forName(clazz);
////        }
//        Class toReturn = null;
//        try {
//            toReturn = Class.forName(clazz);
//        }
//        catch (Exception e) {
//            Logger.log().error(e, "Unable to Class.forName()");
//            throw new RuntimeException();
//        }
//        return container.get(toReturn);
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
    
}

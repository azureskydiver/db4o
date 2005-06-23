package com.db4o.browser.model.test;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import com.db4o.ObjectSet;
import com.db4o.binding.dataeditors.*;
import com.db4o.browser.model.*;
import com.db4o.ext.StoredClass;
import com.db4o.query.Query;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.Reflector;

public class MockDatabase extends Assert implements IDatabase {
	private boolean opened=false;
	/** (String)name -> (StoredClass)class */
	private Map storedClasses=new HashMap();
	/** (String)stored class name -> (Object[])instances */
	private Map instances=new HashMap();
	
	public void open(String path) {
		assertFalse("already opened",opened);
		opened=true;
	}

	public void closeIfOpen() {
		assertTrue("not opened",opened);
		opened=false;
	}

	public void setInitialActivationDepth(int initialActivationDepth) {
		// Nothing else needed here...
	}
	
	public DatabaseGraphIterator graphIterator() {
		StoredClass[] stored = (StoredClass[])storedClasses.values().toArray(new StoredClass[storedClasses.size()]);
		// return new DatabaseGraphIterator(this,stored);
        throw new RuntimeException("FIXME");
	}

	public DatabaseGraphIterator graphIterator(String name) {
		StoredClass storedclass=(StoredClass)storedClasses.get(name);
		StoredClass[] stored=(storedclass==null ? new StoredClass[0] : new StoredClass[]{storedclass});
		// return new DatabaseGraphIterator(this,stored);
        throw new RuntimeException("FIXME");
	}

	public ObjectSet instances(ReflectClass clazz) {
		return new MockObjectSet((Object[])instances.get(clazz));
	}
	
	public void verify() {
		assertFalse("database was not closed",opened);
	}
	
	public void add(StoredClass storedClass,Object[] instances) {
		storedClasses.put(storedClass.getName(),storedClass);
		this.instances.put(storedClass.getName(),instances);
	}
	
	public long getId(Object object) {
		return System.identityHashCode(object);
	}
	
	
	public void activate(Object object) {
		// Nothing to do here; everything is always active in the mock database
	}

    public Object byId(long id) {
        // TODO Auto-generated method stub
        return null;
    }

    public long[] instanceIds(ReflectClass clazz) {
        // TODO Auto-generated method stub
        return null;
    }

    public Reflector reflector() {
        // TODO Auto-generated method stub
        return null;
    }

    public Query query() {
        // TODO Auto-generated method stub
        return null;
    }

    public void open(Db4oConnectionSpec spec) {
        // TODO Auto-generated method stub
        
    }

    public void reopen() {
        // TODO Auto-generated method stub
        
    }

    public IObjectEditor construct() {
        // TODO Auto-generated method stub
        return null;
    }

    public IObjectEditor construct(Object toEdit) {
        // TODO Auto-generated method stub
        return null;
    }
}

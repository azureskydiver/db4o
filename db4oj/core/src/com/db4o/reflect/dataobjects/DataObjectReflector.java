package com.db4o.reflect.dataobjects;

import com.db4o.*;
import com.db4o.reflect.*;

public class DataObjectReflector implements IReflect {

    private final Hashtable4 _iClassByNativeClass = new Hashtable4(1);

    public IArray array() {
        return null;
    }

    public boolean constructorCallsSupported() {
        return false;
    }

    public IClass forName(String className) throws ClassNotFoundException {
        return new DataObjectClass(className);
    }

    public IClass forClass(Class clazz) {
        IClass result = (IClass)_iClassByNativeClass.get(clazz);
        if (result == null) {
            //result = new DataObjectClass(clazz);
            _iClassByNativeClass.put(clazz, result);
        }
        return result;
    }

    public IClass forObject(Object a_object) {
        return null;
    }

	/* (non-Javadoc)
	 * @see com.db4o.reflect.IReflect#isCollection(com.db4o.reflect.IClass)
	 */
	public boolean isCollection(IClass claxx) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.db4o.reflect.IReflect#registerCollection(java.lang.Class)
	 */
	public void registerCollection(Class clazz) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.db4o.reflect.IReflect#registerCollectionUpdateDepth(java.lang.Class, int)
	 */
	public void registerCollectionUpdateDepth(Class clazz, int depth) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.db4o.reflect.IReflect#collectionUpdateDepth(com.db4o.reflect.IClass)
	 */
	public int collectionUpdateDepth(IClass claxx) {
		// TODO Auto-generated method stub
		return 0;
	}

}

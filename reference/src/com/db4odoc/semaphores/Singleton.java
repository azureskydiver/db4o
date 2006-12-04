/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.f1.semaphores;

import com.db4o.*;
import com.db4o.query.*;

/**
 * This class demonstrates the use of a semaphore to ensure that only
 * one instance of a certain class is stored to an ObjectContainer.
 * 
 * Caution !!! The getSingleton method contains a commit() call.  
 */
public class Singleton {
	
	/**
	 * returns a singleton object of one class for an ObjectContainer.
	 * <br><b>Caution !!! This method contains a commit() call.</b> 
	 */
    public static Object getSingleton(ObjectContainer objectContainer, Class clazz) {

        Object obj = queryForSingletonClass(objectContainer, clazz);
        if (obj != null) {
            return obj;
        }

        String semaphore = "Singleton#getSingleton_" + clazz.getName();

        if (!objectContainer.ext().setSemaphore(semaphore, 10000)) {
            throw new RuntimeException("Blocked semaphore " + semaphore);
        }

        obj = queryForSingletonClass(objectContainer, clazz);

        if (obj == null) {

            try {
                obj = clazz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            objectContainer.set(obj);

            /* !!! CAUTION !!!
             * There is a commit call here.
             * 
             * The commit call is necessary, so other transactions
             * can see the new inserted object.
             */
            objectContainer.commit();

        }

        objectContainer.ext().releaseSemaphore(semaphore);

        return obj;
    }

    private static Object queryForSingletonClass(ObjectContainer objectContainer, Class clazz) {
        Query q = objectContainer.query();
        q.constrain(clazz);
        ObjectSet objectSet = q.execute();
        if (objectSet.size() == 1) {
            return objectSet.next();
        }
        if (objectSet.size() > 1) {
            throw new RuntimeException(
                "Singleton problem. Multiple instances of: " + clazz.getName());
        }
        return null;
    }

}

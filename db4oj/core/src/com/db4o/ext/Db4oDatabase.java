/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.ext;

import com.db4o.*;
import com.db4o.types.*;


/**
 * Class to identify a database by it's signature.
 * <br><br>db4o UUID handling uses a reference to the Db4oDatabase object, that
 * represents the database an object was created on.  
 */
public class Db4oDatabase implements Db4oType{

    /**
     * Field is public for implementation reasons, DO NOT TOUCH!
     */
    public byte[] i_signature;
    
    /**
     * Field is public for implementation reasons, DO NOT TOUCH!
     */
    public long i_uuid;
    
    /**
     * cached ObjectContainer for getting the own ID.
     */
    private transient ExtObjectContainer i_objectContainer;
    
    /**
     * cached ID, only valid in combination with i_objectContainer
     */
    private transient int i_id; 
    
    
    /**
     * generates a new Db4oDatabase object with a unique signature.
     */
    public static Db4oDatabase generate() {
        Db4oDatabase db = new Db4oDatabase();
        db.i_signature = Unobfuscated.generateSignature();
        db.i_uuid = System.currentTimeMillis();
        return db;
    }
    
    /**
     * comparison by signature.
     */
    public boolean equals(Object obj) {
    	if(obj==this) {
    		return true;
    	}
    	if(obj==null||this.getClass()!=obj.getClass()) {
    		return false;
    	}
        Db4oDatabase other = (Db4oDatabase)obj;
        // FIXME: Should never happen?
        if(other.i_signature==null||this.i_signature==null) {
        	return false;
        }
        if(other.i_signature.length != i_signature.length) {
        	return false;
        }
        for (int i = 0; i < i_signature.length; i++) {
			if (i_signature[i] != other.i_signature[i]) {
				return false;
			}
		}
		return true;
    }
    
    /**
	 * gets the db4o ID, and may cache it for performance reasons.
	 * 
	 * @param a_oc
	 *            the ObjectContainer
	 * @return the db4o ID for the ObjectContainer
	 */
    public int getID(ExtObjectContainer a_oc) {
        if(a_oc != i_objectContainer) {
            i_objectContainer = a_oc;
            i_id = (int)a_oc.getID(this);
            if(i_id == 0){
                a_oc.set(this);
                i_id = (int)a_oc.getID(this);
            }
        }
        return i_id;
    }
    
    public String toString(){
        return "Db4oDatabase: " + i_signature;
    }
    
}
/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.ids;

import com.db4o.internal.*;

/**
 * @exclude
 */
public class GlobalIdSystemFactory {
	
	public static final byte LEGACY = 0;
	
	public static final byte POINTER_BASED = 1;
	
	public static final byte DEFAULT = POINTER_BASED;
	
	public static final byte BTREE = 2;

	public static GlobalIdSystem createNew(LocalObjectContainer localContainer) {
		byte idSystemType = localContainer.systemData().idSystemType();
		int idSystemId = localContainer.systemData().idSystemID();
		
        switch(idSystemType){
	    	case LEGACY:
	    		return new PointerBasedIdSystem(localContainer);
	    	case POINTER_BASED:
	    		return new PointerBasedIdSystem(localContainer);
	    	case BTREE:
	    		
	        default:
	        	return new PointerBasedIdSystem(localContainer);
        }
	            
    }
	

}

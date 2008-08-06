/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.typehandlers;

import com.db4o.internal.activation.*;
import com.db4o.internal.marshall.*;


/**
 * TypeHandler for objects with own identity that support
 * activation and querying on members.
 */
public interface FirstClassHandler extends TypeHandler4{
    
	/**
	 * will be called during activation if the handled
	 * object is already active 
	 * @param context
	 */
    void cascadeActivation(ActivationContext4 context);
    
    /**
     * will be called during querying to ask for the handler
     * to be used to collect children of the handled object
     * @param context
     * @return
     */
    TypeHandler4 readCandidateHandler(QueryingReadContext context);
    
    /**
     * will be called during querying to ask for IDs of member
     * objects of the handled object.
     * @param context
     */
    public void collectIDs(QueryingReadContext context);

}

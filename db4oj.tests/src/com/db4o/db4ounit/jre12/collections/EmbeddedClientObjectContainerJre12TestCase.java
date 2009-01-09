/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections;

import com.db4o.db4ounit.common.internal.*;
import com.db4o.types.*;

import db4ounit.*;


/**
 * @exclude
 */
/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class EmbeddedClientObjectContainerJre12TestCase extends EmbeddedClientObjectContainerTestCase{
    
	/**
	 * @deprecated using deprecated api
	 */
    public void testCollections(){
        Db4oList list = _client1.collections().newLinkedList();
        Assert.isNotNull(list);
        
        Assert.isTrue(_client1.isStored(list));
        
    }
    

}

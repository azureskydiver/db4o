package com.db4o.db4ounit.jre12.collections;

import java.util.*;

import com.db4o.config.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class CollectionUuidTest extends AbstractDb4oTestCase {	
	
	/**
	 * @deprecated using deprecated api
	 */
	protected void configure(Configuration config) {
		config.generateUUIDs(Integer.MAX_VALUE);
	}
	
	public void test() {
		ArrayList list = new ArrayList();
		db().store(list);
		Assert.isNotNull(db().getObjectInfo(list).getUUID());
	}
	
	public static void main(String[] args) {
        new CollectionUuidTest().runClientServer();
    }
}

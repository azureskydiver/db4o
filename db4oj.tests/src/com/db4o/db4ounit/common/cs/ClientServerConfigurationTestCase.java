/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.cs;

import com.db4o.config.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * @sharpen.ignore
 */
public class ClientServerConfigurationTestCase extends AbstractDb4oTestCase{
	
	protected void configure(Configuration config) throws Exception {
		// Just make sure no exception is thrown when
		// Class.forName() runs in DotNetSupport. 
		config.add(new DotnetSupport(true));
	}
	
	public void testDotNetSupport(){
		// For now: Just make sure a database file is opened.
		Assert.isTrue(true);
	}

}

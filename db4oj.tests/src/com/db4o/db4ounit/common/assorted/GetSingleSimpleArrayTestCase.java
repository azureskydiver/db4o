/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import com.db4o.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class GetSingleSimpleArrayTestCase extends AbstractDb4oTestCase{
	
	public void test(){
        final ObjectSet result=db().get(new double[]{0.6,0.4});
        Assert.isFalse(result.hasNext());
        Assert.isFalse(result.hasNext());
        Assert.expect(IllegalStateException.class, new CodeBlock() {
			public void run() throws Exception {
				result.next();		
			}
		});
	}
	
}

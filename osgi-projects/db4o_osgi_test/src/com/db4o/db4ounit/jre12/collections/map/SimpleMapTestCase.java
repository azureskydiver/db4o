/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
package com.db4o.db4ounit.jre12.collections.map;

import com.db4o.config.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class SimpleMapTestCase extends AbstractDb4oTestCase{
	
	protected void configure(Configuration config) {
		config.generateUUIDs(Integer.MAX_VALUE);
	}

	public static void main(String[] args) {
        new SimpleMapTestCase().runClientServer();
    }
	
	public void testGetByUUID() {
		MapContent c1 = new MapContent("c1");
		db().set(c1);	//comment me bypass the bug

		//db().getObjectInfo(c1).getUUID();	//Uncomment me bypass the bug

		MapHolder mh = new MapHolder("h1");
		mh.map.put("key1", c1);

		db().set(mh);	//comment me bypass the bug

		Db4oUUID uuid = db().getObjectInfo(c1).getUUID();

		Assert.isNotNull(db().getByUUID(uuid));	//This line fails when Test.clientServer = true;
	}
}

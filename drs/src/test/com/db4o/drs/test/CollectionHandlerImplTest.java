/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com

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
package com.db4o.drs.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.db4o.drs.inside.CollectionHandlerImpl;
import com.db4o.drs.inside.ReplicationReflector;
import com.db4o.reflect.Reflector;
import db4ounit.Assert;

public class CollectionHandlerImplTest extends DrsTestCase {

	private final Reflector _reflector = ReplicationReflector.getInstance().reflector();
	private final CollectionHandlerImpl _collectionHandler = new CollectionHandlerImpl();

	public void testVector() {
		Vector vector = new Vector();
		Assert.isTrue(_collectionHandler.canHandle(vector));
		Assert.isTrue(_collectionHandler.canHandle(_reflector.forObject(vector)));
		Assert.isTrue(_collectionHandler.canHandle(Vector.class));
	}

	public void testList() {
		List list = new LinkedList();
		Assert.isTrue(_collectionHandler.canHandle(list));
		Assert.isTrue(_collectionHandler.canHandle(_reflector.forObject(list)));
		Assert.isTrue(_collectionHandler.canHandle(List.class));
	}
	
	public void testSet() {
		Set set = new HashSet();
		Assert.isTrue(_collectionHandler.canHandle(set));
		Assert.isTrue(_collectionHandler.canHandle(_reflector.forObject(set)));
		Assert.isTrue(_collectionHandler.canHandle(Set.class));
	}

	public void testMap() {
		Map map = new HashMap();
		Assert.isTrue(_collectionHandler.canHandle(map));
		Assert.isTrue(_collectionHandler.canHandle(_reflector.forObject(map)));
		Assert.isTrue(_collectionHandler.canHandle(Map.class));
	}

	public void testString() {
		String str = "abc";
		Assert.isTrue(!_collectionHandler.canHandle(str));
		Assert.isTrue(!_collectionHandler.canHandle(_reflector.forObject(str)));
		Assert.isTrue(!_collectionHandler.canHandle(String.class));
	}

}

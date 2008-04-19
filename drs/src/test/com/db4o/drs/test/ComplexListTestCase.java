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

import java.util.List;
import com.db4o.drs.inside.TestableReplicationProviderInside;

import db4ounit.Assert;

@SuppressWarnings("unchecked")
public class ComplexListTestCase extends DrsTestCase {
	public void test() {
		
		store(a(), createList());
		
		replicateAndTest(a(), b());
		
		roundTripTest();
		
	}
	
	private void roundTripTest() {
		changeInProviderB();
		
		replicateAndTest(b(), a());
	}

	private void changeInProviderB() {
		SimpleListHolder SimpleListHolder = (SimpleListHolder) getOneInstance(b(), SimpleListHolder.class);
		
		SimpleItem fooBaby = new SimpleItem(SimpleListHolder, "foobaby");		
		b().provider().storeNew(fooBaby);
		SimpleListHolder.add(fooBaby);		
		SimpleItem foo = getItem(SimpleListHolder, "foo");
		foo.setChild(fooBaby);
		b().provider().update(foo);
		b().provider().update(SimpleListHolder);
	}

	private void replicateAndTest(DrsFixture source, DrsFixture target) {
		replicateAll(source.provider(), target.provider());
		ensureContents(target, (SimpleListHolder) getOneInstance(source, SimpleListHolder.class));
	}

	private void store(DrsFixture fixture , SimpleListHolder list) {
		TestableReplicationProviderInside provider = fixture.provider();
		
		provider.storeNew(list);
		
		provider.storeNew(getItem(list, "foo"));
		provider.storeNew(getItem(list, "foobar"));		
		
		provider.commit();
		
		ensureContents(fixture, list);
	}

	private void ensureContents(DrsFixture actualFixture, SimpleListHolder expected) {
		SimpleListHolder actual = (SimpleListHolder) getOneInstance(actualFixture, SimpleListHolder.class);
		
		List expectedList = expected.getList();
		List actualList = actual.getList();
		
		assertListWithCycles(expectedList, actualList);
	}

	private void assertListWithCycles(List expectedList, List actualList) {
		Assert.areEqual(expectedList.size(), actualList.size());
		
		for(int i = 0; i < expectedList.size(); ++i){
			SimpleItem expected = (SimpleItem) expectedList.get(i);
			SimpleItem actual = (SimpleItem) actualList.get(i);
			
			assertItem(expected, actual);
		}
		
		assertCycle(actualList, "foo", "bar", 1);
		assertCycle(actualList, "foo", "foobar", 1);
		assertCycle(actualList, "foo", "baz", 2);		
	}

	private void assertCycle(List list, String childName, String parentName, int level) {
		SimpleItem foo = getItem(list, childName);
		SimpleItem bar = getItem(list, parentName);
		
		Assert.isNotNull(foo);
		Assert.isNotNull(bar);
		
		Assert.areSame(foo, bar.getChild(level));
		Assert.areSame(foo.getParent(), bar.getParent());
	}

	private void assertItem(SimpleItem expected, SimpleItem actual) {
		if (expected == null) {
			Assert.isNull(actual);
			return;
		}
		
		Assert.areEqual(expected.getValue(), actual.getValue());
		assertItem(expected.getChild(), actual.getChild());
	}

	private SimpleItem getItem(SimpleListHolder holder, String tbf) {
		return getItem(holder.getList(), tbf);
	}
	
	private SimpleItem getItem(List list, String tbf) {
		int itemIndex = list.indexOf(new SimpleItem(tbf));		
		return (SimpleItem) (itemIndex >= 0 ? list.get(itemIndex) : null); 
	}

	public SimpleListHolder createList() {
		
		// list : {foo, bar, baz, foobar}
		//
		// baz -----+
		//          |
		//         bar --> foo
		//                  ^
		//                  |
		// foobar ----------+
		
		SimpleListHolder listHolder = new SimpleListHolder();
		
		SimpleItem foo = new SimpleItem(listHolder, "foo");
		SimpleItem bar = new SimpleItem(listHolder, "bar", foo);
		listHolder.add(foo);
		listHolder.add(bar);
		listHolder.add(new SimpleItem(listHolder, "baz", bar));
		listHolder.add(new SimpleItem(listHolder, "foobar", foo));
		
		return listHolder;
	}
}

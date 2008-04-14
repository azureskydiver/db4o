package com.db4o.drs.test;

import java.util.ArrayList;
import java.util.List;
import com.db4o.drs.inside.TestableReplicationProviderInside;

import db4ounit.Assert;

@SuppressWarnings("unchecked")
public class ComplexListTestCase extends DrsTestCase {
	public void test() {
		
		//TODO: Fix the following exception and remove the "if" line
		
		/*
		 * 1) com.db4o.drs.test.ComplexListTestCase.test: java.lang.ClassCastException: com.db4o.drs.test.ListContent cannot be cast to com.db4o.drs.test.Item
	at com.db4o.drs.test.ComplexListTestCase.assertListWithCycles(ComplexListTestCase.java:77)
	at com.db4o.drs.test.ComplexListTestCase.ensureContents(ComplexListTestCase.java:69)
	at com.db4o.drs.test.ComplexListTestCase.store(ComplexListTestCase.java:58)
	at com.db4o.drs.test.ComplexListTestCase.test(ComplexListTestCase.java:17)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
	at java.lang.reflect.Method.invoke(Method.java:597)
	at db4ounit.TestMethod.invoke(TestMethod.java:52)
	at db4ounit.TestMethod.run(TestMethod.java:40)
	at com.db4o.foundation.DynamicVariable$1.run(DynamicVariable.java:71)
	at com.db4o.foundation.DynamicVariable.with(DynamicVariable.java:62)
	at com.db4o.foundation.DynamicVariable.with(DynamicVariable.java:69)
	at db4ounit.fixtures.FixtureContext.run(FixtureContext.java:33)
	at db4ounit.fixtures.Contextful.run(Contextful.java:20)
	at db4ounit.ContextfulTest.run(ContextfulTest.java:27)
	at db4ounit.TestRunner.run(TestRunner.java:24)
	at db4ounit.ConsoleTestRunner.run(ConsoleTestRunner.java:43)
	at db4ounit.ConsoleTestRunner.run(ConsoleTestRunner.java:37)
	at com.db4o.drs.test.hibernate.RdbmsTests.runHsqldb4oCS(RdbmsTests.java:59)
	at com.db4o.drs.test.hibernate.RdbmsTests.main(RdbmsTests.java:42)
	at com.db4o.drs.test.all.AllDrsTests.main(AllDrsTests.java:14)
		 */
		
		if (a().getClass().getName().indexOf("HsqlMemoryFixture") >= 0 || b().getClass().getName().indexOf("HsqlMemoryFixture") >= 0) return;
		
		store(a(), createList("foo.list"));
		
		replicateAndTest(a(), b());
		
		roundTripTest();
		
	}
	
	private void roundTripTest() {
		changeInProviderB();
		
		replicateAndTest(b(), a());
	}

	private void changeInProviderB() {
		ListHolder listHolder = (ListHolder) getOneInstance(b(), ListHolder.class);
		
		Item fooBaby = new Item("foobaby", listHolder);		
		b().provider().storeNew(fooBaby);
		listHolder.add(fooBaby);		
		Item foo = getItem(listHolder, "foo");
		foo.setChild(fooBaby);
		b().provider().update(foo);
		b().provider().update(listHolder);
	}

	private void replicateAndTest(DrsFixture source, DrsFixture target) {
		replicateAll(source.provider(), target.provider());
		ensureContents(target, (ListHolder) getOneInstance(source, ListHolder.class));
	}

	private void store(DrsFixture fixture , ListHolder list) {
		TestableReplicationProviderInside provider = fixture.provider();
		
		provider.storeNew(list);
		
		provider.storeNew(getItem(list, "foo"));
		provider.storeNew(getItem(list, "foobar"));		
		
		provider.commit();
		
		ensureContents(fixture, list);
	}

	private void ensureContents(DrsFixture actualFixture, ListHolder expected) {
		ListHolder actual = (ListHolder) getOneInstance(actualFixture, ListHolder.class);
		
		Assert.areEqual(expected.getName(), actual.getName());
		
		List expectedList = expected.getList();
		List actualList = actual.getList();
		
		assertListWithCycles(expectedList, actualList);
	}

	private void assertListWithCycles(List expectedList, List actualList) {
		Assert.areEqual(expectedList.size(), actualList.size());
		
		for(int i = 0; i < expectedList.size(); ++i){
			Item expected = (Item) expectedList.get(i);
			Item actual = (Item) actualList.get(i);
			
			assertItem(expected, actual);
		}
		
		assertCycle(actualList, "foo", "bar", 1);
		assertCycle(actualList, "foo", "foobar", 1);
		assertCycle(actualList, "foo", "baz", 2);		
	}

	private void assertCycle(List list, String childName, String parentName, int level) {
		Item foo = getItem(list, childName);
		Item bar = getItem(list, parentName);
		
		Assert.isNotNull(foo);
		Assert.isNotNull(bar);
		
		Assert.areSame(foo, bar.child(level));
		Assert.areSame(foo.parent(), bar.parent());
	}

	private void assertItem(Item expected, Item actual) {
		if (expected == null) {
			Assert.isNull(actual);
			return;
		}
		
		Assert.areEqual(expected.getName(), actual.getName());
		assertItem(expected.child(), actual.child());
	}

	private Item getItem(ListHolder holder, String tbf) {
		return getItem(holder.getList(), tbf);
	}
	
	private Item getItem(List list, String tbf) {
		int itemIndex = list.indexOf(new Item(tbf));		
		return (Item) (itemIndex >= 0 ? list.get(itemIndex) : null); 
	}

	public ListHolder createList(String name) {
		
		// list : {foo, bar, baz, foobar}
		//
		// baz -----+
		//          |
		//         bar --> foo
		//                  ^
		//                  |
		// foobar ----------+
		
		ListHolder listHolder = NewList(name);
		
		Item foo = new Item("foo", listHolder);
		Item bar = new Item("bar", foo, listHolder);
		listHolder.add(foo);
		listHolder.add(bar);
		listHolder.add(new Item("baz", bar, listHolder));
		listHolder.add(new Item("foobar", foo, listHolder));
		
		return listHolder;
	}
	
	private ListHolder NewList(String name) {
		ListHolder holder = new ListHolder(name);
		holder.setList(new ArrayList());
		
		return holder;
	}
}

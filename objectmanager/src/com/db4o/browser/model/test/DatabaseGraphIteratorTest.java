/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.model.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.db4o.browser.model.DatabaseGraphIterator;
import com.db4o.browser.model.GraphPathNode;
import com.db4o.browser.model.GraphPosition;
import com.db4o.browser.model.nodes.IModelNode;

public class DatabaseGraphIteratorTest extends TestCase {
	
	private MockDatabase database=null;

	protected void setUp() {
		database=new MockDatabase();
	}
	
	public void testEmptyDatabase() {
		DatabaseGraphIterator graphiter=database.graphIterator();
		assertFalse(graphiter.hasNext());
		GraphPosition path=graphiter.getPath();
		IModelNode current=path.getCurrent();
	}

	private static class PrimitiveHolder {
		private int i;

		public PrimitiveHolder(int i) {
			this.i = i;
		}
	}

	public void testSinglePrimitiveHolder() {
		PrimitiveHolder data=new PrimitiveHolder(42);
		assertSingleHolder(PrimitiveHolder.class,"i",data,"i: "+data.i,String.valueOf(data.i),0);
	}
	
	private static class CollectionHolder {
		private List list;

		public CollectionHolder(List list) {
			this.list = list;
		}
	}

	public void testSingleListHolder() {
		List list=new ArrayList();
		list.add("one");
		list.add(new Integer(42));
		CollectionHolder data=new CollectionHolder(list);
		IModelNode fieldnode=assertSingleHolder(CollectionHolder.class,"list",data,"list: interface "+List.class.getName(),null,2);
		for(int idx=0;idx<fieldnode.children().length;idx++) {
			// TODO: String shows members in instance node
			// assertNodeContent(fieldnode.children()[idx],"",list.get(idx).toString(),list.get(idx).toString(),0);
			assertNodeContent(fieldnode.children()[idx],"",list.get(idx).toString(),list.get(idx).toString(),-1);
		}
	}

	public void testTwoClasses() {
		PrimitiveHolder pdata=new PrimitiveHolder(42);
		List list=new ArrayList();
		list.add("one");
		list.add(new Integer(42));
		CollectionHolder cdata=new CollectionHolder(list);
		database.add(new MockStoredClass(PrimitiveHolder.class), new Object[] {pdata});
		database.add(new MockStoredClass(CollectionHolder.class), new Object[] {cdata});
		DatabaseGraphIterator iter=database.graphIterator();
		assertEquals(0,iter.nextIndex());
		GraphPosition path=iter.getPath();
		GraphPathNode node=path.pop();
		assertEquals(-1, node.selectedChild);
		assertFalse(path.iterator().hasNext());
		iter.selectNextChild();
		assertEquals(0,iter.nextIndex());
		
		path=iter.getPath();
		node=path.pop();
		assertEquals(-1, node.selectedChild);
		node=path.pop();
		assertEquals(-1, node.selectedChild);
		
		iter.selectNextChild();
		assertEquals(0,iter.nextIndex());		
		iter.selectParent();
		assertEquals(0,iter.nextIndex());		
	}
	
	private IModelNode assertSingleHolder(Class clazz,String fieldname,Object data,String text,String valuetext,int numchildren) {
		database.add(new MockStoredClass(clazz),new Object[]{data});
		DatabaseGraphIterator graphiter=database.graphIterator();
		assertTrue(graphiter.hasNext());
		GraphPosition path=graphiter.getPath();		
		
		IModelNode classnode=path.getCurrent();
		assertNodeContent(classnode,"",clazz.getName(),clazz.getName(),1);
		IModelNode instancenode=classnode.children()[0];
		assertNodeContent(instancenode,"",data.toString(),data.toString(),1);
		IModelNode fieldnode=instancenode.children()[0];
		assertNodeContent(fieldnode,fieldname,text,valuetext,numchildren);	
		
		assertTrue(graphiter.hasNext());
		Object current=graphiter.next();
		assertEquals(classnode,current);
		assertFalse(graphiter.hasNext());
		graphiter.previous();
		assertTrue(graphiter.hasNext());
		graphiter.selectNextChild();
		current=graphiter.next();
		assertEquals(instancenode,current);
		graphiter.previous();
		graphiter.selectNextChild();
		current=graphiter.next();
		assertEquals(fieldnode,current);
		assertFalse(graphiter.hasNext());
		graphiter.previous();
		assertTrue(graphiter.hasParent());
		graphiter.selectParent();
		current=graphiter.next();
		assertEquals(instancenode,current);

		path=graphiter.getPath();		
		graphiter.setSelectedPath(path);
		graphiter.setSelectedPath(path);
		GraphPosition returned=graphiter.getPath();
		assertEquals(path.size(),returned.size());
		return fieldnode;
	}

	private void assertNodeContent(IModelNode node,String name,String text,String valueText,int numChildren) {
		assertEquals(name,node.getName());
		assertEquals(text,node.getText());
		if(valueText!=null) {
			assertEquals(valueText,node.getValueString());
		}
		if(numChildren>-1) {
			assertEquals(numChildren,node.children().length);
			assertEquals(node.children().length>0,node.hasChildren());
		}
	}

	public void testEmployees() {
		Employee frank = new Employee("Frank");
		Employee joe = new Employee("Joe");
		Employee chet = new Employee("Chet");
		Employee laurel = new Employee("Laurel");
		Employee johndoe = new Employee("John Doe");
		Employee george = new Employee("George Vancouver", new Employee[] {
						frank,
						joe,
						chet
				});
		Employee mary = new Employee("Typhoid Mary", new Employee[] {
				johndoe,
				george,
				laurel,
				frank
		});
		Object[] instances={frank,joe,chet,laurel,johndoe,george,mary};
		database.add(new MockStoredClass(Employee.class),instances);
		DatabaseGraphIterator graphiter=database.graphIterator();
		assertTrue(graphiter.hasNext());
		GraphPosition path=graphiter.getPath();
		IModelNode classnode=path.getCurrent();
		assertNodeContent(classnode,"",Employee.class.getName(),Employee.class.getName(),instances.length);
	}
	
	private static class Employee {
		private String name;
		private Employee[] subordinates;

		public Employee(String name) {
			this.name = name;
		}
		
		public Employee(String name, Employee[] reportsTo) {
			this.name = name;
			this.subordinates = reportsTo;
		}
	}

	public static void main(String[] args) {
		DatabaseGraphIteratorTest test=new DatabaseGraphIteratorTest();
		test.setUp();
		test.testSinglePrimitiveHolder();
	}
}

/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.qlin;

import java.util.*;

import static com.db4o.qlin.QLinSupport.*;

import db4ounit.*;
import db4ounit.extensions.*;

@decaf.Remove(decaf.Platform.JDK11)
public class BasicQLinTestCase extends AbstractDb4oTestCase {
	
	public static class Cat {
		
		public int age;
		
		public String name;
		
		public Cat spouse;
		
		public Cat father;
		
		public Cat mother;
		
		public Cat(String name){
			this.name = name;
		}
		
		public Cat(String name, int age){
			this(name);
			this.age = age;
		}
		
		public String name(){
			return name;
		}
		
		public void spouse(Cat spouse){
			this.spouse = spouse;
			spouse.spouse = this;
		}

		@Override
		public boolean equals(Object obj) {
			if(! (obj instanceof Cat)){
				return false;
			}
			Cat other = (Cat) obj;
			if (name == null) {
				return other.name == null;
			}
			return name.equals(other.name);
		}
		
		public int age(){
			return age;
		}
		
	}
	
	public void testFromSelect(){
		storeAll(occamAndZora());
		IteratorAssert.sameContent(occamAndZora(), db().from(Cat.class).select());
	}
	
	public void testWhereFieldNameAsString(){
		storeAll(occamAndZora());
		IteratorAssert.sameContent(occam(), 
				db().from(Cat.class)
					.where("name")
					.equal("Occam")
					.select());
	}
	
	public void testWherePrototypeFieldIsString(){
		storeAll(occamAndZora());
		IteratorAssert.sameContent(occam(), 
				db().from(Cat.class)
					.where(p(Cat.class).name())
					.equal("Occam")
					.select());
	}
	
	public void testWherePrototypeFieldStartsWith(){
		storeAll(occamAndZora());
		IteratorAssert.sameContent(occam(), 
				db().from(Cat.class)
					.where(p(Cat.class).name())
					.startsWith("Occ")
					.select());
	}
	
	public void testField(){
		storeAll(occamAndZora());
		IteratorAssert.sameContent(occam(), 
				db().from(Cat.class)
					.where(field("name"))
					.equal("Occam")
					.select());
	}
	
	public void testWherePrototypeFieldIsPrimitiveInt(){
		storeAll(occamAndZora());
		IteratorAssert.sameContent(occam(), 
				db().from(Cat.class)
					.where(p(Cat.class).age)
					.equal(7)
					.select());
	}
	
	public void testWherePrototypeFieldIsSmaller(){
		storeAll(occamAndZora());
		IteratorAssert.sameContent(zora(), 
				db().from(Cat.class)
					.where(p(Cat.class).age)
					.smaller(7)
					.select());
	}
	
	public void testWherePrototypeFieldIsGreater(){
		storeAll(occamAndZora());
		IteratorAssert.sameContent(occamAndZora(), 
				db().from(Cat.class)
					.where(p(Cat.class).age)
					.greater(5)
					.select());
	}
	
	public void testLimit(){
		storeAll(occamAndZora());
		Assert.areEqual(1,
				db().from(Cat.class)
					.limit(1)
					.select()
					.size());
	}
	
	public void testPredefinedPrototype(){
		storeAll(occamAndZora());
		Cat cat = prototype(Cat.class);
		IteratorAssert.sameContent(occam(), 
				db().from(Cat.class)
					.where(cat.name())
					.startsWith("Occ")
					.select());
	}

	
	private void storeAll(List expected) {
		for (Object obj : expected) {
			store(obj);
		}
	}

	private List<Cat> occamAndZora() {
		List<Cat> list = new ArrayList<Cat>();
		Cat occam = new Cat("Occam", 7);
		Cat zora = new Cat("Zora", 6);
		occam.spouse(zora);
		list.add(occam);
		list.add(zora);
		return list;
	}
	
	private List<Cat> occam() {
		return singleCat("Occam");
	}

	private List<Cat> singleCat(String name) {
		List<Cat> list = new ArrayList<Cat>();
		list.add(new Cat(name));
		return list;
	}
	
	private List<Cat> zora() {
		return singleCat("Zora");
	}

}

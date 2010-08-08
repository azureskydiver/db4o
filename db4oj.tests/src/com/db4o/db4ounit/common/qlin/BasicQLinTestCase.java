/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.qlin;

import java.util.*;

import static com.db4o.qlin.QLinSupport.*;

import db4ounit.*;
import db4ounit.extensions.*;


/**
 * @sharpen.if !SILVERLIGHT
 */
@decaf.Remove(decaf.Platform.JDK11)
public class BasicQLinTestCase extends AbstractDb4oTestCase implements TestLifeCycle {
	
	
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
	
	public void testQueryingByInterface(){
		storeAll(occamAndIsetta());
		Dog dog = prototype(Dog.class);
		Cat cat = prototype(Cat.class);
		assertQuery(isetta(), dog, "Isetta");
		assertQuery(occam(), cat, "Occam");
	}
	
	public void assertQuery(List<? extends Pet> expected, Pet pet, String name){
		IteratorAssert.sameContent(expected, 
				db().from(pet.getClass())
					.where(pet.name())
					.equal(name)
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
	
	private List<Cat> zora() {
		return singleCat("Zora");
	}
	
	private List<Dog> isetta() {
		return singleDog("Isetta");
	}
	
	private List<Pet> occamAndIsetta(){
		List<Pet> list = new ArrayList<Pet>();
		list.add(new Cat("Occam"));
		list.add(new Dog("Isetta"));
		return list;
	}

	private List<Cat> singleCat(String name) {
		List<Cat> list = new ArrayList<Cat>();
		list.add(new Cat(name));
		return list;
	}
	
	private List<Dog> singleDog(String name) {
		List<Dog> list = new ArrayList<Dog>();
		list.add(new Dog(name));
		return list;
	}
	
	public static class Cat implements Pet {
		
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
	
	public static class Dog implements Pet {
		
		private String _name;
		
		public Dog(String name){
			_name = name;
		}

		public String name() {
			return _name;
		}
		
		public boolean equals(Object obj) {
			if(! (obj instanceof Dog)){
				return false;
			}
			Dog other = (Dog) obj;
			if (_name == null) {
				return other._name == null;
			}
			return _name.equals(other._name);
		}
	}
	
	public interface Pet<T> {
		
		public String name();
		
	}

}

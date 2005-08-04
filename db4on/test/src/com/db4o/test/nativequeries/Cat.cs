/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */
using System;
using com.db4o;
using com.db4o.query;

namespace com.db4o.test.nativequeries
{
	public class Cat 
	{
		public String name;
    
		public Cat()
		{
		}
    
		public Cat(String name)
		{
			this.name = name;
		}
    
		public void store()
		{
			Tester.store(new Cat("Fritz"));
			Tester.store(new Cat("Garfield"));
			Tester.store(new Cat("Tom"));
			Tester.store(new Cat("Occam"));
			Tester.store(new Cat("Zora"));
		}
    
		public void test()
		{
			ObjectContainer objectContainer = Tester.objectContainer();
			ObjectSet objectSet = objectContainer.query(new CatPredicate());
			Tester.ensure(objectSet.Count == 2);
			String[] lookingFor = new String[] {"Occam" , "Zora"};
			bool[] found = new bool[2];
			foreach (Cat cat in objectSet)
			{
				for (int i = 0; i < lookingFor.Length; i++) 
				{
					if(cat.name == lookingFor[i])
					{
						found[i] = true;
					}
				}
			}
			for (int i = 0; i < found.Length; i++) 
			{
				Tester.ensure(found[i]);
			}
		}
    
		public class CatPredicate : Predicate
		{
			public bool match(Cat cat)
			{
				return cat.name == "Occam"  || cat.name == "Zora"; 
			}
		}
	}
}

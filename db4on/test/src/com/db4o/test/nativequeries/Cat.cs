/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */
using System;
using com.db4o;
using com.db4o.query;
using System.Collections;

namespace com.db4o.test.nativequeries
{
	public class Cat 
	{
		public string name;
    
		public Cat()
		{
		}
    
		public Cat(string name)
		{
			this.name = name;
		}
    
		public void store()
		{
            Tester.store(new Cat("Tom"));
            Tester.store(new Cat("Occam"));
			Tester.store(new Cat("Fritz"));
			Tester.store(new Cat("Garfield"));
			Tester.store(new Cat("Zora"));
		}
	    
	    class CatComparer : IComparer
	    {
	        public static readonly IComparer Instance = new CatComparer();
	        
	        public int Compare(object x, object y)
	        {
                return ((Cat)y).name.CompareTo(((Cat)x).name);
	        }
	    }
	    
	    class AllCatsPredicate : Predicate
	    {
	        public static readonly Predicate Instance = new AllCatsPredicate();
	        
	        public bool match(Cat candidate)
	        {
                return true;
	        }
	    }
	    
	    public void testComparer()
	    {
            ObjectSet result = Tester.objectContainer().query(AllCatsPredicate.Instance, CatComparer.Instance);
            assertCatOrder(result, "Fritz", "Garfield", "Occam", "Tom", "Zora");
	    }
  
		public void testOrPredicate()
		{
            if(Db4oVersion.MAJOR >= 5){
                ObjectContainer objectContainer = Tester.objectContainer();
                ObjectSet objectSet = objectContainer.query(new OrPredicate());
                Tester.ensureEquals(2, objectSet.Count);
                ensureContains(objectSet, "Occam");
                ensureContains(objectSet, "Zora");
            }
		}
		
		public class OrPredicate : Predicate
		{
			public bool match(Cat cat)
			{
				return cat.name == "Occam" || cat.name == "Zora"; 
			}
		}

#if NET_2_0
        public void testGenericPredicate()
        {
            if(Db4oVersion.MAJOR >= 5){
                ObjectContainer objectContainer = Tester.objectContainer();
                System.Collections.Generic.IList<Cat> found = objectContainer.query<Cat>(delegate(Cat c)
                {
                    return c.name == "Occam" || c.name == "Zora";
                });
                Tester.ensureEquals(2, found.Count);
                ensureContains(found, "Occam");
                ensureContains(found, "Zora");
            }
        }
	    
	    class GenericCatComparer : System.Collections.Generic.IComparer<Cat>
	    {
            public static readonly System.Collections.Generic.IComparer<Cat> Instance = new GenericCatComparer();
	        
	        public int Compare(Cat x, Cat y)
	        {
                return y.name.CompareTo(x.name);
	        }
	    }

        public void testGenericComparer()
        {
            System.Collections.Generic.IList<Cat> result = Tester.objectContainer().query(GenericCatComparer.Instance);
            assertCatOrder(result, "Fritz", "Garfield", "Occam", "Tom", "Zora");
        }

#endif
	    
	    private void assertCatOrder(IEnumerable cats, params string[] catNames)
	    {
            IEnumerator e = cats.GetEnumerator();
	        for (int i=0; i<catNames.Length; ++i)
	        {
                if (!Tester.ensure(e.MoveNext())) break;
                if (!Tester.ensureEquals(catNames[i], ((Cat)e.Current).name)) break;
	        }
	    }

        private void ensureContains(IEnumerable objectSet, string catName)
        {
            foreach (Cat cat in objectSet)
            {
              if (cat.name == catName) return;
            }
            Tester.ensure(catName + " expected!", false);
        }
	}
}

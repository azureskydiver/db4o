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

		public void Store()
		{
			Tester.Store(new Cat("Tom"));
			Tester.Store(new Cat("Occam"));
			Tester.Store(new Cat("Fritz"));
			Tester.Store(new Cat("Garfield"));
			Tester.Store(new Cat("Zora"));
		}

		class CatComparer : IComparer
		{
			public static readonly IComparer Instance = new CatComparer();

			public int Compare(object x, object y)
			{
				return ((Cat)x).name.CompareTo(((Cat)y).name);
			}
		}

		class AllCatsPredicate : Predicate
		{
			public static readonly Predicate Instance = new AllCatsPredicate();

			public bool Match(Cat candidate)
			{
				return true;
			}
		}

		public void TestComparer()
		{
			ObjectSet result = Tester.ObjectContainer().Query(AllCatsPredicate.Instance, CatComparer.Instance);
			AssertCatOrder(result, "Fritz", "Garfield", "Occam", "Tom", "Zora");
		}

		public void TestOrPredicate()
		{
			if (Db4oVersion.MAJOR >= 5)
			{
				ObjectContainer objectContainer = Tester.ObjectContainer();
				ObjectSet objectSet = objectContainer.Query(new OrPredicate());
				Tester.EnsureEquals(2, objectSet.Count);
				EnsureContains(objectSet, "Occam");
				EnsureContains(objectSet, "Zora");
			}
		}

		public class OrPredicate : Predicate
		{
			public bool Match(Cat cat)
			{
				return cat.name == "Occam" || cat.name == "Zora";
			}
		}

#if NET_2_0 || CF_2_0
		public void TestGenericPredicate()
		{
			if (Db4oVersion.MAJOR >= 5)
			{
				ObjectContainer objectContainer = Tester.ObjectContainer();
				System.Collections.Generic.IList<Cat> found = objectContainer.Query<Cat>(delegate(Cat c)
				{
					return c.name == "Occam" || c.name == "Zora";
				});
				Tester.EnsureEquals(2, found.Count);
				EnsureContains(found, "Occam");
				EnsureContains(found, "Zora");
			}
		}

		class GenericCatComparer : System.Collections.Generic.IComparer<Cat>
		{
			public static readonly System.Collections.Generic.IComparer<Cat> Instance = new GenericCatComparer();

			public int Compare(Cat x, Cat y)
			{
				return x.name.CompareTo(y.name);
			}
		}

		public void TestGenericComparer()
		{
			System.Collections.Generic.IList<Cat> result = Tester.ObjectContainer().Query(GenericCatComparer.Instance);
			AssertCatOrder(result, "Fritz", "Garfield", "Occam", "Tom", "Zora");
		}

		public void TestGenericComparison()
		{
			System.Collections.Generic.IList<Cat> result = Tester.ObjectContainer().Query<Cat>(
				delegate(Cat candidate) { return true; },
				delegate(Cat x, Cat y) { return x.name.CompareTo(y.name); });
			AssertCatOrder(result, "Fritz", "Garfield", "Occam", "Tom", "Zora");
		}

#endif

		private void AssertCatOrder(IEnumerable cats, params string[] catNames)
		{
			IEnumerator e = cats.GetEnumerator();
			for (int i = 0; i < catNames.Length; ++i)
			{
				if (!Tester.Ensure(e.MoveNext())) break;
				if (!Tester.EnsureEquals(catNames[i], ((Cat)e.Current).name)) break;
			}
		}

		private void EnsureContains(IEnumerable objectSet, string catName)
		{
			foreach (Cat cat in objectSet)
			{
				if (cat.name == catName) return;
			}
			Tester.Ensure(catName + " expected!", false);
		}
	}
}

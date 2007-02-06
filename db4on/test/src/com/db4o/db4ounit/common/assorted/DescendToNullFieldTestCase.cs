namespace com.db4o.db4ounit.common.assorted
{
	public class DescendToNullFieldTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		private static int COUNT = 2;

		public class ParentItem
		{
			public string _name;

			public com.db4o.db4ounit.common.assorted.DescendToNullFieldTestCase.ChildItem one;

			public com.db4o.db4ounit.common.assorted.DescendToNullFieldTestCase.ChildItem two;

			public ParentItem(string name, com.db4o.db4ounit.common.assorted.DescendToNullFieldTestCase.ChildItem
				 child1, com.db4o.db4ounit.common.assorted.DescendToNullFieldTestCase.ChildItem 
				child2)
			{
				_name = name;
				one = child1;
				two = child2;
			}
		}

		public class ChildItem
		{
			public string _name;

			public ChildItem(string name)
			{
				_name = name;
			}
		}

		protected override void Store()
		{
			for (int i = 0; i < COUNT; i++)
			{
				Store(new com.db4o.db4ounit.common.assorted.DescendToNullFieldTestCase.ParentItem
					("one", new com.db4o.db4ounit.common.assorted.DescendToNullFieldTestCase.ChildItem
					("one"), null));
			}
			for (int i = 0; i < COUNT; i++)
			{
				Store(new com.db4o.db4ounit.common.assorted.DescendToNullFieldTestCase.ParentItem
					("two", null, new com.db4o.db4ounit.common.assorted.DescendToNullFieldTestCase.ChildItem
					("two")));
			}
		}

		public virtual void Test()
		{
			AssertResults("one");
			AssertResults("two");
		}

		private void AssertResults(string name)
		{
			com.db4o.query.Query query = NewQuery(typeof(com.db4o.db4ounit.common.assorted.DescendToNullFieldTestCase.ParentItem)
				);
			query.Descend(name).Descend("_name").Constrain(name);
			com.db4o.ObjectSet objectSet = query.Execute();
			Db4oUnit.Assert.AreEqual(COUNT, objectSet.Size());
			while (objectSet.HasNext())
			{
				com.db4o.db4ounit.common.assorted.DescendToNullFieldTestCase.ParentItem parentItem
					 = (com.db4o.db4ounit.common.assorted.DescendToNullFieldTestCase.ParentItem)objectSet
					.Next();
				Db4oUnit.Assert.AreEqual(name, parentItem._name);
			}
		}
	}
}

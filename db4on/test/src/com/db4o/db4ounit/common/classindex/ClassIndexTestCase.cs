namespace com.db4o.db4ounit.common.classindex
{
	public class ClassIndexTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase, Db4oUnit.Extensions.Fixtures.OptOutCS
	{
		public class Item
		{
			public string name;

			public Item(string _name)
			{
				this.name = _name;
			}
		}

		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.classindex.ClassIndexTestCase().RunSolo();
		}

		public virtual void TestDelete()
		{
			com.db4o.db4ounit.common.classindex.ClassIndexTestCase.Item item = new com.db4o.db4ounit.common.classindex.ClassIndexTestCase.Item
				("test");
			Store(item);
			int id = (int)Db().GetID(item);
			AssertID(id);
			Reopen();
			item = (com.db4o.db4ounit.common.classindex.ClassIndexTestCase.Item)Db().Get(item
				).Next();
			id = (int)Db().GetID(item);
			AssertID(id);
			Db().Delete(item);
			Db().Commit();
			AssertEmpty();
			Reopen();
			AssertEmpty();
		}

		private void AssertID(int id)
		{
			AssertIndex(new object[] { id });
		}

		private void AssertEmpty()
		{
			AssertIndex(new object[] {  });
		}

		private void AssertIndex(object[] expected)
		{
			com.db4o.@internal.ClassMetadata clazz = Stream().GetYapClass(Db4oUnit.Extensions.Db4oUnitPlatform.GetReflectClass
				(Reflector(), typeof(com.db4o.db4ounit.common.classindex.ClassIndexTestCase.Item)
				));
			com.db4o.db4ounit.common.btree.ExpectingVisitor visitor = new com.db4o.db4ounit.common.btree.ExpectingVisitor
				(expected);
			com.db4o.@internal.classindex.ClassIndexStrategy index = clazz.Index();
			index.TraverseAll(Trans(), visitor);
			visitor.AssertExpectations();
		}
	}
}

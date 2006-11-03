namespace com.db4o.db4ounit.common.querying
{
	/// <exclude></exclude>
	public class ObjectSetTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.querying.ObjectSetTestCase().RunSoloAndClientServer(
				);
		}

		public class Item
		{
			public string name;

			public Item()
			{
			}

			public Item(string name)
			{
				this.name = name;
			}

			public override string ToString()
			{
				return "Item(\"" + name + "\")";
			}
		}

		protected override void Store()
		{
			Db().Set(new com.db4o.db4ounit.common.querying.ObjectSetTestCase.Item("foo"));
			Db().Set(new com.db4o.db4ounit.common.querying.ObjectSetTestCase.Item("bar"));
			Db().Set(new com.db4o.db4ounit.common.querying.ObjectSetTestCase.Item("baz"));
		}

		public virtual void TestObjectsCantBeSeenAfterDelete()
		{
			com.db4o.Transaction trans1 = NewTransaction();
			com.db4o.Transaction trans2 = NewTransaction();
			com.db4o.ObjectSet os = QueryItems(trans1);
			DeleteItemAndCommit(trans2, "foo");
			AssertItems(new string[] { "bar", "baz" }, os);
		}

		public virtual void _testAccessOrder()
		{
			com.db4o.ObjectSet result = NewQuery(typeof(com.db4o.db4ounit.common.querying.ObjectSetTestCase.Item)
				).Execute();
			for (int i = 0; i < result.Size(); ++i)
			{
				Db4oUnit.Assert.IsTrue(result.HasNext());
				Db4oUnit.Assert.AreSame(result.Ext().Get(i), result.Next());
			}
			Db4oUnit.Assert.IsFalse(result.HasNext());
		}

		private void AssertItems(string[] expectedNames, com.db4o.ObjectSet actual)
		{
			for (int i = 0; i < expectedNames.Length; i++)
			{
				Db4oUnit.Assert.IsTrue(actual.HasNext());
				Db4oUnit.Assert.AreEqual(expectedNames[i], ((com.db4o.db4ounit.common.querying.ObjectSetTestCase.Item
					)actual.Next()).name);
			}
			Db4oUnit.Assert.IsFalse(actual.HasNext());
		}

		private void DeleteItemAndCommit(com.db4o.Transaction trans, string name)
		{
			Stream().Delete(trans, QueryItem(trans, name));
			trans.Commit();
		}

		private com.db4o.db4ounit.common.querying.ObjectSetTestCase.Item QueryItem(com.db4o.Transaction
			 trans, string name)
		{
			com.db4o.query.Query q = NewQuery(trans, typeof(com.db4o.db4ounit.common.querying.ObjectSetTestCase.Item)
				);
			q.Descend("name").Constrain(name);
			return (com.db4o.db4ounit.common.querying.ObjectSetTestCase.Item)q.Execute().Next
				();
		}

		private com.db4o.ObjectSet QueryItems(com.db4o.Transaction trans)
		{
			com.db4o.query.Query q = NewQuery(trans, typeof(com.db4o.db4ounit.common.querying.ObjectSetTestCase.Item)
				);
			q.Descend("name").OrderAscending();
			return q.Execute();
		}
	}
}

namespace com.db4o.db4ounit.common.assorted
{
	public class SimplestPossibleTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.assorted.SimplestPossibleTestCase().RunSolo();
		}

		protected override void Store()
		{
			Db().Set(new com.db4o.db4ounit.common.assorted.SimplestPossibleItem("one"));
		}

		public virtual void Test()
		{
			com.db4o.query.Query q = Db().Query();
			q.Constrain(typeof(com.db4o.db4ounit.common.assorted.SimplestPossibleItem));
			q.Descend("name").Constrain("one");
			com.db4o.ObjectSet objectSet = q.Execute();
			com.db4o.db4ounit.common.assorted.SimplestPossibleItem item = (com.db4o.db4ounit.common.assorted.SimplestPossibleItem
				)objectSet.Next();
			Db4oUnit.Assert.IsNotNull(item);
			Db4oUnit.Assert.AreEqual("one", item.GetName());
		}
	}
}

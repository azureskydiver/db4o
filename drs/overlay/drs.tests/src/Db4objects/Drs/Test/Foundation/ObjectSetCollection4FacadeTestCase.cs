/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

namespace Db4objects.Drs.Test.Foundation
{
	public class ObjectSetCollection4FacadeTestCase : Db4oUnit.ITestCase
	{
		public static void Main(string[] args)
		{
			new Db4oUnit.TestRunner(typeof(Db4objects.Drs.Test.Foundation.ObjectSetCollection4FacadeTestCase)
				).Run();
		}

		public virtual void TestEmpty()
		{
			Db4objects.Drs.Foundation.ObjectSetCollection4Facade facade = new Db4objects.Drs.Foundation.ObjectSetCollection4Facade
				(new Db4objects.Db4o.Foundation.Collection4());
			Db4oUnit.Assert.IsFalse(facade.HasNext());
			Db4oUnit.Assert.IsFalse(facade.HasNext());
		}

		public virtual void TestIteration()
		{
			Db4objects.Db4o.Foundation.Collection4 collection = new Db4objects.Db4o.Foundation.Collection4
				();
			collection.Add("bar");
			collection.Add("foo");
			Db4objects.Drs.Foundation.ObjectSetCollection4Facade facade = new Db4objects.Drs.Foundation.ObjectSetCollection4Facade
				(collection);
			Db4oUnit.Assert.IsTrue(facade.HasNext());
			Db4oUnit.Assert.AreEqual("bar", facade.Next());
			Db4oUnit.Assert.IsTrue(facade.HasNext());
			Db4oUnit.Assert.AreEqual("foo", facade.Next());
			Db4oUnit.Assert.IsFalse(facade.HasNext());
			facade.Reset();
			Db4oUnit.Assert.AreEqual("bar", facade.Next());
			Db4oUnit.Assert.AreEqual("foo", facade.Next());
			Db4oUnit.Assert.IsFalse(facade.HasNext());
		}
	}
}

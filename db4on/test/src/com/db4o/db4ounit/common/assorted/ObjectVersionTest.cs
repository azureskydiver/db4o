namespace com.db4o.db4ounit.common.assorted
{
	public class ObjectVersionTest : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		protected override void Configure(com.db4o.config.Configuration config)
		{
			config.GenerateUUIDs(int.MaxValue);
			config.GenerateVersionNumbers(int.MaxValue);
		}

		public virtual void Test()
		{
			com.db4o.ext.ExtObjectContainer oc = this.Db();
			com.db4o.db4ounit.common.assorted.SimplestPossibleItem @object = new com.db4o.db4ounit.common.assorted.SimplestPossibleItem
				("c1");
			oc.Set(@object);
			com.db4o.ext.ObjectInfo objectInfo1 = oc.GetObjectInfo(@object);
			long oldVer = objectInfo1.GetVersion();
			@object.SetName("c3");
			oc.Set(@object);
			com.db4o.ext.ObjectInfo objectInfo2 = oc.GetObjectInfo(@object);
			long newVer = objectInfo2.GetVersion();
			Db4oUnit.Assert.IsNotNull(objectInfo1.GetUUID());
			Db4oUnit.Assert.IsNotNull(objectInfo2.GetUUID());
			Db4oUnit.Assert.IsTrue(oldVer > 0);
			Db4oUnit.Assert.IsTrue(newVer > 0);
			Db4oUnit.Assert.AreEqual(objectInfo1.GetUUID(), objectInfo2.GetUUID());
			Db4oUnit.Assert.IsTrue(newVer > oldVer);
		}
	}
}

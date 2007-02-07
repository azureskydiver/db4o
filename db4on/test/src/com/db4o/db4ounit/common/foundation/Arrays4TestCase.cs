namespace com.db4o.db4ounit.common.foundation
{
	/// <exclude></exclude>
	public class Arrays4TestCase : Db4oUnit.TestCase
	{
		public virtual void TestContainsInstanceOf()
		{
			object[] array = new object[] { "foo", 42 };
			Db4oUnit.Assert.IsTrue(Db4oUnit.Extensions.Db4oUnitPlatform.ArrayContainsInstanceOf
				(array, typeof(string)));
			Db4oUnit.Assert.IsTrue(Db4oUnit.Extensions.Db4oUnitPlatform.ArrayContainsInstanceOf
				(array, typeof(int)));
			Db4oUnit.Assert.IsTrue(Db4oUnit.Extensions.Db4oUnitPlatform.ArrayContainsInstanceOf
				(array, typeof(object)));
			Db4oUnit.Assert.IsFalse(Db4oUnit.Extensions.Db4oUnitPlatform.ArrayContainsInstanceOf
				(array, typeof(float)));
			Db4oUnit.Assert.IsFalse(Db4oUnit.Extensions.Db4oUnitPlatform.ArrayContainsInstanceOf
				(new object[0], typeof(object)));
			Db4oUnit.Assert.IsFalse(Db4oUnit.Extensions.Db4oUnitPlatform.ArrayContainsInstanceOf
				(new object[1], typeof(object)));
			Db4oUnit.Assert.IsFalse(Db4oUnit.Extensions.Db4oUnitPlatform.ArrayContainsInstanceOf
				(null, typeof(object)));
		}
	}
}

namespace com.db4o.db4ounit.common.types.arrays
{
	public class SimpleTypeArrayInUntypedVariableTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		private static readonly int[] ARRAY = { 1, 2, 3 };

		public class Data
		{
			public object _arr;

			public Data(object arr)
			{
				this._arr = arr;
			}
		}

		protected override void Store()
		{
			Db().Set(new com.db4o.db4ounit.common.types.arrays.SimpleTypeArrayInUntypedVariableTestCase.Data
				(ARRAY));
		}

		public virtual void TestRetrieval()
		{
			com.db4o.db4ounit.common.types.arrays.SimpleTypeArrayInUntypedVariableTestCase.Data
				 data = (com.db4o.db4ounit.common.types.arrays.SimpleTypeArrayInUntypedVariableTestCase.Data
				)RetrieveOnlyInstance(typeof(com.db4o.db4ounit.common.types.arrays.SimpleTypeArrayInUntypedVariableTestCase.Data)
				);
			Db4oUnit.Assert.IsTrue(data._arr is int[]);
			int[] arri = (int[])data._arr;
			Db4oUnit.ArrayAssert.AreEqual(ARRAY, arri);
		}
	}
}

namespace com.db4o.db4ounit.common.types.arrays
{
	public class SimpleStringArrayTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		private static readonly string[] ARRAY = new string[] { "hi", "babe" };

		public class Data
		{
			public string[] _arr;

			public Data(string[] _arr)
			{
				this._arr = _arr;
			}
		}

		protected override void Store()
		{
			Db().Set(new com.db4o.db4ounit.common.types.arrays.SimpleStringArrayTestCase.Data
				(ARRAY));
		}

		public virtual void TestRetrieve()
		{
			com.db4o.db4ounit.common.types.arrays.SimpleStringArrayTestCase.Data data = (com.db4o.db4ounit.common.types.arrays.SimpleStringArrayTestCase.Data
				)RetrieveOnlyInstance(typeof(com.db4o.db4ounit.common.types.arrays.SimpleStringArrayTestCase.Data)
				);
			Db4oUnit.ArrayAssert.AreEqual(ARRAY, data._arr);
		}
	}
}

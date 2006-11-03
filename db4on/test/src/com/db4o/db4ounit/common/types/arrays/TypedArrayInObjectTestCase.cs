namespace com.db4o.db4ounit.common.types.arrays
{
	public class TypedArrayInObjectTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		private static readonly com.db4o.db4ounit.common.sampledata.AtomData[] ARRAY = { 
			new com.db4o.db4ounit.common.sampledata.AtomData("TypedArrayInObject") };

		public class Data
		{
			public object _obj;

			public object[] _objArr;

			public Data(object obj, object[] obj2)
			{
				this._obj = obj;
				this._objArr = obj2;
			}
		}

		protected override void Store()
		{
			com.db4o.db4ounit.common.types.arrays.TypedArrayInObjectTestCase.Data data = new 
				com.db4o.db4ounit.common.types.arrays.TypedArrayInObjectTestCase.Data(ARRAY, ARRAY
				);
			Db().Set(data);
		}

		public virtual void TestRetrieve()
		{
			com.db4o.db4ounit.common.types.arrays.TypedArrayInObjectTestCase.Data data = (com.db4o.db4ounit.common.types.arrays.TypedArrayInObjectTestCase.Data
				)RetrieveOnlyInstance(typeof(com.db4o.db4ounit.common.types.arrays.TypedArrayInObjectTestCase.Data)
				);
			Db4oUnit.Assert.IsTrue(data._obj is com.db4o.db4ounit.common.sampledata.AtomData[]
				, "Expected instance of " + typeof(com.db4o.db4ounit.common.sampledata.AtomData[])
				 + ", but got " + data._obj);
			Db4oUnit.Assert.IsTrue(data._objArr is com.db4o.db4ounit.common.sampledata.AtomData[]
				, "Expected instance of " + typeof(com.db4o.db4ounit.common.sampledata.AtomData[])
				 + ", but got " + data._objArr);
			Db4oUnit.ArrayAssert.AreEqual(ARRAY, data._objArr);
			Db4oUnit.ArrayAssert.AreEqual(ARRAY, (com.db4o.db4ounit.common.sampledata.AtomData[]
				)data._obj);
		}
	}
}

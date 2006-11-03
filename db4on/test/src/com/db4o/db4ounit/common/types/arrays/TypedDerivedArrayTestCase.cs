namespace com.db4o.db4ounit.common.types.arrays
{
	public class TypedDerivedArrayTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		private static readonly com.db4o.db4ounit.common.sampledata.MoleculeData[] ARRAY = 
			{ new com.db4o.db4ounit.common.sampledata.MoleculeData("TypedDerivedArray") };

		public class Data
		{
			public com.db4o.db4ounit.common.sampledata.AtomData[] _array;

			public Data(com.db4o.db4ounit.common.sampledata.AtomData[] AtomDatas)
			{
				this._array = AtomDatas;
			}
		}

		protected override void Store()
		{
			Db().Set(new com.db4o.db4ounit.common.types.arrays.TypedDerivedArrayTestCase.Data
				(ARRAY));
		}

		public virtual void Test()
		{
			com.db4o.db4ounit.common.types.arrays.TypedDerivedArrayTestCase.Data data = (com.db4o.db4ounit.common.types.arrays.TypedDerivedArrayTestCase.Data
				)RetrieveOnlyInstance(typeof(com.db4o.db4ounit.common.types.arrays.TypedDerivedArrayTestCase.Data)
				);
			Db4oUnit.Assert.IsTrue(data._array is com.db4o.db4ounit.common.sampledata.MoleculeData[]
				, "Expected instance of " + typeof(com.db4o.db4ounit.common.sampledata.MoleculeData[])
				 + ", but got " + data._array);
			Db4oUnit.ArrayAssert.AreEqual(ARRAY, data._array);
		}
	}
}

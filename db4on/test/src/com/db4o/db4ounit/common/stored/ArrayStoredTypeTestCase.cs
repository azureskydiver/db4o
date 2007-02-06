namespace com.db4o.db4ounit.common.stored
{
	public class ArrayStoredTypeTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public class Data
		{
			public bool[] _primitiveBoolean;

			public bool[] _wrapperBoolean;

			public int[] _primitiveInt;

			public int[] _wrapperInteger;

			public Data(bool[] primitiveBoolean, bool[] wrapperBoolean, int[] primitiveInteger
				, int[] wrapperInteger)
			{
				this._primitiveBoolean = primitiveBoolean;
				this._wrapperBoolean = wrapperBoolean;
				this._primitiveInt = primitiveInteger;
				this._wrapperInteger = wrapperInteger;
			}
		}

		protected override void Store()
		{
			com.db4o.db4ounit.common.stored.ArrayStoredTypeTestCase.Data data = new com.db4o.db4ounit.common.stored.ArrayStoredTypeTestCase.Data
				(new bool[] { true, false }, new bool[] { true, false }, new int[] { 0, 1, 2 }, 
				new int[] { 4, 5, 6 });
			Store(data);
		}

		public virtual void TestArrayStoredTypes()
		{
			com.db4o.ext.StoredClass clazz = Db().StoredClass(typeof(com.db4o.db4ounit.common.stored.ArrayStoredTypeTestCase.Data)
				);
			AssertStoredType(clazz, "_primitiveBoolean", typeof(bool));
			AssertStoredType(clazz, "_wrapperBoolean", typeof(bool));
			AssertStoredType(clazz, "_primitiveInt", typeof(int));
			AssertStoredType(clazz, "_wrapperInteger", typeof(int));
		}

		private void AssertStoredType(com.db4o.ext.StoredClass clazz, string fieldName, System.Type
			 type)
		{
			com.db4o.ext.StoredField field = clazz.StoredField(fieldName, null);
			Db4oUnit.Assert.AreEqual(type.FullName, SimpleName(field.GetStoredType().GetName(
				)));
		}

		private string SimpleName(string name)
		{
			int index = name.IndexOf(',');
			if (index < 0)
			{
				return name;
			}
			return j4o.lang.JavaSystem.Substring(name, 0, index);
		}
	}
}

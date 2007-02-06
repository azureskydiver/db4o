namespace com.db4o.db4ounit.common.assorted
{
	public class ClassMetadataTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public class SuperClazz
		{
			public int _id;

			public string _name;
		}

		public class SubClazz : com.db4o.db4ounit.common.assorted.ClassMetadataTestCase.SuperClazz
		{
			public int _age;
		}

		protected override void Store()
		{
			Store(new com.db4o.db4ounit.common.assorted.ClassMetadataTestCase.SubClazz());
		}

		public virtual void TestFieldIterator()
		{
			com.db4o.foundation.Collection4 expectedNames = new com.db4o.foundation.Collection4
				(new com.db4o.foundation.ArrayIterator4(new string[] { "_id", "_name", "_age" })
				);
			com.db4o.@internal.ClassMetadata clazz = Stream().GetYapClass(Db4oUnit.Extensions.Db4oUnitPlatform.GetReflectClass
				(Reflector(), typeof(com.db4o.db4ounit.common.assorted.ClassMetadataTestCase.SubClazz)
				));
			System.Collections.IEnumerator fieldIter = clazz.Fields();
			while (fieldIter.MoveNext())
			{
				com.db4o.@internal.FieldMetadata curField = (com.db4o.@internal.FieldMetadata)fieldIter
					.Current;
				Db4oUnit.Assert.IsNotNull(expectedNames.Remove(curField.GetName()));
			}
			Db4oUnit.Assert.IsTrue(expectedNames.IsEmpty());
		}
	}
}

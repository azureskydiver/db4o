namespace com.db4o.db4ounit.common.assorted
{
	public class PersistStaticFieldValuesTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public class Data
		{
			public static readonly com.db4o.db4ounit.common.assorted.PersistStaticFieldValuesTestCase.PsfvHelper
				 ONE = new com.db4o.db4ounit.common.assorted.PersistStaticFieldValuesTestCase.PsfvHelper
				();

			public static readonly com.db4o.db4ounit.common.assorted.PersistStaticFieldValuesTestCase.PsfvHelper
				 TWO = new com.db4o.db4ounit.common.assorted.PersistStaticFieldValuesTestCase.PsfvHelper
				();

			public static readonly com.db4o.db4ounit.common.assorted.PersistStaticFieldValuesTestCase.PsfvHelper
				 THREE = new com.db4o.db4ounit.common.assorted.PersistStaticFieldValuesTestCase.PsfvHelper
				();

			public com.db4o.db4ounit.common.assorted.PersistStaticFieldValuesTestCase.PsfvHelper
				 one;

			public com.db4o.db4ounit.common.assorted.PersistStaticFieldValuesTestCase.PsfvHelper
				 two;

			public com.db4o.db4ounit.common.assorted.PersistStaticFieldValuesTestCase.PsfvHelper
				 three;
		}

		protected override void Configure(com.db4o.config.Configuration config)
		{
			config.ObjectClass(typeof(com.db4o.db4ounit.common.assorted.PersistStaticFieldValuesTestCase.Data)
				).PersistStaticFieldValues();
		}

		protected override void Store()
		{
			com.db4o.db4ounit.common.assorted.PersistStaticFieldValuesTestCase.Data psfv = new 
				com.db4o.db4ounit.common.assorted.PersistStaticFieldValuesTestCase.Data();
			psfv.one = com.db4o.db4ounit.common.assorted.PersistStaticFieldValuesTestCase.Data
				.ONE;
			psfv.two = com.db4o.db4ounit.common.assorted.PersistStaticFieldValuesTestCase.Data
				.TWO;
			psfv.three = com.db4o.db4ounit.common.assorted.PersistStaticFieldValuesTestCase.Data
				.THREE;
			Store(psfv);
		}

		public virtual void Test()
		{
			com.db4o.db4ounit.common.assorted.PersistStaticFieldValuesTestCase.Data psfv = (com.db4o.db4ounit.common.assorted.PersistStaticFieldValuesTestCase.Data
				)RetrieveOnlyInstance(typeof(com.db4o.db4ounit.common.assorted.PersistStaticFieldValuesTestCase.Data)
				);
			Db4oUnit.Assert.AreSame(com.db4o.db4ounit.common.assorted.PersistStaticFieldValuesTestCase.Data
				.ONE, psfv.one);
			Db4oUnit.Assert.AreSame(com.db4o.db4ounit.common.assorted.PersistStaticFieldValuesTestCase.Data
				.TWO, psfv.two);
			Db4oUnit.Assert.AreSame(com.db4o.db4ounit.common.assorted.PersistStaticFieldValuesTestCase.Data
				.THREE, psfv.three);
		}

		public class PsfvHelper
		{
		}
	}
}

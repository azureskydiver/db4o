namespace com.db4o.db4ounit.common.defragment
{
	public class SlotDefragmentTestCase : Db4oUnit.TestLifeCycle
	{
		public virtual void TestPrimitiveIndex()
		{
			com.db4o.db4ounit.common.defragment.SlotDefragmentFixture.AssertIndex(com.db4o.db4ounit.common.defragment.SlotDefragmentFixture
				.PRIMITIVE_FIELDNAME);
		}

		public virtual void TestWrapperIndex()
		{
			com.db4o.db4ounit.common.defragment.SlotDefragmentFixture.AssertIndex(com.db4o.db4ounit.common.defragment.SlotDefragmentFixture
				.WRAPPER_FIELDNAME);
		}

		public virtual void TestTypedObjectIndex()
		{
			com.db4o.db4ounit.common.defragment.SlotDefragmentFixture.ForceIndex();
			com.db4o.defragment.Defragment.Defrag(com.db4o.db4ounit.common.defragment.SlotDefragmentTestConstants
				.FILENAME, com.db4o.db4ounit.common.defragment.SlotDefragmentTestConstants.BACKUPFILENAME
				);
			com.db4o.ObjectContainer db = com.db4o.Db4o.OpenFile(com.db4o.Db4o.NewConfiguration
				(), com.db4o.db4ounit.common.defragment.SlotDefragmentTestConstants.FILENAME);
			com.db4o.query.Query query = db.Query();
			query.Constrain(typeof(com.db4o.db4ounit.common.defragment.SlotDefragmentFixture.Data)
				);
			query.Descend(com.db4o.db4ounit.common.defragment.SlotDefragmentFixture.TYPEDOBJECT_FIELDNAME
				).Descend(com.db4o.db4ounit.common.defragment.SlotDefragmentFixture.PRIMITIVE_FIELDNAME
				).Constrain(com.db4o.db4ounit.common.defragment.SlotDefragmentFixture.VALUE);
			com.db4o.ObjectSet result = query.Execute();
			Db4oUnit.Assert.AreEqual(1, result.Size());
			db.Close();
		}

		public virtual void TestNoForceDelete()
		{
			com.db4o.defragment.Defragment.Defrag(com.db4o.db4ounit.common.defragment.SlotDefragmentTestConstants
				.FILENAME, com.db4o.db4ounit.common.defragment.SlotDefragmentTestConstants.BACKUPFILENAME
				);
			Db4oUnit.Assert.Expect(typeof(System.IO.IOException), new _AnonymousInnerClass37(
				this));
		}

		private sealed class _AnonymousInnerClass37 : Db4oUnit.CodeBlock
		{
			public _AnonymousInnerClass37(SlotDefragmentTestCase _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Run()
			{
				com.db4o.defragment.Defragment.Defrag(com.db4o.db4ounit.common.defragment.SlotDefragmentTestConstants
					.FILENAME, com.db4o.db4ounit.common.defragment.SlotDefragmentTestConstants.BACKUPFILENAME
					);
			}

			private readonly SlotDefragmentTestCase _enclosing;
		}

		public virtual void SetUp()
		{
			new j4o.io.File(com.db4o.db4ounit.common.defragment.SlotDefragmentTestConstants.FILENAME
				).Delete();
			new j4o.io.File(com.db4o.db4ounit.common.defragment.SlotDefragmentTestConstants.BACKUPFILENAME
				).Delete();
			com.db4o.db4ounit.common.defragment.SlotDefragmentFixture.CreateFile(com.db4o.db4ounit.common.defragment.SlotDefragmentTestConstants
				.FILENAME);
		}

		public virtual void TearDown()
		{
		}
	}
}

namespace com.db4o.db4ounit.common.assorted
{
	public class ReferenceSystemTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		private static readonly int[] IDS = new int[] { 100, 134, 689, 666, 775 };

		private static readonly object[] REFERENCES = CreateReferences();

		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.assorted.ReferenceSystemTestCase().RunSolo();
		}

		public virtual void TestTransactionalReferenceSystem()
		{
			com.db4o.@internal.TransactionalReferenceSystem transactionalReferenceSystem = new 
				com.db4o.@internal.TransactionalReferenceSystem();
			AssertAllRerefencesAvailableOnNew(transactionalReferenceSystem);
			transactionalReferenceSystem.Rollback();
			AssertEmpty(transactionalReferenceSystem);
			AssertAllRerefencesAvailableOnCommit(transactionalReferenceSystem);
		}

		public virtual void TestHashCodeReferenceSystem()
		{
			com.db4o.@internal.HashcodeReferenceSystem hashcodeReferenceSystem = new com.db4o.@internal.HashcodeReferenceSystem
				();
			AssertAllRerefencesAvailableOnNew(hashcodeReferenceSystem);
		}

		private void AssertAllRerefencesAvailableOnCommit(com.db4o.@internal.ReferenceSystem
			 referenceSystem)
		{
			FillReferenceSystem(referenceSystem);
			referenceSystem.Commit();
			AssertAllReferencesAvailable(referenceSystem);
		}

		private void AssertAllRerefencesAvailableOnNew(com.db4o.@internal.ReferenceSystem
			 referenceSystem)
		{
			FillReferenceSystem(referenceSystem);
			AssertAllReferencesAvailable(referenceSystem);
		}

		private void AssertEmpty(com.db4o.@internal.ReferenceSystem referenceSystem)
		{
			AssertContains(referenceSystem, new object[] {  });
		}

		private void AssertAllReferencesAvailable(com.db4o.@internal.ReferenceSystem referenceSystem
			)
		{
			AssertContains(referenceSystem, REFERENCES);
		}

		private void AssertContains(com.db4o.@internal.ReferenceSystem referenceSystem, object[]
			 objects)
		{
			com.db4o.db4ounit.common.btree.ExpectingVisitor expectingVisitor = new com.db4o.db4ounit.common.btree.ExpectingVisitor
				(objects);
			referenceSystem.TraverseReferences(expectingVisitor);
			expectingVisitor.AssertExpectations();
		}

		private void FillReferenceSystem(com.db4o.@internal.ReferenceSystem referenceSystem
			)
		{
			for (int i = 0; i < REFERENCES.Length; i++)
			{
				referenceSystem.AddNewReference((com.db4o.@internal.ObjectReference)REFERENCES[i]
					);
			}
		}

		private static object[] CreateReferences()
		{
			object[] references = new object[IDS.Length];
			for (int i = 0; i < IDS.Length; i++)
			{
				com.db4o.@internal.ObjectReference @ref = new com.db4o.@internal.ObjectReference(
					IDS[i]);
				@ref.SetObject(IDS[i].ToString());
				references[i] = @ref;
			}
			return references;
		}
	}
}

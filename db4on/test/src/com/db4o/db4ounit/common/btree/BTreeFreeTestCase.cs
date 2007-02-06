namespace com.db4o.db4ounit.common.btree
{
	public class BTreeFreeTestCase : com.db4o.db4ounit.common.btree.BTreeTestCaseBase
	{
		private static readonly int[] VALUES = new int[] { 1, 2, 5, 7, 8, 9, 12 };

		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.btree.BTreeFreeTestCase().RunSolo();
		}

		public virtual void Test()
		{
			Add(VALUES);
			System.Collections.IEnumerator allSlotIDs = _btree.AllNodeIds(SystemTrans());
			com.db4o.foundation.Collection4 allSlots = new com.db4o.foundation.Collection4();
			while (allSlotIDs.MoveNext())
			{
				int slotID = (int)allSlotIDs.Current;
				com.db4o.@internal.slots.Slot slot = FileTransaction().GetCurrentSlotOfID(slotID);
				allSlots.Add(slot);
			}
			com.db4o.@internal.LocalObjectContainer yapFile = (com.db4o.@internal.LocalObjectContainer
				)Stream();
			com.db4o.foundation.Collection4 freedSlots = new com.db4o.foundation.Collection4(
				);
			yapFile.InstallDebugFreespaceManager(new com.db4o.db4ounit.common.btree.FreespaceManagerForDebug
				(yapFile, new _AnonymousInnerClass40(this, freedSlots)));
			_btree.Free(SystemTrans());
			SystemTrans().Commit();
			Db4oUnit.Assert.IsTrue(freedSlots.ContainsAll(allSlots.GetEnumerator()));
		}

		private sealed class _AnonymousInnerClass40 : com.db4o.db4ounit.common.btree.SlotListener
		{
			public _AnonymousInnerClass40(BTreeFreeTestCase _enclosing, com.db4o.foundation.Collection4
				 freedSlots)
			{
				this._enclosing = _enclosing;
				this.freedSlots = freedSlots;
			}

			public void OnFree(com.db4o.@internal.slots.Slot slot)
			{
				freedSlots.Add(slot);
			}

			private readonly BTreeFreeTestCase _enclosing;

			private readonly com.db4o.foundation.Collection4 freedSlots;
		}

		private com.db4o.@internal.LocalTransaction FileTransaction()
		{
			return ((com.db4o.@internal.LocalTransaction)Trans());
		}
	}
}

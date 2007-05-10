using System.Collections;
using Db4oUnit;
using Db4objects.Db4o.Foundation;
using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Internal.Slots;
using Db4objects.Db4o.Tests.Common.Btree;

namespace Db4objects.Db4o.Tests.Common.Btree
{
	public class BTreeFreeTestCase : BTreeTestCaseBase
	{
		private static readonly int[] VALUES = new int[] { 1, 2, 5, 7, 8, 9, 12 };

		public static void Main(string[] args)
		{
			new BTreeFreeTestCase().RunSolo();
		}

		public virtual void Test()
		{
			Add(VALUES);
			IEnumerator allSlotIDs = _btree.AllNodeIds(SystemTrans());
			Collection4 allSlots = new Collection4();
			while (allSlotIDs.MoveNext())
			{
				int slotID = (int)allSlotIDs.Current;
				Slot slot = FileTransaction().GetCurrentSlotOfID(slotID);
				allSlots.Add(slot);
			}
			LocalObjectContainer container = (LocalObjectContainer)Stream();
			Collection4 freedSlots = new Collection4();
			container.InstallDebugFreespaceManager(new FreespaceManagerForDebug(container, new 
				_AnonymousInnerClass40(this, freedSlots, container)));
			_btree.Free(SystemTrans());
			SystemTrans().Commit();
			Assert.IsTrue(freedSlots.ContainsAll(allSlots.GetEnumerator()));
		}

		private sealed class _AnonymousInnerClass40 : ISlotListener
		{
			public _AnonymousInnerClass40(BTreeFreeTestCase _enclosing, Collection4 freedSlots
				, LocalObjectContainer container)
			{
				this._enclosing = _enclosing;
				this.freedSlots = freedSlots;
				this.container = container;
			}

			public void OnFree(Slot slot)
			{
				freedSlots.Add(container.ToNonBlockedLength(slot));
			}

			private readonly BTreeFreeTestCase _enclosing;

			private readonly Collection4 freedSlots;

			private readonly LocalObjectContainer container;
		}

		private LocalTransaction FileTransaction()
		{
			return ((LocalTransaction)Trans());
		}
	}
}

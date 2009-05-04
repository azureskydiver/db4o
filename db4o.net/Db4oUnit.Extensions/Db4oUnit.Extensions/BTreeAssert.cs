/* Copyright (C) 2004 - 2008  Versant Inc.  http://www.db4o.com */

using System.Collections;
using Db4oUnit;
using Db4oUnit.Extensions;
using Db4objects.Db4o.Foundation;
using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Internal.Btree;
using Db4objects.Db4o.Internal.Handlers;
using Db4objects.Db4o.Internal.Slots;

namespace Db4oUnit.Extensions
{
	public class BTreeAssert
	{
		public static void TraverseKeys(IBTreeRange result, IVisitor4 visitor)
		{
			IEnumerator i = result.Keys();
			while (i.MoveNext())
			{
				visitor.Visit(i.Current);
			}
		}

		public static void AssertKeys(Transaction transaction, BTree btree, int[] keys)
		{
			ExpectingVisitor visitor = ExpectingVisitor.CreateExpectingVisitor(keys);
			btree.TraverseKeys(transaction, visitor);
			visitor.AssertExpectations();
		}

		public static void AssertEmpty(Transaction transaction, BTree tree)
		{
			ExpectingVisitor visitor = new ExpectingVisitor(new object[0]);
			tree.TraverseKeys(transaction, visitor);
			visitor.AssertExpectations();
			Assert.AreEqual(0, tree.Size(transaction));
		}

		public static void DumpKeys(Transaction trans, BTree tree)
		{
			tree.TraverseKeys(trans, new _IVisitor4_35());
		}

		private sealed class _IVisitor4_35 : IVisitor4
		{
			public _IVisitor4_35()
			{
			}

			public void Visit(object obj)
			{
				Sharpen.Runtime.Out.WriteLine(obj);
			}
		}

		public static int FillSize(BTree btree)
		{
			return btree.NodeSize() + 1;
		}

		public static int[] NewBTreeNodeSizedArray(BTree btree, int value)
		{
			return IntArrays4.Fill(new int[FillSize(btree)], value);
		}

		public static void AssertRange(int[] expectedKeys, IBTreeRange range)
		{
			Assert.IsNotNull(range);
			ExpectingVisitor visitor = ExpectingVisitor.CreateSortedExpectingVisitor(expectedKeys
				);
			TraverseKeys(range, visitor);
			visitor.AssertExpectations();
		}

		public static BTree CreateIntKeyBTree(ObjectContainerBase stream, int id, int nodeSize
			)
		{
			return new BTree(stream.SystemTransaction(), id, new IntHandler(), nodeSize);
		}

		public static void AssertSingleElement(Transaction trans, BTree btree, object element
			)
		{
			Assert.AreEqual(1, btree.Size(trans));
			IBTreeRange result = btree.Search(trans, element);
			ExpectingVisitor expectingVisitor = new ExpectingVisitor(new object[] { element }
				);
			BTreeAssert.TraverseKeys(result, expectingVisitor);
			expectingVisitor.AssertExpectations();
			expectingVisitor = new ExpectingVisitor(new object[] { element });
			btree.TraverseKeys(trans, expectingVisitor);
			expectingVisitor.AssertExpectations();
		}

		/// <exception cref="System.Exception"></exception>
		public static void AssertAllSlotsFreed(LocalTransaction trans, BTree bTree, ICodeBlock
			 block)
		{
			IEnumerator allSlotIDs = bTree.AllNodeIds(trans.SystemTransaction());
			Collection4 allSlots = new Collection4();
			while (allSlotIDs.MoveNext())
			{
				int slotID = ((int)allSlotIDs.Current);
				Slot slot = trans.GetCurrentSlotOfID(slotID);
				allSlots.Add(slot);
			}
			Slot bTreeSlot = trans.GetCurrentSlotOfID(bTree.GetID());
			allSlots.Add(bTreeSlot);
			LocalObjectContainer container = (LocalObjectContainer)trans.Container();
			Collection4 freedSlots = new Collection4();
			container.InstallDebugFreespaceManager(new FreespaceManagerForDebug(container, new 
				_ISlotListener_95(freedSlots, container)));
			block.Run();
			Assert.IsTrue(freedSlots.ContainsAll(allSlots.GetEnumerator()));
		}

		private sealed class _ISlotListener_95 : ISlotListener
		{
			public _ISlotListener_95(Collection4 freedSlots, LocalObjectContainer container)
			{
				this.freedSlots = freedSlots;
				this.container = container;
			}

			public void OnFree(Slot slot)
			{
				freedSlots.Add(container.ToNonBlockedLength(slot));
			}

			private readonly Collection4 freedSlots;

			private readonly LocalObjectContainer container;
		}
	}
}

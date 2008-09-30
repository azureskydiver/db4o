/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package db4ounit.extensions;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.slots.*;

import db4ounit.*;

public class BTreeAssert {

	public static void traverseKeys(BTreeRange result, Visitor4 visitor) {
		final Iterator4 i = result.keys();
		while (i.moveNext()) {
			visitor.visit(i.current());
		}
	}

	public static void assertKeys(final Transaction transaction, BTree btree, final int[] keys) {
		final ExpectingVisitor visitor = ExpectingVisitor.createExpectingVisitor(keys);
		btree.traverseKeys(transaction, visitor);
		visitor.assertExpectations();
	}

	public static void assertEmpty(Transaction transaction, BTree tree) {
	    final ExpectingVisitor visitor = new ExpectingVisitor(new Object[0]);
	    tree.traverseKeys(transaction, visitor);
		visitor.assertExpectations();
	    Assert.areEqual(0, tree.size(transaction));
	}

	public static void dumpKeys(Transaction trans, BTree tree) {
		tree.traverseKeys(trans, new Visitor4() {
			public void visit(Object obj) {
				System.out.println(obj);
			}
		});
	}

	public static int fillSize(BTree btree) {
		return btree.nodeSize()+1;
	}

	public static int[] newBTreeNodeSizedArray(final BTree btree, int value) {
		return IntArrays4.fill(new int[fillSize(btree)], value);
	}

	public static void assertRange(int[] expectedKeys, BTreeRange range) {
		Assert.isNotNull(range);
		final ExpectingVisitor visitor = ExpectingVisitor.createSortedExpectingVisitor(expectedKeys);
		
		traverseKeys(range, visitor);
		visitor.assertExpectations();
	}

	public static BTree createIntKeyBTree(final ObjectContainerBase stream, int id, int nodeSize) {
		return new BTree(stream.systemTransaction(), id, new IntHandler(), nodeSize, stream.configImpl().bTreeCacheHeight());
	}
	
	public static BTree createIntKeyBTree(final ObjectContainerBase stream, int id, int treeCacheHeight, int nodeSize) {
		return new BTree(stream.systemTransaction(), id, new IntHandler(), nodeSize, treeCacheHeight);
	}

	public static void assertSingleElement(Transaction trans, BTree btree, Object element) {
		Assert.areEqual(1, btree.size(trans));
		
		final BTreeRange result = btree.search(trans, element);
		ExpectingVisitor expectingVisitor = new ExpectingVisitor(new Object[] { element });
		BTreeAssert.traverseKeys(result, expectingVisitor);
		expectingVisitor.assertExpectations();
		
		expectingVisitor = new ExpectingVisitor(new Object[] { element });
		btree.traverseKeys(trans, expectingVisitor);
		expectingVisitor.assertExpectations();
	}
	
	public static void assertAllSlotsFreed(LocalTransaction trans, BTree bTree, CodeBlock block) throws Throwable {
		Iterator4 allSlotIDs = bTree.allNodeIds(trans.systemTransaction());
        
        Collection4 allSlots = new Collection4();
        
        while(allSlotIDs.moveNext()){
            int slotID = ((Integer)allSlotIDs.current()).intValue();
            Slot slot = trans.getCurrentSlotOfID(slotID);
            allSlots.add(slot);
        }
        
        Slot bTreeSlot = trans.getCurrentSlotOfID(bTree.getID());
        allSlots.add(bTreeSlot);
        
        final LocalObjectContainer container = (LocalObjectContainer)trans.container();
        
        
        final Collection4 freedSlots = new Collection4();
        
        container.installDebugFreespaceManager(
            new FreespaceManagerForDebug(container, new SlotListener() {
                public void onFree(Slot slot) {
                    freedSlots.add(container.toNonBlockedLength(slot));
                }
        }));
        
        block.run();
        
        
        Assert.isTrue(freedSlots.containsAll(allSlots.iterator()));
	}


}

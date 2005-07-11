/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.*;

/**
 * 
 * @exclude
 */
public class IxField {

    static final int   MAX_LEAVES = 3;

    static private int i_version;

    final YapField     i_field;

    final MetaIndex    i_metaIndex;

    IxFieldTransaction i_globalIndex;

    Collection4        i_transactionIndices;

    IxFileRangeReader  i_fileRangeReader;

    IxField(Transaction a_systemTrans, YapField a_field, MetaIndex a_metaIndex) {
        i_metaIndex = a_metaIndex;
        i_field = a_field;
        i_globalIndex = new IxFieldTransaction(a_systemTrans, this);
        createGlobalFileRange();
    }

    IxFieldTransaction dirtyFieldTransaction(Transaction a_trans) {
        IxFieldTransaction ift = new IxFieldTransaction(a_trans, this);
        if (i_transactionIndices == null) {
            i_transactionIndices = new Collection4();
        } else {
            IxFieldTransaction iftExisting = (IxFieldTransaction) i_transactionIndices
                    .get(ift);
            if (iftExisting != null) {
                return iftExisting;
            }
        }
        a_trans.addDirtyFieldIndex(ift);
        ift.setRoot(Tree.deepClone(i_globalIndex.getRoot(), ift));
        ift.i_version = ++i_version;
        i_transactionIndices.add(ift);
        return ift;
    }

    IxFieldTransaction getFieldTransaction(Transaction a_trans) {
        if (i_transactionIndices != null) {
            IxFieldTransaction ift = new IxFieldTransaction(a_trans, this);
            ift = (IxFieldTransaction) i_transactionIndices.get(ift);
            if (ift != null) {
                return ift;
            }
        }
        return i_globalIndex;
    }
    
// Debug index tree depth    
    
//    void debug(IxFieldTransaction a_ft){
//        System.out.println("+++  IxField commit debug begin");
//        Tree t1 = i_globalIndex.getRoot();
//        if (t1 != null){
//            System.out.println("i_globalIndex");
//            t1.debugDepth();
//        }
//        if(a_ft != null){
//            Tree t2 = a_ft.getRoot();
//            if (t2 != null){
//                System.out.println("a_ft");
//                t2.debugDepth();
//            }
//        }
//        System.out.println("---  IxField commit debug complete");
//    }

    void commit(IxFieldTransaction a_ft) {
        i_transactionIndices.remove(a_ft);

        i_globalIndex.merge(a_ft);

        int leaves = i_globalIndex.countLeaves();

        // TODO: Use more intelligent heuristic here to
        // calculate when to flush the global index
        // boolean createNewFileRange = leaves > MAX_LEAVES;
        boolean createNewFileRange = true;

        if (createNewFileRange) {
            final Transaction trans = i_globalIndex.i_trans;

            int[] free = new int[] { i_metaIndex.indexAddress,
                    i_metaIndex.indexLength, i_metaIndex.patchAddress,
                    i_metaIndex.patchLength};

            Tree root = i_globalIndex.getRoot();
            final YapDataType handler = i_field.getHandler();

            final int lengthPerEntry = handler.linkLength()
                    + YapConst.YAPINT_LENGTH;

            i_metaIndex.indexEntries = root == null ? 0 : root.size();
            i_metaIndex.indexLength = i_metaIndex.indexEntries * lengthPerEntry;
            i_metaIndex.indexAddress = ((YapFile) trans.i_stream)
                    .getSlot(i_metaIndex.indexLength);
            i_metaIndex.patchEntries = 0;
            i_metaIndex.patchAddress = 0;
            i_metaIndex.patchLength = 0;
            trans.i_stream.setInternal(trans, i_metaIndex, 1, false);

            final YapWriter writer = new YapWriter(trans,
                    i_metaIndex.indexAddress, lengthPerEntry);
            if (root != null) {
                root.traverse(new Visitor4() {

                    public void visit(Object a_object) {
                        ((IxTree) a_object).write(handler, writer);
                    }
                });
            }
            IxFileRange newFileRange = createGlobalFileRange();

            Iterator4 i = i_transactionIndices.iterator();
            while (i.hasNext()) {
                final IxFieldTransaction ft = (IxFieldTransaction) i.next();
                Tree clonedTree = newFileRange;
                if (clonedTree != null) {
                    clonedTree = clonedTree.deepClone(ft);
                }
                final Tree[] tree = { clonedTree};
                ft.getRoot().traverseFromLeaves((new Visitor4() {
                    
                    public void visit(Object a_object) {
                        IxTree ixTree = (IxTree) a_object;
                        if (ixTree.i_version == ft.i_version) {
                            if (!(ixTree instanceof IxFileRange)) {
                                ixTree.beginMerge();
                                tree[0] = tree[0].add(ixTree);
                            }
                        }
                    }
                }));
                ft.setRoot(tree[0]);
            }

            if (free[0] > 0) {
                trans.i_file.free(free[0], free[1]);
            }
            if (free[2] > 0) {
                trans.i_file.free(free[2], free[3]);
            }
        } else {
            Iterator4 i = i_transactionIndices.iterator();
            while (i.hasNext()) {
                ((IxFieldTransaction) i.next()).merge(a_ft);
            }
        }
    }

    private IxFileRange createGlobalFileRange() {
        IxFileRange fr = null;
        if (i_metaIndex.indexEntries > 0) {
            fr = new IxFileRange(i_globalIndex,
                    i_metaIndex.indexAddress, 0, i_metaIndex.indexEntries);
        }
        i_globalIndex.setRoot(fr);
        return fr;
    }

    void rollback(IxFieldTransaction a_ft) {
        i_transactionIndices.remove(a_ft);
    }

    IxFileRangeReader fileRangeReader() {
        if (i_fileRangeReader == null) {
            i_fileRangeReader = new IxFileRangeReader(i_field.getHandler());
        }
        return i_fileRangeReader;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("IxField  " + System.identityHashCode(this));
        if (i_globalIndex != null) {
            sb.append("\n  Global \n   ");
            sb.append(i_globalIndex.toString());
        } else {
            sb.append("\n  no global index \n   ");
        }
        if (i_transactionIndices != null) {
            Iterator4 i = i_transactionIndices.iterator();
            while (i.hasNext()) {
                sb.append("\n");
                sb.append(i.next().toString());
            }
        }
        return sb.toString();
    }

}

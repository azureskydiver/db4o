/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.inside.ix;

import com.db4o.*;
import com.db4o.foundation.*;

/**
 * 
 * @exclude
 */
public class Index4 {

    public final Indexable4     _handler;

    static private int _version;

    private final MetaIndex    _metaIndex;

    private IndexTransaction _globalIndexTransaction;

    private Collection4        _indexTransactions;

    private IxFileRangeReader  _fileRangeReader;

    public Index4(Transaction systemTrans, Indexable4 handler, MetaIndex metaIndex) {
        _metaIndex = metaIndex;
        _handler = handler;
        _globalIndexTransaction = new IndexTransaction(systemTrans, this);
        createGlobalFileRange();
    }

    public IndexTransaction dirtyIndexTransaction(Transaction a_trans) {
        IndexTransaction ift = new IndexTransaction(a_trans, this);
        if (_indexTransactions == null) {
            _indexTransactions = new Collection4();
        } else {
            IndexTransaction iftExisting = (IndexTransaction) _indexTransactions.get(ift);
            if (iftExisting != null) {
                return iftExisting;
            }
        }
        a_trans.addDirtyFieldIndex(ift);
        ift.setRoot(Tree.deepClone(_globalIndexTransaction.getRoot(), ift));
        ift.i_version = ++_version;
        _indexTransactions.add(ift);
        return ift;
    }
    
    public IndexTransaction globalIndexTransaction(){
        return _globalIndexTransaction;
    }

    public IndexTransaction indexTransactionFor(Transaction a_trans) {
        if (_indexTransactions != null) {
            IndexTransaction ift = new IndexTransaction(a_trans, this);
            ift = (IndexTransaction) _indexTransactions.get(ift);
            if (ift != null) {
                return ift;
            }
        }
        return _globalIndexTransaction;
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

    void commit(IndexTransaction a_ft) {
        _indexTransactions.remove(a_ft);

        _globalIndexTransaction.merge(a_ft);

        int leaves = _globalIndexTransaction.countLeaves();

        // TODO: Use more intelligent heuristic here to
        // calculate when to flush the global index
        // boolean createNewFileRange = leaves > MAX_LEAVES;
        boolean createNewFileRange = true;

        if (createNewFileRange) {
            final Transaction trans = _globalIndexTransaction.i_trans;

            int[] free = new int[] { _metaIndex.indexAddress,
                    _metaIndex.indexLength, _metaIndex.patchAddress,
                    _metaIndex.patchLength};

            Tree root = _globalIndexTransaction.getRoot();

            final int lengthPerEntry = _handler.linkLength()
                    + YapConst.YAPINT_LENGTH;

            _metaIndex.indexEntries = root == null ? 0 : root.size();
            _metaIndex.indexLength = _metaIndex.indexEntries * lengthPerEntry;
            _metaIndex.indexAddress = ((YapFile) trans.i_stream)
                    .getSlot(_metaIndex.indexLength);
            _metaIndex.patchEntries = 0;
            _metaIndex.patchAddress = 0;
            _metaIndex.patchLength = 0;
            trans.i_stream.setInternal(trans, _metaIndex, 1, false);

            final YapWriter writer = new YapWriter(trans,
                    _metaIndex.indexAddress, lengthPerEntry);
            if (root != null) {
                root.traverse(new Visitor4() {

                    public void visit(Object a_object) {
                        ((IxTree) a_object).write(_handler, writer);
                    }
                });
            }
            IxFileRange newFileRange = createGlobalFileRange();

            IIterator4 i = _indexTransactions.iterator();
            while (i.hasNext()) {
                final IndexTransaction ft = (IndexTransaction) i.next();
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
            IIterator4 i = _indexTransactions.iterator();
            while (i.hasNext()) {
                ((IndexTransaction) i.next()).merge(a_ft);
            }
        }
    }

    private IxFileRange createGlobalFileRange() {
        IxFileRange fr = null;
        if (_metaIndex.indexEntries > 0) {
            fr = new IxFileRange(_globalIndexTransaction,
                    _metaIndex.indexAddress, 0, _metaIndex.indexEntries);
        }
        _globalIndexTransaction.setRoot(fr);
        return fr;
    }

    void rollback(IndexTransaction a_ft) {
        _indexTransactions.remove(a_ft);
    }

    IxFileRangeReader fileRangeReader() {
        if (_fileRangeReader == null) {
            _fileRangeReader = new IxFileRangeReader(_handler);
        }
        return _fileRangeReader;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("IxField  " + System.identityHashCode(this));
        if (_globalIndexTransaction != null) {
            sb.append("\n  Global \n   ");
            sb.append(_globalIndexTransaction.toString());
        } else {
            sb.append("\n  no global index \n   ");
        }
        if (_indexTransactions != null) {
            IIterator4 i = _indexTransactions.iterator();
            while (i.hasNext()) {
                sb.append("\n");
                sb.append(i.next().toString());
            }
        }
        return sb.toString();
    }

}

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
    
    private int[] prepareFree(){
        return new int[] {
            _metaIndex.indexAddress,
            _metaIndex.indexLength,
            _metaIndex.patchAddress,
            _metaIndex.patchLength
        };
    }
    
    private void doFree(int[] free){
        YapFile file = file();
        for(int i = 0; i < free.length; i += 2){
            file.free(free[i], free[i + 1]);
        }
    }
                
    
    /**
     * solving a hen-egg problem: commit itself works with freespace 
     * so we have to do this all sequentially in the right way, working
     * with with both indexes at the same time.
     */
    public void commitFreeSpace(Index4 other){
        
        int[] myFree = prepareFree();
        int[] otherFree = other.prepareFree();
  
        Tree root = getRoot();
        
        int entries = countEntries();
        
        // For the two freespace indexes themselves, we call
        // the freespace system and we store two meta indexes. Potential effects:
        // 4 x getSlot   -4   to   0     
        // 4 x free      -4   to   + 4
        // 
        
        // Therefore we have to raise the value by 2, to make 
        // sure that there are enough.
        
        int length = (entries + 4) * lengthPerEntry();
        
        int mySlot = getSlot(length);
        int otherSlot = getSlot(length);
        
        storeMetaIndex(entries, length, mySlot);
        other.storeMetaIndex(entries, length, otherSlot);
        
        int newEntries = countEntries();
        
        if(entries != newEntries){
            remarshallMetaIndex(newEntries);
            other.remarshallMetaIndex(newEntries);
        }
        
        writeToNewSlot(mySlot, length);
        other.writeToNewSlot(otherSlot, length);
        
        doFree(myFree);
        doFree(otherFree);
        
//        if(Deploy.debug){
//            YapFile file = file();
//            for(int i = 0; i < free.length; i += 2){
//                file.writeXBytes(free[i], file.blocksFor(free[i + 1]) * file.blockSize() );
//            }
//        }
        
    }
    
    private int lengthPerEntry(){
        return _handler.linkLength() + YapConst.YAPINT_LENGTH;
    }
    
    private void free(){
        file().free(_metaIndex.indexAddress, _metaIndex.indexLength);
        file().free(_metaIndex.patchAddress, _metaIndex.indexLength);
    }
    
    private void storeMetaIndex(int entries, int length, int address){
        Transaction trans = trans();
        _metaIndex.indexEntries = entries;
        _metaIndex.indexLength = length;
        _metaIndex.indexAddress = address;
        _metaIndex.patchEntries = 0;
        _metaIndex.patchAddress = 0;
        _metaIndex.patchLength = 0;
        trans.i_stream.setInternal(trans, _metaIndex, 1, false);
    }
    
    private void remarshallMetaIndex(int entries){
        _metaIndex.indexEntries = entries;
        Transaction trans = trans();
        trans.i_stream.getYapObject(_metaIndex).remarshall(trans);
    }
    
    private IxFileRange writeToNewSlot(int slot, int length ){
        Transaction trans = trans();
        Tree root = getRoot();
        final YapWriter writer = new YapWriter(trans,slot, lengthPerEntry());
        if (root != null) {
            root.traverse(new Visitor4() {
                public void visit(Object a_object) {
                    ((IxTree) a_object).write(_handler, writer); 
                }
            });
        }
        return createGlobalFileRange();
    }
    

    void commit(IndexTransaction ixTrans) {
        
        _indexTransactions.remove(ixTrans);
        _globalIndexTransaction.merge(ixTrans);


        // TODO: Use more intelligent heuristic here to
        // calculate when to flush the global index
        
        // int leaves = _globalIndexTransaction.countLeaves();
        // boolean createNewFileRange = leaves > MAX_LEAVES;
        
        
        boolean createNewFileRange = true;

        if (createNewFileRange) {
            
            int entries = countEntries();
            int length = countEntries() * lengthPerEntry();
            int slot = getSlot(length);
            
            int[] free = prepareFree();
            
            storeMetaIndex(entries, length, slot);
            
            IxFileRange newFileRange = writeToNewSlot(slot, length);

            if(_indexTransactions != null){
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
            }
            
            doFree(free);

        } else {
            IIterator4 i = _indexTransactions.iterator();
            while (i.hasNext()) {
                ((IndexTransaction) i.next()).merge(ixTrans);
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
    
    private Transaction trans(){
        return _globalIndexTransaction.i_trans;
    }
    
    private YapFile file(){
        return trans().i_file;
    }
    
    private int getSlot(int length){
        return file().getSlot(length);
    }
    
    private Tree getRoot(){
        return _globalIndexTransaction.getRoot();
    }
    
    private int countEntries(){
        Tree root = getRoot();
        return root == null ? 0 : root.size();
    }
    

}

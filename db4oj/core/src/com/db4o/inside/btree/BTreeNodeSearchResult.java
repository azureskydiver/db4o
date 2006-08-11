/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.*;


/**
 * @exclude
 */
class BTreeNodeSearchResult {
    
    private final boolean _foundMatch;
    
    private final boolean _afterLast;
    
    private BTreePointer _pointer;

    BTreeNodeSearchResult(BTreeNode node, int cursor, boolean foundMatch, boolean afterLast) {
        if(node != null){
            _pointer = new BTreePointer(node, cursor);
        }
        _foundMatch = foundMatch;
        _afterLast = afterLast;
    }

    BTreeNodeSearchResult(Searcher searcher, BTreeNode node) {
        this(node,searcher.cursor(),searcher.foundMatch(), searcher.afterLast());
    }
    
    public BTreeRange createRangeTo(Transaction trans, BTreeNodeSearchResult end) {
        if(! _foundMatch){
            if(pointsToSameAs(end)){
                return new EmptyBTreeRange();
            }
            moveForward();
        }
        if(end._foundMatch){
            end.moveForward();
        }
        return new BTreeRangeImpl(trans, _pointer, end._pointer);
    }
    
    private void moveForward() {
        _pointer = _pointer.next();
    }
    
    private boolean pointsToSameAs(BTreeNodeSearchResult other){
        if(_pointer == null || other._pointer == null){
            return false;
        }
        return _pointer.equals(other._pointer);
    }

}

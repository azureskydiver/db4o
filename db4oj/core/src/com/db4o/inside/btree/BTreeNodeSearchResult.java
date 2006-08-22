/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.*;


/**
 * @exclude
 */
public class BTreeNodeSearchResult {
    
    private final boolean _foundMatch;
    
    private final BTreePointer _pointer;
    
    BTreeNodeSearchResult(BTreePointer pointer, boolean foundMatch) {
        _pointer = pointer;
        _foundMatch = foundMatch;
    }

    BTreeNodeSearchResult(BTreeNode node, int cursor, boolean foundMatch) {
        this(pointerOrNull(node, cursor), foundMatch);
    }

    BTreeNodeSearchResult(Transaction trans, Searcher searcher, BTreeNode node) {
        this(
            nextPointerIf(trans, pointerOrNull(node, searcher.cursor()), searcher.isGreater()),
            searcher.foundMatch());
    }
    
    private static BTreePointer nextPointerIf(Transaction trans, BTreePointer pointer, boolean condition) {
        if (null == pointer) {
            return null;
        }
        if (condition) {
            return pointer.next(trans);
        }
        return pointer;
    }
    
    private static BTreePointer pointerOrNull(BTreeNode node, int cursor) {
        return node == null ? null : new BTreePointer(node, cursor);
    }
    
    public BTreeRange createIncludingRange(Transaction trans, BTreeNodeSearchResult end) {
        if(!_foundMatch && pointsToSameAs(end)){
            return EmptyBTreeRange.INSTANCE;
        }
        BTreePointer endPointer = end._pointer;
        if(endPointer != null && end._foundMatch){
            endPointer = endPointer.next(trans);
        }
        return new BTreeRangeImpl(trans, _pointer, endPointer);
    }
    
    private boolean pointsToSameAs(BTreeNodeSearchResult other){
        if(_pointer == null || other._pointer == null){
            return false;
        }
        return _pointer.equals(other._pointer);
    }

}

/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.*;


/**
 * @exclude
 */
class BTreeNodeSearchResult {
    
    private int _cursor;
    
    private final boolean _foundMatch;
    
    private final boolean _afterLast;
    
    private BTreeNode _node;

    BTreeNodeSearchResult(BTreeNode node, int cursor, boolean foundMatch, boolean afterLast) {
        _node = node;
        _cursor = cursor;
        _foundMatch = foundMatch;
        _afterLast = afterLast;
    }

    BTreeNodeSearchResult(Searcher searcher, BTreeNode node) {
        this(node,searcher.cursor(),searcher.foundMatch(), searcher.afterLast());
    }
    
    private BTreePointer createPointer(){
        if(_node == null){
            return null;
        }
        return new BTreePointer(_node, _cursor);
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
        return new BTreeRangeImpl(trans, createPointer(), end.createPointer());
    }
    
//    private void moveBackward() {
//        if(_cursor == 0){
//            _node = _node.previousNode();
//            if(_node == null){
//                throw new IllegalStateException();
//            }
//            _cursor = _node.count() - 1;
//            return;
//        }
//        _cursor --;
//    }

    private void moveForward() {
        if(_afterLast){
            _node = _node.nextNode();
            _cursor = 0;
            return;
        }
        _cursor ++;
    }
    
    private boolean pointsToSameAs(BTreeNodeSearchResult other){
        if(other._node != _node){
            return false;
        }
        return _cursor == other._cursor;
    }

}

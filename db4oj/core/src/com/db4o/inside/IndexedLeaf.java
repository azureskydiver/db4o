/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.btree.*;


/**
 * @exclude
 */
public class IndexedLeaf {
    
    private final Transaction _transaction;
    private final QConObject _constraint;
    private BTreeRange _range;
    
    public IndexedLeaf(Transaction transaction, QConObject qcon) {
        _transaction = transaction;
        _constraint = qcon;
        _range = search();
    }
    
    private FieldIndexKey integerComposite(int integerPart){
        return new FieldIndexKey(integerPart, _constraint.getObject());
    }
    
    private BTreeNodeSearchResult searchLeaf(int integerPart){
        return getIndex().searchLeaf(_transaction, integerComposite(integerPart), SearchTarget.LOWEST);
    }
    

    private BTreeRange search() {
        
        BTreeNodeSearchResult lowestResult = searchLeaf(0);
        BTreeNodeSearchResult highestResult = searchLeaf(Integer.MAX_VALUE);
        
        final BTreeRange range = lowestResult.createIncludingRange(_transaction, highestResult);
        final QEBitmap bitmap = QEBitmap.forQE(_constraint.i_evaluator);
        if (bitmap.takeGreater()) {             
            if (bitmap.takeEqual()) {
                return range.extend();
            }
            return range.greater();
        }
        if (bitmap.takeSmaller()) {
            return range.smaller();
        }
        return range;
    }

    public BTree getIndex() {
        return getYapField().getIndex();
    }

    private YapField getYapField() {
        return _constraint.getField().getYapField();
    }

    public int resultSize() {
        return _range.size();
    }

    public TreeInt toTreeInt() {
        final KeyValueIterator i = _range.iterator();
        
        TreeInt result = null;
        while (i.moveNext()) {
            FieldIndexKey composite = (FieldIndexKey)i.key();
            result = (TreeInt) Tree.add(result, new TreeInt(composite.parentID()));
        }
        return result;
    }

    public QConObject constraint() {
        return _constraint;
    }

    
    

}

/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * An addition to a field index.
 */
class IxAdd extends IxPatch {
    
    boolean i_keepRemoved;

    IxAdd(IxFieldTransaction a_ft, int a_parentID, Object a_value) {
        super(a_ft, a_parentID, a_value);
    }
    
    Tree addToCandidatesTree(Tree a_tree, QCandidates a_candidates, int[] a_lowerAndUpperMatch) {
        QCandidate candidate = new QCandidate(a_candidates, null, i_parentID, true);
        if (a_tree == null) {
            return candidate;
        }
        a_tree = a_tree.add(candidate);
        return a_tree;
    }

    void write(YapDataType a_handler, YapWriter a_writer) {
        a_handler.writeIndexEntry(a_writer, i_value);
        a_writer.writeInt(i_parentID);
        a_writer.writeForward();
    }
    
    void beginMerge(){
        super.beginMerge();
        handler().prepareComparison( handler().comparableObject(trans(), i_value));
    }
    
    public String toString(){
        String str = "IxAdd "  + i_parentID + "\n " + handler().comparableObject(trans(), i_value);
        return str;
    }
    

}

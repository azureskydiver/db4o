/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * A node to represent an entry removed from an Index
 */
class IxRemove extends IxPatch {

    IxRemove(IxFieldTransaction a_ft, int a_parentID, Object a_value) {
        super(a_ft, a_parentID, a_value);
        i_size = 0;
    }

    Tree addToCandidatesTree(Tree a_tree, QCandidates a_candidates, int[] a_lowerAndUpperMatch) {
        return a_tree;
    }

    void write(YapDataType a_handler, YapWriter a_writer) {
        // do nothing
    }

    int ownSize() {
        return 0;
    }

    public String toString() {
        String str = "IxRemove " + i_parentID + "\n " + handler().indexObject(trans(), i_value);
        return str;
    }

}

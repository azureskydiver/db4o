/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.*;

/**
 * Node for index tree, can be addition or removal node
 */
abstract class IxPatch extends IxTree {

    int    i_parentID;

    Object i_value;

    Queue4 i_queue;    // queue of patch objects for the same parent

    IxPatch(IxFieldTransaction a_ft, int a_parentID, Object a_value) {
        super(a_ft);
        i_parentID = a_parentID;
        i_value = a_value;
    }

    public Tree add(final Tree a_new) {
        int cmp = compare(a_new);
        if (cmp == 0) {
            IxPatch patch = (IxPatch) a_new;
            cmp = i_parentID - patch.i_parentID;

            if (cmp == 0) {

                Queue4 queue = i_queue;

                if (queue == null) {
                    queue = new Queue4();
                    queue.add(this);
                }

                queue.add(patch);
                patch.i_queue = queue;
                patch.i_subsequent = i_subsequent;
                patch.i_preceding = i_preceding;
                patch.calculateSize();
                return patch;
            }
        }
        return add(a_new, cmp);
    }

    int compare(Tree a_to) {
        YapDataType handler = i_fieldTransaction.i_index.i_field.getHandler();
        return handler.compareTo(handler.comparableObject(trans(), i_value));
    }

}

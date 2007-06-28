/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.query.processor;

import com.db4o.internal.*;
import com.db4o.internal.handlers.*;

/**
 * @exclude
 */
public class QEGreater extends QEAbstract {

    boolean evaluate(QConObject constraint, QCandidate candidate, Object obj) {
        if (obj == null) {
            return false;
        }
        Comparable4 comparator = constraint.getComparator(candidate);
        if (comparator instanceof ArrayHandler) {
            return ((ArrayHandler) comparator).isGreater(obj);
        }
        return comparator.compareTo(obj) > 0;
    }

    public void indexBitMap(boolean[] bits) {
        bits[QE.GREATER] = true;
    }
}

/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */
package com.db4o.internal.query.processor;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.query.*;

public interface InternalCandidate extends Candidate {
	QCandidateBase getRoot();
	ClassMetadata classMetadata();
	QCandidates candidates();
	Transaction transaction();
	boolean evaluate(QConObject qConObject, QE evaluator);
	boolean fieldIsAvailable();
	Integer key();
	PreparedComparison prepareComparison(ObjectContainerBase container, Object constraint);
}

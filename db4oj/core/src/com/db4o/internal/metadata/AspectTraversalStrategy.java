/* Copyright (C) 2009  Versant Corp.  http://www.db4o.com */

package com.db4o.internal.metadata;

import com.db4o.internal.marshall.*;

/**
 * @exclude
 */
public interface AspectTraversalStrategy {

	void traverseAllAspects(MarshallingInfo context,
			TraverseAspectCommand command, FieldListInfo fieldListInfo);

}
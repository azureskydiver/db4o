/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.ids;

import com.db4o.foundation.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public interface IdSystemCommitContext {
	
	public void commit(Visitable<SlotChange> slotChanges, int slotChangeCount);

}

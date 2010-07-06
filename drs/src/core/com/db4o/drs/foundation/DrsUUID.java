/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.foundation;

import com.db4o.ext.*;

public interface DrsUUID {

	long getLongPart();

	byte[] getSignaturePart();

	Db4oUUID db4oUUID();
	

}

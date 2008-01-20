/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.defragment;

import com.db4o.foundation.io.*;

public abstract class SlotDefragmentTestConstants {
	
	public final static String FILENAME = Path4.getTempFileName();
	public final static String BACKUPFILENAME = FILENAME+".backup";

	private SlotDefragmentTestConstants() {
	}
}

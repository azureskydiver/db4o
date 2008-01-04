/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.bench.logging.replay;

import com.db4o.io.*;

public interface IoCommand {
	
	public void replay(IoAdapter adapter);

}

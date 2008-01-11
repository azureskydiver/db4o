/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.bench.logging.replay.commands;

import com.db4o.io.*;

public class WriteCommand extends ReadWriteCommand implements IoCommand{
	
	public WriteCommand(int length) {
		super(length);
	}
	
	public void replay(IoAdapter adapter){
		adapter.write(prepareBuffer());
	}

}

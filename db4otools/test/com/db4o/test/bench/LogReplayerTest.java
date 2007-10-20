/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */


/**
 * TODO: Package name ok?
 */
package com.db4o.test.bench;

import com.db4o.bench.*;

import db4ounit.extensions.*;
import db4ounit.Assert;


public class LogReplayerTest extends AbstractDb4oTestCase {

	private static final String LINE_WRITE = "WRITE 42,187";
	private static final long LINE_WRITE_INDEX = 8;
	private static final String LINE_READ = "READ 84326841,6465";
	private static final long LINE_READ_INDEX = 13;
	private static final String LINE_SYNC = "SYNC";

	public static void main(String[] args) {
		new LogReplayerTest().runAll();
	}

	
	public void testSeparatorIndexForLine() {
		LogReplayer replay = new LogReplayer(null, null);
//		Assert.areEqual(replay.separatorIndexForLine(LINE_WRITE), LINE_WRITE_INDEX);
	}
}

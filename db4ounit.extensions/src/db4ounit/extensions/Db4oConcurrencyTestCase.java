/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions;

import com.db4o.foundation.*;

/**
 * @exclude
 */
public class Db4oConcurrencyTestCase extends Db4oClientServerTestCase {
	
	private boolean[] _done;
	
	
	protected void db4oSetupAfterStore() throws Exception {
		initTasksDoneFlag();
		super.db4oSetupAfterStore();
	}

	private void initTasksDoneFlag() {
		_done = new boolean[threadCount()];
	}
	
	protected void markTaskDone(int seq, boolean done) {
		_done[seq] = done;
	}
	
	protected void waitForAllTasksDone() throws Exception {
		while(!areAllTasksDone()) {
			Cool.sleepIgnoringInterruption(1);
		}
	}

	private boolean areAllTasksDone() {
		for(int i = 0; i < _done.length; ++i) {
			if(!_done[i]) {
				return false;
			}
		}
		return true;
	}
	
}

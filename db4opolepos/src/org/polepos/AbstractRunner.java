/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package org.polepos;

import org.polepos.framework.*;

public abstract class AbstractRunner {

	public void run() {
		new Racer(circuits(), teams()).run();
	}

	public abstract Circuit[] circuits();

	public abstract Team[] teams();

}
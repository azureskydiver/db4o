/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;

public class ActivationDepthZero {
	public void configure() {
		Db4o.configure().activationDepth(0);
	}
}

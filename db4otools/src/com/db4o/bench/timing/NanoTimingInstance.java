/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench.timing;


public class NanoTimingInstance {

	public static NanoTiming newInstance() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		return (NanoTiming) Class.forName("com.db4o.bench.timing.NanoTimingImpl").newInstance();
	}
	
}

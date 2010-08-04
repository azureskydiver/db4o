/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import java.util.*;

import com.db4o.drs.inside.*;
import com.db4o.drs.test.*;
import com.db4o.drs.versant.*;

public class VodFixture implements DrsFixture{
	
	private final VodDatabase _vod;

	public VodFixture(String name){
		_vod = new VodDatabase(name);
	}
	
	public void clean() {
		_vod.removeDb();
	}
	
	public void close() {
		
	}

	public void open() {
		_vod.produceDb();
		
		
	}

	public TestableReplicationProviderInside provider() {
		// return new VodReplicationProvider(vod);
		return null;
	}

}

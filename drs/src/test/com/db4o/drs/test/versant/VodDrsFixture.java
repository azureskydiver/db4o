/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import com.db4o.drs.inside.*;
import com.db4o.drs.test.*;
import com.db4o.drs.versant.*;

public class VodDrsFixture implements DrsFixture{
	
	private final VodDatabase _vod;
	
	protected VodReplicationProvider _provider;

	public VodDrsFixture(String name){
		_vod = new VodDatabase(name);
	}
	
	public void clean() {
		_vod.removeDb();
	}
	
	public void close() {
		_provider.destroy();
		_provider = null;
	}

	public void open() {
		_vod.produceDb();
		_provider = new VodReplicationProvider(_vod);
	}

	public TestableReplicationProviderInside provider() {
		return _provider;
	}

}

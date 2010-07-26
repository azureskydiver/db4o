/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import javax.jdo.*;

import com.versant.core.vds.*;

public class VodJdo {
	
	private final VodDatabase _vod;
	
	private final PersistenceManager _pm;

	public VodJdo(VodDatabase vod, PersistenceManager pm) {
		_vod = vod;
		_pm = pm;
	}

	public long loid(Object obj) {
		return VdsUtils.getLOID(obj, _pm);
	}

	public Object objectByLoid(long loid) {
		return VdsUtils.getObjectByLOID(loid, true, _pm);
	}
	
	

}

/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

public class LoidSignatureLongPart {
	
	public LoidSignatureLongPart(long loid, long signatureLoid, long longPart) {
		this.loid = loid;
		this.signatureLoid = signatureLoid;
		this.longPart = longPart;
	}

	public long loid;
	
	public long signatureLoid;
	
	public long longPart;

}

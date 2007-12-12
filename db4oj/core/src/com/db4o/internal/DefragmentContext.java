/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.marshall.*;

public interface DefragmentContext extends Context, ReadBuffer {
	
	public ClassMetadata classMetadataForId(int id);

	public void copyID();

	public int copyIDReturnOriginalID();

	public void copyUnindexedID();

	public void incrementOffset(int length);

	boolean isLegacyHandlerVersion();
	
	public int mappedID(int origID);
	
	public Buffer sourceBuffer();
	
	public Buffer targetBuffer();

}

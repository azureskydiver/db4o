/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.internal.mapping.*;

public interface DefragmentContext {
	
	boolean isLegacyHandlerVersion();
	
	public boolean redirect();

	public void copyID();

	public void copyUnindexedID();

	public void incrementOffset(int length);

	public void readBegin(byte identifier);

	public void readEnd();

	public int readInt();

	public IDMapping mapping();

	public Buffer sourceBuffer();
	
	public Buffer targetBuffer();

	public void seek(int offset);

	public int offset();

	public MappedIDPair copyIDAndRetrieveMapping();

	public DefragmentServices services();
	
}

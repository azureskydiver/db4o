/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */
package com.db4o.internal;

import com.db4o.*;


/**
 * @exclude
 */
public interface SlotCopyHandler {
	void processCopy(ReaderPair readers) throws CorruptionException;
}
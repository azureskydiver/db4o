/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.internal;

interface SlotChangeCollector {

	void added(int id);

	void updated(int id);

	void deleted(int id);

}
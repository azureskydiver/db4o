/* Copyright (C) 2010   Versant Inc.   http://www.db4o.com */

package com.db4o.filestats;

import java.util.*;

import com.db4o.internal.slots.*;

@decaf.Ignore
public interface SlotMap {

	void add(Slot slot);

	List<Slot> merged();

	List<Slot> gaps(long length);

}
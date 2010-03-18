/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.polepos.idsys;

import org.polepos.framework.*;

public class PlainLookupCircuit extends Circuit {

	@Override
	protected void addLaps() {
		add(new Lap("lapAllocate", false, false));
		add(new Lap("lapLookup"));
	}

	@Override
	public String description() {
		return "lookup IDs sequentially";
	}

	@Override
	public Class<? extends Driver> requiredDriver() {
		return PlainLookupDriver.class;
	}

}
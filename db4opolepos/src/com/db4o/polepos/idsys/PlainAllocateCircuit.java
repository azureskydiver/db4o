/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.polepos.idsys;

import org.polepos.framework.*;

public class PlainAllocateCircuit extends Circuit {

	@Override
	protected void addLaps() {
		add(new Lap("lapAllocate"));
	}

	@Override
	public String description() {
		return "allocated IDs sequentially";
	}

	@Override
	public Class<? extends Driver> requiredDriver() {
		return PlainAllocateDriver.class;
	}

}

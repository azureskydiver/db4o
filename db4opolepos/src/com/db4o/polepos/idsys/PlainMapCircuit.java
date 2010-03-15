/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.polepos.idsys;

import org.polepos.framework.*;

public class PlainMapCircuit extends Circuit {

	@Override
	protected void addLaps() {
		add(new Lap("lapAllocate", false, false));
		add(new Lap("lapMap"));
	}

	@Override
	public String description() {
		return "map IDs sequentially";
	}

	@Override
	public Class<? extends Driver> requiredDriver() {
		return PlainMapDriver.class;
	}

}

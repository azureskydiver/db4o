/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.polepos.idsys;

import com.db4o.config.*;


public class PointerBasedCar extends IdSystemCar {

	@Override
	public String name() {
		return "pointer-based id system";
	}

	public void configure(IdSystemConfiguration config) {
		config.usePointerBasedSystem();
	}

}

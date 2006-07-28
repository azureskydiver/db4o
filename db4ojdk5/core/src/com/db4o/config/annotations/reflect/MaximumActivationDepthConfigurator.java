/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.config.annotations.reflect;

public class MaximumActivationDepthConfigurator extends Db4oConfigurator {
	private String _className;

	private int _max;

	public MaximumActivationDepthConfigurator(String name, int max) {
		this._className = name;
		this._max =max;
	}

	@Override
	protected void configure() {
		objectClass(_className).maximumActivationDepth(_max);
	}

}

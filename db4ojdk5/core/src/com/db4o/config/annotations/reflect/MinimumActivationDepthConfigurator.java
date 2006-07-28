/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.config.annotations.reflect;

public class MinimumActivationDepthConfigurator extends Db4oConfigurator {
	private String _className;

	private int _min;

	public MinimumActivationDepthConfigurator(String name, int min) {
		this._className = name;
		this._min = min;
	}

	@Override
	protected void configure() {
		objectClass(_className).minimumActivationDepth(_min);

	}

}

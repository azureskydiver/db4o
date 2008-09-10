package com.db4o.container.tests.internal;

import com.db4o.container.tests.*;

public class ComplexServiceImpl implements ComplexService {
	
	private final SingletonService _service;

	public ComplexServiceImpl(SingletonService dependency) {
		_service = dependency;
	}
	
	public ComplexServiceImpl() {
		_service = null;
	}
	
	public SingletonService dependency() {
		return _service;
	}

}

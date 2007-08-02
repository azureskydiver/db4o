/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.ta.tests;import java.util.*;
class UnitOfWork extends ActivatableImpl {
		Date _started;	Date _finished;	String _name;
	public UnitOfWork(String name, Date started, Date finished) {		_name = name;		_started = started;		_finished = finished;	}	public String getName() {		// TA BEGIN		activate();		// TA END		return _name;	}		public long timeSpent() {		// TA BEGIN		activate();		// TA END		return _finished.getTime() - _started.getTime();	}}
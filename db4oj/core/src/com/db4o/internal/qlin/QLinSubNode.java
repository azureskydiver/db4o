/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.qlin;

import com.db4o.*;
import com.db4o.qlin.*;
import com.db4o.query.*;

/**
 * @exclude
 */
public abstract class QLinSubNode<T> extends QLinNode<T>{
	
	protected final QLinRoot<T> _root;

	public QLinSubNode(QLinRoot<T> root) {
		_root = root;
	}
	
	public QLin<T> where(Object expression) {
		return new QLinField<T>(_root, query().descend(QLinSupport.field(expression).getName()));
	}
	
	public ObjectSet<T> select() {
		return _root.select();
	}
	
	private Query query(){
		return _root.query();
	}
	
	public QLin<T> limit(int size){
		_root.limit(size);
		return this;
	}

}

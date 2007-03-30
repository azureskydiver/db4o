/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
package com.db4o.foundation;

public interface Queue4 {

	public abstract void add(Object obj);

	public abstract Object next();

	public abstract boolean hasNext();

	public abstract Iterator4 iterator();

}
/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.expr;

public interface Expression {
	
	/**
	 *  
	 * @param visitor must implement the visitor interface required
	 * by the concrete Expression implementation.
	 */
	void accept(ExpressionVisitor visitor);
}

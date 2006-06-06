package com.db4o.nativequery.expr;

public interface Expression {
	
	/**
	 *  
	 * @param visitor must implement the visitor interface required
	 * by the concrete Expression implementation.
	 */
	void accept(ExpressionVisitor visitor);
}

package com.db4o.nativequery.expr;

public interface Expression {
	
	public interface ExpressionVisitor {
	}
	
	/**
	 * <a href='http://c2.com/cgi/wiki?AcyclicVisitor'>Acyclic Visitor</a>
	 * 
	 * @param visitor must implement the visitor interface required
	 * by the concrete Expression implementation.
	 */
	void accept(ExpressionVisitor visitor);
}

/* Copyright (C) 2008  db4objects Inc.   http://www.db4o.com */
package decaf.core;

import org.eclipse.jdt.core.dom.*;

import decaf.builder.*;

public interface IterablePlatformMapping {

	String iteratorClassName();
	String iteratorNextCheckName();
	String iteratorNextElementName();

	Expression coerceIterableExpression(Expression iterableExpr, DecafASTNodeBuilder builder, DecafRewritingServices rewrite);
	Expression unwrapIterableExpression(Expression iterableExpr, DecafASTNodeBuilder builder, DecafRewritingServices rewrite);

}

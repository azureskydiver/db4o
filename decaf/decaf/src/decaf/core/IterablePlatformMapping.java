/* Copyright (C) 2008  db4objects Inc.   http://www.db4o.com */
package decaf.core;

import org.eclipse.jdt.core.dom.*;

import decaf.rewrite.*;

public interface IterablePlatformMapping {

	public static final IterablePlatformMapping JDK11_ITERABLE_MAPPING = new IterableJdk11Mapping();
	public static final IterablePlatformMapping JDK12_ITERABLE_MAPPING = new IterableJdk12Mapping();
	String iteratorClassName();
	String iteratorNextCheckName();
	String iteratorNextElementName();

	Expression coerceIterableExpression(Expression iterableExpr, DecafASTNodeBuilder builder, DecafRewritingServices rewrite);
	Expression unwrapIterableExpression(Expression iterableExpr, DecafASTNodeBuilder builder, DecafRewritingServices rewrite);

}

/* Copyright (C) 2008  db4objects Inc.   http://www.db4o.com */
package decaf.core;

import java.util.*;

import org.eclipse.jdt.core.dom.*;

import decaf.builder.*;
import decaf.rewrite.*;

public class IterableJdk12Mapping implements IterablePlatformMapping {

	private static final String FACTORY_CLASS_NAME = "com.db4o.foundation.IterableBaseFactory";

	public String iteratorClassName() {
		return Iterator.class.getName();
	}

	public String iteratorNextCheckName() {
		return "hasNext";
	}

	public String iteratorNextElementName() {
		return "next";
	}
	
	public Expression coerceIterableExpression(Expression iterableExpr, DecafASTNodeBuilder builder, DecafRewritingServices rewrite) {
		MethodInvocation coerceInvocation = builder.newMethodInvocation(builder.newQualifiedName(FACTORY_CLASS_NAME), "coerce");
		coerceInvocation.arguments().add(rewrite.safeMove(iterableExpr));
		return coerceInvocation;
	}

	public Expression unwrapIterableExpression(Expression iterableExpr, DecafASTNodeBuilder builder, DecafRewritingServices rewrite) {
		MethodInvocation unwrapInvocation = builder.newMethodInvocation(builder.newQualifiedName(FACTORY_CLASS_NAME), "unwrap");
		unwrapInvocation.arguments().add(rewrite.safeMove(iterableExpr));
		return unwrapInvocation;
	}
}
/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.expr.build;

import com.db4o.nativequery.expr.*;
import com.db4o.nativequery.expr.cmp.*;
import com.db4o.nativequery.expr.cmp.field.*;

import junit.framework.*;

public class ExpressionBuilderTest extends TestCase {
	private MockComparisonExpressionBuilder mockBuilder;
	private ExpressionBuilder builder;
	private Expression expr;
	private Expression other;
	
	protected void setUp() throws Exception {
		mockBuilder=new MockComparisonExpressionBuilder();
		builder=new ExpressionBuilder();
		expr=mockBuilder.build();
		other=mockBuilder.build();
	}
	
	public void testConstant() {
		assertSame(BoolConstExpression.TRUE,builder.constant(Boolean.TRUE));
		assertSame(BoolConstExpression.FALSE,builder.constant(Boolean.FALSE));
		// TODO: Move to const expr (or expr) test
		assertEquals(BoolConstExpression.FALSE,BoolConstExpression.expr(false));
		assertEquals(BoolConstExpression.TRUE,BoolConstExpression.expr(true));
	}

	public void testNot() {
		assertSame(BoolConstExpression.FALSE,builder.not(BoolConstExpression.TRUE));
		assertSame(BoolConstExpression.TRUE,builder.not(BoolConstExpression.FALSE));
		assertSame(BoolConstExpression.TRUE,builder.not(builder.not(BoolConstExpression.TRUE)));
		assertSame(BoolConstExpression.FALSE,builder.not(builder.not(BoolConstExpression.FALSE)));
		assertEquals(new NotExpression(expr),builder.not(expr));
		assertEquals(new ComparisonExpression(new FieldValue(CandidateFieldRoot.INSTANCE,"foo"),new ConstValue(Boolean.TRUE),ComparisonOperator.EQUALS),
					builder.not(new ComparisonExpression(new FieldValue(CandidateFieldRoot.INSTANCE,"foo"),new ConstValue(Boolean.FALSE),ComparisonOperator.EQUALS)));
	}
	
	public void testAnd() {
		assertSame(BoolConstExpression.FALSE,builder.and(BoolConstExpression.FALSE,expr));
		assertSame(BoolConstExpression.FALSE,builder.and(expr,BoolConstExpression.FALSE));
		assertSame(expr,builder.and(BoolConstExpression.TRUE,expr));
		assertSame(expr,builder.and(expr,BoolConstExpression.TRUE));
		assertEquals(expr,builder.and(expr,expr));
		assertEquals(BoolConstExpression.FALSE,builder.and(expr,builder.not(expr)));
		assertEquals(new AndExpression(expr,other),builder.and(expr,other));
	}

	public void testOr() {
		assertSame(BoolConstExpression.TRUE,builder.or(BoolConstExpression.TRUE,expr));
		assertSame(BoolConstExpression.TRUE,builder.or(expr,BoolConstExpression.TRUE));
		assertSame(expr,builder.or(BoolConstExpression.FALSE,expr));
		assertSame(expr,builder.or(expr,BoolConstExpression.FALSE));
		assertSame(expr,builder.or(expr,expr));
		assertEquals(BoolConstExpression.TRUE,builder.or(expr,builder.not(expr)));
		assertEquals(new OrExpression(expr,other),builder.or(expr,other));
	}
	
	public void testIfThenElse() {
		assertSame(expr,builder.ifThenElse(BoolConstExpression.TRUE,expr,other));
		assertSame(other,builder.ifThenElse(BoolConstExpression.FALSE,expr,other));
		assertSame(BoolConstExpression.TRUE,builder.ifThenElse(expr,BoolConstExpression.TRUE,BoolConstExpression.TRUE));
		assertSame(BoolConstExpression.FALSE,builder.ifThenElse(expr,BoolConstExpression.FALSE,BoolConstExpression.FALSE));
		assertSame(expr,builder.ifThenElse(expr,BoolConstExpression.TRUE,BoolConstExpression.FALSE));
		assertEquals(new NotExpression(expr),builder.ifThenElse(expr,BoolConstExpression.FALSE,BoolConstExpression.TRUE));
		assertEquals(builder.or(expr,other),builder.ifThenElse(expr,BoolConstExpression.TRUE,other));
		// FIXME more compund boolean constraint tests
		//assertEquals(builder.or(expr,builder.and(builder.not(expr),other)),builder.ifThenElse(expr,BoolConstExpression.TRUE,other));
	}
	
	public void testCombined() {
		Expression a=mockBuilder.build();
		Expression b=mockBuilder.build();
		Expression exp1=builder.and(a,builder.constant(Boolean.TRUE));
		Expression exp2=builder.and(BoolConstExpression.FALSE,builder.not(b));
		Expression exp=builder.or(exp1,exp2);
		assertEquals(a,exp);
	}
}

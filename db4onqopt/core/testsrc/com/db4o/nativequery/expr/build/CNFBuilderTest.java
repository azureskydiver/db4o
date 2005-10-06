package com.db4o.nativequery.expr.build;

import com.db4o.nativequery.expr.*;

import junit.framework.*;

public class CNFBuilderTest extends TestCase {
	private MockComparisonExpressionBuilder mockBuilder;
	private CNFBuilder builder;
	
	protected void setUp() {
		mockBuilder=new MockComparisonExpressionBuilder();
		builder=new CNFBuilder();
	}
	
	public void testNoChange() {
		assertNoConversion(BoolConstExpression.TRUE);
		assertNoConversion(new AndExpression(BoolConstExpression.TRUE,BoolConstExpression.FALSE));
		assertNoConversion(new OrExpression(BoolConstExpression.TRUE,BoolConstExpression.FALSE));
		Expression simpleCNF=new AndExpression(
				new OrExpression(new NotExpression(mockBuilder.build()),mockBuilder.build()),
				new OrExpression(mockBuilder.build(),mockBuilder.build())
		);
		assertNoConversion(simpleCNF);
	}
	
	public void testBaseDoubleNegation() {
		assertConversion(BoolConstExpression.FALSE,new NotExpression(BoolConstExpression.TRUE));
		assertConversion(BoolConstExpression.TRUE,new NotExpression(new NotExpression(BoolConstExpression.TRUE)));
		assertConversion(BoolConstExpression.FALSE,new NotExpression(new NotExpression(new NotExpression(BoolConstExpression.TRUE))));
	}

	public void testBaseDeMorgan() {
		assertConversion(new AndExpression(new NotExpression(BoolConstExpression.TRUE),new NotExpression(BoolConstExpression.FALSE)),new NotExpression(new OrExpression(BoolConstExpression.TRUE,BoolConstExpression.FALSE)));
		assertConversion(new OrExpression(new NotExpression(BoolConstExpression.TRUE),new NotExpression(BoolConstExpression.FALSE)),new NotExpression(new AndExpression(BoolConstExpression.TRUE,BoolConstExpression.FALSE)));
	}
	
	private void assertNoConversion(Expression source) {
		assertConversion(source,source);
	}
	
	private void assertConversion(Expression expected,Expression source) {
		assertEquals(expected,builder.cnf(source));
	}
}

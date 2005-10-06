package com.db4o.nativequery.optimization.db4o;

import junit.framework.*;

import com.db4o.nativequery.expr.*;
import com.db4o.nativequery.expr.cmp.*;
import com.db4o.nativequery.optimization.SODAQueryBuilder;
import com.db4o.query.*;

public class SODABloatQueryBuilderTest extends TestCase {
	private QueryMockBuilder builder;
	
	protected void setUp() throws Exception {
		builder=new QueryMockBuilder();
	}
	
	public void testSimpleEqualsComparison() {
		assertSimpleComparison(ComparisonOperator.EQUALS,false);
	}

	public void testSimpleSmallerComparison() {
		assertSimpleComparison(ComparisonOperator.SMALLER,false);
	}

	public void testSimpleGreaterComparison() {
		assertSimpleComparison(ComparisonOperator.GREATER,false);
	}

	public void testSimpleNotEqualsComparison() {
		assertSimpleComparison(ComparisonOperator.EQUALS,true);
	}

	public void testSimpleSmallerNotComparison() {
		assertSimpleComparison(ComparisonOperator.SMALLER,true);
	}

	public void testSimpleGreaterNotComparison() {
		assertSimpleComparison(ComparisonOperator.GREATER,true);
	}

	private void assertSimpleComparison(ComparisonOperator op,boolean negated) {
		ComparisonExpression cmpExpr=simpleComparison("foo","bar",op);
		Expression expr=cmpExpr;
		if(negated) {
			expr=new NotExpression(expr);
		}
		
		Query query=builder.query();
		Query subquery=builder.assertDescend(query,(String)cmpExpr.left().fieldNames().next());
		Constraint constraint=builder.assertConstrain(subquery,((ConstValue)cmpExpr.right()).value());
		builder.assertOperator(constraint,op);
		builder.assertNegated(constraint,negated);

		builder.replay();
		
		new SODAQueryBuilder().optimizeQuery(expr,query,null);

		builder.verify();
	}

	private ComparisonExpression simpleComparison(String fieldName,Object value,ComparisonOperator op) {
		FieldValue left=new FieldValue(1,fieldName);
		ConstValue right=new ConstValue(value);
		return new ComparisonExpression(left,right,op);
	}	
}

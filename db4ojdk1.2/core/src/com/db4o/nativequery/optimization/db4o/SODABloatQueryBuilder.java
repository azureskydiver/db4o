package com.db4o.nativequery.optimization.db4o;

import java.lang.reflect.*;
import java.util.*;

import com.db4o.nativequery.expr.*;
import com.db4o.nativequery.expr.cmp.*;
import com.db4o.query.*;

public class SODABloatQueryBuilder {		
	private class SODABloatQueryVisitor implements DiscriminatingExpressionVisitor {
		private Predicate filter;
		private Query query;
		private Constraint constraint;

		private SODABloatQueryVisitor(Query query,Predicate filter) {
			this.query=query;
			this.filter=filter;
		}
		
		public void visit(AndExpression expression) {
			expression.left().accept(this);
			Constraint left=constraint;
			expression.right().accept(this);
			left.and(constraint);
			constraint=left;
		}

		public void visit(BoolConstExpression expression) {
			throw new RuntimeException("No boolean constants expected in parsed expression tree");
		}

		public void visit(OrExpression expression) {
			expression.left().accept(this);
			Constraint left=constraint;
			expression.right().accept(this);
			left.or(constraint);
			constraint=left;
		}

		public void visit(ComparisonExpression expression) {
			Query subQuery=query;
			Iterator fieldNames = expression.left().fieldNames();
			while(fieldNames.hasNext()) {
				subQuery=subQuery.descend((String)fieldNames.next());
			}
			final Object[] value={null};
			expression.right().accept(new DiscriminatingComparisonOperandVisitor() {				
				public void visit(ConstValue operand) {
					value[0] = operand.value();
				}

				public void visit(FieldValue operand) {
					value[0]=findValue(operand);
				}

				public void visit(ArithmeticExpression operand) {
					operand.left().accept(this);
					// FIXME urgently
					int left=((Integer)value[0]).intValue();
					operand.right().accept(this);
					int right=((Integer)value[0]).intValue();
					int result=0;
					switch(operand.op().id()) {
						case ArithmeticOperator.ADD_ID: 
							result=left+right;
							break;
						case ArithmeticOperator.SUBTRACT_ID: 
							result=left-right;
							break;
						case ArithmeticOperator.MULTIPLY_ID: 
							result=left*right;
							break;
						case ArithmeticOperator.DIVIDE_ID: 
							result=left/right;
							break;
					}
					value[0]=Integer.valueOf(result);
				}
				
			});
			constraint=subQuery.constrain(value[0]);
			if(!expression.op().equals(ComparisonOperator.EQUALS)) {
				if(expression.op().equals(ComparisonOperator.GREATER)) {
					constraint.greater();
				}
				else {
					constraint.smaller();
				}
			}
		}

		private Object findValue(FieldValue spec) {
			Object value=filter;
			try {
				Iterator fieldNames=spec.fieldNames();
				while(fieldNames.hasNext()) {
					// FIXME declared is not enough
					Field field=value.getClass().getDeclaredField((String)fieldNames.next());
					field.setAccessible(true);
					value=field.get(value);
				}
				return value;
			} catch (Exception exc) {
				exc.printStackTrace();
				return null;
			}
		}

		public void visit(NotExpression expression) {
			expression.expr().accept(this);
			constraint.not();
		}

	}
	
	public void optimizeQuery(Expression expr,Query query,Predicate filter) {
		expr.accept(new SODABloatQueryVisitor(query,filter));
	}	
}

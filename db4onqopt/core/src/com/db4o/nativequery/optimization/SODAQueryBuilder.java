package com.db4o.nativequery.optimization;

import java.lang.reflect.Field;

import com.db4o.Platform4;
import com.db4o.foundation.Iterator4;
import com.db4o.nativequery.expr.*;
import com.db4o.nativequery.expr.cmp.*;
import com.db4o.query.*;

public class SODAQueryBuilder {		
	private static class SODAQueryVisitor implements DiscriminatingExpressionVisitor {
		private Object _predicate;
		private Query _query;
		private Constraint _constraint;

		SODAQueryVisitor(Query query, Object predicate) {
			_query=query;
			_predicate = predicate;
		}
		
		public void visit(AndExpression expression) {
			expression.left().accept(this);
			Constraint left=_constraint;
			expression.right().accept(this);
			left.and(_constraint);
			_constraint=left;
		}

		public void visit(BoolConstExpression expression) {
			//throw new RuntimeException("No boolean constants expected in parsed expression tree");
		}

		public void visit(OrExpression expression) {
			expression.left().accept(this);
			Constraint left=_constraint;
			expression.right().accept(this);
			left.or(_constraint);
			_constraint=left;
		}

		public void visit(ComparisonExpression expression) {
			Query subQuery=_query;
			Iterator4 fieldNames = expression.left().fieldNames();
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

				private Number add(Number a,Number b) {
					if(a instanceof Double||b instanceof Double) {
						return new Double(a.doubleValue()+b.doubleValue());
					}
					if(a instanceof Float||b instanceof Float) {
						return new Float(a.floatValue()+b.floatValue());
					}
					if(a instanceof Long||b instanceof Long) {
						return new Long(a.longValue()+b.longValue());
					}
					return new Integer(a.intValue()+b.intValue());
				}

				private Number subtract(Number a,Number b) {
					if(a instanceof Double||b instanceof Double) {
						return new Double(a.doubleValue()-b.doubleValue());
					}
					if(a instanceof Float||b instanceof Float) {
						return new Float(a.floatValue()-b.floatValue());
					}
					if(a instanceof Long||b instanceof Long) {
						return new Long(a.longValue()-b.longValue());
					}
					return new Integer(a.intValue()-b.intValue());
				}

				private Number multiply(Number a,Number b) {
					if(a instanceof Double||b instanceof Double) {
						return new Double(a.doubleValue()*b.doubleValue());
					}
					if(a instanceof Float||b instanceof Float) {
						return new Float(a.floatValue()*b.floatValue());
					}
					if(a instanceof Long||b instanceof Long) {
						return new Long(a.longValue()*b.longValue());
					}
					return new Integer(a.intValue()*b.intValue());
				}

				private Number divide(Number a,Number b) {
					if(a instanceof Double||b instanceof Double) {
						return new Double(a.doubleValue()/b.doubleValue());
					}
					if(a instanceof Float||b instanceof Float) {
						return new Float(a.floatValue()/b.floatValue());
					}
					if(a instanceof Long||b instanceof Long) {
						return new Long(a.longValue()/b.longValue());
					}
					return new Integer(a.intValue()/b.intValue());
				}

				public void visit(ArithmeticExpression operand) {
					operand.left().accept(this);
					Number left=(Number)value[0];
					operand.right().accept(this);
					Number right=(Number)value[0];
					switch(operand.op().id()) {
						case ArithmeticOperator.ADD_ID: 
							value[0]=add(left,right);
							break;
						case ArithmeticOperator.SUBTRACT_ID: 
							value[0]=subtract(left,right);
							break;
						case ArithmeticOperator.MULTIPLY_ID: 
							value[0]=multiply(left,right);
							break;
						case ArithmeticOperator.DIVIDE_ID: 
							value[0]=divide(left,right);
							break;
					}
				}
				
			});
			_constraint=subQuery.constrain(value[0]);
			if(!expression.op().equals(ComparisonOperator.EQUALS)) {
				if(expression.op().equals(ComparisonOperator.GREATER)) {
					_constraint.greater();
				}
				else {
					_constraint.smaller();
				}
			}
		}

		private Object findValue(FieldValue spec) {
			Object value=_predicate;
			try {
				Iterator4 fieldNames=spec.fieldNames();
				while(fieldNames.hasNext()) {
					// FIXME declared is not enough
					Field field=value.getClass().getDeclaredField((String)fieldNames.next());
					Platform4.setAccessible(field);
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
			_constraint.not();
		}

	}
	
	public void optimizeQuery(Expression expr, Query query, Object predicate) {
		expr.accept(new SODAQueryVisitor(query, predicate));
	}	
}

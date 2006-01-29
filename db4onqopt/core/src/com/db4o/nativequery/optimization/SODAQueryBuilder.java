package com.db4o.nativequery.optimization;

import java.lang.reflect.*;

import com.db4o.*;
import com.db4o.foundation.Iterator4;
import com.db4o.nativequery.expr.*;
import com.db4o.nativequery.expr.cmp.*;
import com.db4o.nativequery.expr.cmp.field.*;
import com.db4o.query.*;

public class SODAQueryBuilder {		
	private static class SODAQueryVisitor implements ExpressionVisitor {
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
			expression.right().accept(new ComparisonOperandVisitor() {				
				public void visit(ConstValue operand) {
					value[0] = operand.value();
				}

				public void visit(FieldValue operand) {
					value[0]=findValue(operand);
				}

				private Object add(Object a,Object b) {
					if(a instanceof Double||b instanceof Double) {
						return new Double(((Double)a).doubleValue()+ ((Double)b).doubleValue());
					}
					if(a instanceof Float||b instanceof Float) {
						return new Float(((Float)a).floatValue()+ ((Float)b).floatValue());
					}
					if(a instanceof Long||b instanceof Long) {
						return new Long(((Long)a).longValue()+ ((Long)b).longValue());
					}
					return new Integer(((Integer)a).intValue()+ ((Integer)b).intValue());
				}

				private Object subtract(Object a,Object b) {
					if(a instanceof Double||b instanceof Double) {
                        return new Double(((Double)a).doubleValue()- ((Double)b).doubleValue());
					}
					if(a instanceof Float||b instanceof Float) {
                        return new Float(((Float)a).floatValue() - ((Float)b).floatValue());
					}
					if(a instanceof Long||b instanceof Long) {
                        return new Long(((Long)a).longValue() - ((Long)b).longValue());
					}
                    return new Integer(((Integer)a).intValue() - ((Integer)b).intValue());
				}

				private Object multiply(Object a,Object b) {
                    if(a instanceof Double||b instanceof Double) {
                        return new Double(((Double)a).doubleValue() * ((Double)b).doubleValue());
                    }
                    if(a instanceof Float||b instanceof Float) {
                        return new Float(((Float)a).floatValue() * ((Float)b).floatValue());
                    }
                    if(a instanceof Long||b instanceof Long) {
                        return new Long(((Long)a).longValue() * ((Long)b).longValue());
                    }
                    return new Integer(((Integer)a).intValue() * ((Integer)b).intValue());
				}

				private Object divide(Object a,Object b) {
                    if(a instanceof Double||b instanceof Double) {
                        return new Double(((Double)a).doubleValue()/ ((Double)b).doubleValue());
                    }
                    if(a instanceof Float||b instanceof Float) {
                        return new Float(((Float)a).floatValue() / ((Float)b).floatValue());
                    }
                    if(a instanceof Long||b instanceof Long) {
                        return new Long(((Long)a).longValue() / ((Long)b).longValue());
                    }
                    return new Integer(((Integer)a).intValue() / ((Integer)b).intValue());
				}

				public void visit(ArithmeticExpression operand) {
					operand.left().accept(this);
					Object left=value[0];
					operand.right().accept(this);
					Object right=value[0];
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

		private Field fieldFor(final Class clazz,final String name) {
			Class curclazz=clazz;
			while(curclazz!=null) {
				try {
					Field field=curclazz.getDeclaredField(name);
					Platform4.setAccessible(field);
					return field;
				} catch (Exception e) {
				}
				curclazz=curclazz.getSuperclass();
			}
			return null;
		}
		
		private Object findValue(FieldValue spec) {
			if(spec.root() instanceof StaticFieldRoot) {
				StaticFieldRoot root=(StaticFieldRoot)spec.root();
				try {
					Class clazz=Class.forName(root.className());
					// FIXME need declared for private fields
					Field field=fieldFor(clazz,(String)spec.fieldNames().next());
					return field.get(null);
				} catch (Exception exc) {
					throw new RuntimeException("Unable to resolve static field: "+spec);
				}
			}
			Object value=_predicate;
			Iterator4 fieldNames=spec.fieldNames();
			while(fieldNames.hasNext()) {
                String fieldName = (String)fieldNames.next();
                Class clazz = value.getClass();
                while(clazz != null){
                    try{
                        Field field=clazz.getDeclaredField(fieldName);
                        Platform4.setAccessible(field);
                        value=field.get(value);
                        return value;
                    }catch(Exception e){
                        // e.printStackTrace();
                    }
                    clazz = clazz.getSuperclass();
                    if(clazz == YapConst.CLASS_OBJECT){
                        return null;
                    }
                }
			}
			return value;
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

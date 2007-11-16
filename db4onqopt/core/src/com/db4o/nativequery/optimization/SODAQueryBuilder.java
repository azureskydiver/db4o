/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.optimization;

import com.db4o.foundation.*;
import com.db4o.instrumentation.core.*;
import com.db4o.nativequery.expr.*;
import com.db4o.nativequery.expr.cmp.*;
import com.db4o.nativequery.expr.cmp.operand.*;
import com.db4o.query.*;

public class SODAQueryBuilder {		
	private static class SODAQueryVisitor implements ExpressionVisitor {
		private Object _predicate;
		private Query _query;
		private Constraint _constraint;
		private NativeClassFactory _classSource;

		SODAQueryVisitor(Query query, Object predicate, NativeClassFactory classSource) {
			_query=query;
			_predicate = predicate;
			_classSource = classSource;
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
			Query subQuery = _query;
			Iterator4 fieldNameIterator = fieldNames(expression.left());
			while (fieldNameIterator.moveNext()) {
				subQuery = subQuery.descend((String) fieldNameIterator
						.current());
			}
			ComparisonQueryGeneratingVisitor visitor = new ComparisonQueryGeneratingVisitor(
					_predicate, _classSource);
			expression.right().accept(visitor);
			_constraint = subQuery.constrain(visitor.value());
			ComparisonOperator op = expression.op();
			if (op.equals(ComparisonOperator.EQUALS)) {
				return;
			}
			if (op.equals(ComparisonOperator.IDENTITY)) {
				_constraint.identity();
				return;
			}
			if (op.equals(ComparisonOperator.GREATER)) {
				_constraint.greater();
				return;
			}
			if (op.equals(ComparisonOperator.SMALLER)) {
				_constraint.smaller();
				return;
			} 
			if (op.equals(ComparisonOperator.CONTAINS)) {
				_constraint.contains();
				return;
			}
			if (op.equals(ComparisonOperator.STARTSWITH)) {
				_constraint.startsWith(true);
				return;
			}
			if (op.equals(ComparisonOperator.ENDSWITH)) {
				_constraint.endsWith(true);
				return;
			}
			throw new RuntimeException("Can't handle constraint: "
					+ op);
		}

		public void visit(NotExpression expression) {
			expression.expr().accept(this);
			_constraint.not();
		}

		private Iterator4 fieldNames(FieldValue fieldValue) {
			Collection4 coll=new Collection4();
			ComparisonOperand curOp=fieldValue;
			while(curOp instanceof FieldValue) {
				FieldValue curField=(FieldValue)curOp;
				coll.prepend(curField.fieldName());
				curOp=curField.parent();
			}
			return coll.iterator();
		}
	}
		
	public void optimizeQuery(Expression expr, Query query, Object predicate, NativeClassFactory classSource) {
		expr.accept(new SODAQueryVisitor(query, predicate, classSource));
	}	
}

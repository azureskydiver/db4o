namespace com.db4o.nativequery.optimization
{
	public class SODAQueryBuilder
	{
		private class SODAQueryVisitor : com.db4o.nativequery.expr.ExpressionVisitor
		{
			private object _predicate;

			private com.db4o.query.Query _query;

			private com.db4o.query.Constraint _constraint;

			internal SODAQueryVisitor(com.db4o.query.Query query, object predicate)
			{
				_query = query;
				_predicate = predicate;
			}

			public virtual void visit(com.db4o.nativequery.expr.AndExpression expression)
			{
				expression.left().accept(this);
				com.db4o.query.Constraint left = _constraint;
				expression.right().accept(this);
				left.and(_constraint);
				_constraint = left;
			}

			public virtual void visit(com.db4o.nativequery.expr.BoolConstExpression expression
				)
			{
			}

			public virtual void visit(com.db4o.nativequery.expr.OrExpression expression)
			{
				expression.left().accept(this);
				com.db4o.query.Constraint left = _constraint;
				expression.right().accept(this);
				left.or(_constraint);
				_constraint = left;
			}

			public virtual void visit(com.db4o.nativequery.expr.ComparisonExpression expression
				)
			{
				com.db4o.query.Query subQuery = _query;
				com.db4o.foundation.Iterator4 fieldNameIterator = fieldNames(expression.left());
				while (fieldNameIterator.hasNext())
				{
					subQuery = subQuery.descend((string)fieldNameIterator.next());
				}
				com.db4o.nativequery.optimization.ComparisonQueryGeneratingVisitor visitor = new 
					com.db4o.nativequery.optimization.ComparisonQueryGeneratingVisitor(_predicate);
				expression.right().accept(visitor);
				_constraint = subQuery.constrain(visitor.value());
				if (!expression.op().Equals(com.db4o.nativequery.expr.cmp.ComparisonOperator.EQUALS
					))
				{
					if (expression.op().Equals(com.db4o.nativequery.expr.cmp.ComparisonOperator.GREATER
						))
					{
						_constraint.greater();
					}
					else
					{
						if (expression.op().Equals(com.db4o.nativequery.expr.cmp.ComparisonOperator.SMALLER
							))
						{
							_constraint.smaller();
						}
						else
						{
							if (expression.op().Equals(com.db4o.nativequery.expr.cmp.ComparisonOperator.CONTAINS
								))
							{
								_constraint.contains();
							}
							else
							{
								if (expression.op().Equals(com.db4o.nativequery.expr.cmp.ComparisonOperator.STARTSWITH
									))
								{
									_constraint.startsWith(true);
								}
								else
								{
									if (expression.op().Equals(com.db4o.nativequery.expr.cmp.ComparisonOperator.ENDSWITH
										))
									{
										_constraint.endsWith(true);
									}
									else
									{
										throw new j4o.lang.RuntimeException("Can't handle constraint: " + expression.op()
											);
									}
								}
							}
						}
					}
				}
			}

			public virtual void visit(com.db4o.nativequery.expr.NotExpression expression)
			{
				expression.expr().accept(this);
				_constraint.not();
			}

			private com.db4o.foundation.Iterator4 fieldNames(com.db4o.nativequery.expr.cmp.FieldValue
				 fieldValue)
			{
				com.db4o.foundation.Collection4 coll = new com.db4o.foundation.Collection4();
				com.db4o.nativequery.expr.cmp.ComparisonOperand curOp = fieldValue;
				while (curOp is com.db4o.nativequery.expr.cmp.FieldValue)
				{
					com.db4o.nativequery.expr.cmp.FieldValue curField = (com.db4o.nativequery.expr.cmp.FieldValue
						)curOp;
					coll.add(curField.fieldName());
					curOp = curField.parent();
				}
				return coll.iterator();
			}
		}

		public virtual void optimizeQuery(com.db4o.nativequery.expr.Expression expr, com.db4o.query.Query
			 query, object predicate)
		{
			expr.accept(new com.db4o.nativequery.optimization.SODAQueryBuilder.SODAQueryVisitor
				(query, predicate));
		}
	}
}

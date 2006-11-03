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

			public virtual void Visit(com.db4o.nativequery.expr.AndExpression expression)
			{
				expression.Left().Accept(this);
				com.db4o.query.Constraint left = _constraint;
				expression.Right().Accept(this);
				left.And(_constraint);
				_constraint = left;
			}

			public virtual void Visit(com.db4o.nativequery.expr.BoolConstExpression expression
				)
			{
			}

			public virtual void Visit(com.db4o.nativequery.expr.OrExpression expression)
			{
				expression.Left().Accept(this);
				com.db4o.query.Constraint left = _constraint;
				expression.Right().Accept(this);
				left.Or(_constraint);
				_constraint = left;
			}

			public virtual void Visit(com.db4o.nativequery.expr.ComparisonExpression expression
				)
			{
				com.db4o.query.Query subQuery = _query;
				System.Collections.IEnumerator fieldNameIterator = FieldNames(expression.Left());
				while (fieldNameIterator.MoveNext())
				{
					subQuery = subQuery.Descend((string)fieldNameIterator.Current);
				}
				com.db4o.nativequery.optimization.ComparisonQueryGeneratingVisitor visitor = new 
					com.db4o.nativequery.optimization.ComparisonQueryGeneratingVisitor(_predicate);
				expression.Right().Accept(visitor);
				_constraint = subQuery.Constrain(visitor.Value());
				if (!expression.Op().Equals(com.db4o.nativequery.expr.cmp.ComparisonOperator.EQUALS
					))
				{
					if (expression.Op().Equals(com.db4o.nativequery.expr.cmp.ComparisonOperator.GREATER
						))
					{
						_constraint.Greater();
					}
					else
					{
						if (expression.Op().Equals(com.db4o.nativequery.expr.cmp.ComparisonOperator.SMALLER
							))
						{
							_constraint.Smaller();
						}
						else
						{
							if (expression.Op().Equals(com.db4o.nativequery.expr.cmp.ComparisonOperator.CONTAINS
								))
							{
								_constraint.Contains();
							}
							else
							{
								if (expression.Op().Equals(com.db4o.nativequery.expr.cmp.ComparisonOperator.STARTSWITH
									))
								{
									_constraint.StartsWith(true);
								}
								else
								{
									if (expression.Op().Equals(com.db4o.nativequery.expr.cmp.ComparisonOperator.ENDSWITH
										))
									{
										_constraint.EndsWith(true);
									}
									else
									{
										throw new System.Exception("Can't handle constraint: " + expression.Op());
									}
								}
							}
						}
					}
				}
			}

			public virtual void Visit(com.db4o.nativequery.expr.NotExpression expression)
			{
				expression.Expr().Accept(this);
				_constraint.Not();
			}

			private System.Collections.IEnumerator FieldNames(com.db4o.nativequery.expr.cmp.FieldValue
				 fieldValue)
			{
				com.db4o.foundation.Collection4 coll = new com.db4o.foundation.Collection4();
				com.db4o.nativequery.expr.cmp.ComparisonOperand curOp = fieldValue;
				while (curOp is com.db4o.nativequery.expr.cmp.FieldValue)
				{
					com.db4o.nativequery.expr.cmp.FieldValue curField = (com.db4o.nativequery.expr.cmp.FieldValue
						)curOp;
					coll.Prepend(curField.FieldName());
					curOp = curField.Parent();
				}
				return coll.GetEnumerator();
			}
		}

		public virtual void OptimizeQuery(com.db4o.nativequery.expr.Expression expr, com.db4o.query.Query
			 query, object predicate)
		{
			expr.Accept(new com.db4o.nativequery.optimization.SODAQueryBuilder.SODAQueryVisitor
				(query, predicate));
		}
	}
}

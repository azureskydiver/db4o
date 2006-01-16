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
				com.db4o.foundation.Iterator4 fieldNames = expression.left().fieldNames();
				while (fieldNames.hasNext())
				{
					subQuery = subQuery.descend((string)fieldNames.next());
				}
				object[] value = { null };
				expression.right().accept(new _AnonymousInnerClass48(this, value));
				_constraint = subQuery.constrain(value[0]);
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
						_constraint.smaller();
					}
				}
			}

			private sealed class _AnonymousInnerClass48 : com.db4o.nativequery.expr.cmp.ComparisonOperandVisitor
			{
				public _AnonymousInnerClass48(SODAQueryVisitor _enclosing, object[] value)
				{
					this._enclosing = _enclosing;
					this.value = value;
				}

				public void visit(com.db4o.nativequery.expr.cmp.ConstValue operand)
				{
					value[0] = operand.value();
				}

				public void visit(com.db4o.nativequery.expr.cmp.FieldValue operand)
				{
					value[0] = this._enclosing.findValue(operand);
				}

				private object add(object a, object b)
				{
					if (a is double || b is double)
					{
						return ((double)a) + ((double)b);
					}
					if (a is float || b is float)
					{
						return ((float)a) + ((float)b);
					}
					if (a is long || b is long)
					{
						return ((long)a) + ((long)b);
					}
					return ((int)a) + ((int)b);
				}

				private object subtract(object a, object b)
				{
					if (a is double || b is double)
					{
						return ((double)a) - ((double)b);
					}
					if (a is float || b is float)
					{
						return ((float)a) - ((float)b);
					}
					if (a is long || b is long)
					{
						return ((long)a) - ((long)b);
					}
					return ((int)a) - ((int)b);
				}

				private object multiply(object a, object b)
				{
					if (a is double || b is double)
					{
						return ((double)a) * ((double)b);
					}
					if (a is float || b is float)
					{
						return ((float)a) * ((float)b);
					}
					if (a is long || b is long)
					{
						return ((long)a) * ((long)b);
					}
					return ((int)a) * ((int)b);
				}

				private object divide(object a, object b)
				{
					if (a is double || b is double)
					{
						return ((double)a) / ((double)b);
					}
					if (a is float || b is float)
					{
						return ((float)a) / ((float)b);
					}
					if (a is long || b is long)
					{
						return ((long)a) / ((long)b);
					}
					return ((int)a) / ((int)b);
				}

				public void visit(com.db4o.nativequery.expr.cmp.ArithmeticExpression operand)
				{
					operand.left().accept(this);
					object left = value[0];
					operand.right().accept(this);
					object right = value[0];
					switch (operand.op().id())
					{
						case com.db4o.nativequery.expr.cmp.ArithmeticOperator.ADD_ID:
						{
							value[0] = this.add(left, right);
							break;
						}

						case com.db4o.nativequery.expr.cmp.ArithmeticOperator.SUBTRACT_ID:
						{
							value[0] = this.subtract(left, right);
							break;
						}

						case com.db4o.nativequery.expr.cmp.ArithmeticOperator.MULTIPLY_ID:
						{
							value[0] = this.multiply(left, right);
							break;
						}

						case com.db4o.nativequery.expr.cmp.ArithmeticOperator.DIVIDE_ID:
						{
							value[0] = this.divide(left, right);
							break;
						}
					}
				}

				private readonly SODAQueryVisitor _enclosing;

				private readonly object[] value;
			}

			private object findValue(com.db4o.nativequery.expr.cmp.FieldValue spec)
			{
				object value = _predicate;
				com.db4o.foundation.Iterator4 fieldNames = spec.fieldNames();
				while (fieldNames.hasNext())
				{
					string fieldName = (string)fieldNames.next();
					j4o.lang.Class clazz = j4o.lang.Class.getClassForObject(value);
					while (clazz != null)
					{
						try
						{
							j4o.lang.reflect.Field field = clazz.getDeclaredField(fieldName);
							com.db4o.Platform4.setAccessible(field);
							value = field.get(value);
							return value;
						}
						catch (System.Exception e)
						{
						}
						clazz = clazz.getSuperclass();
						if (clazz == com.db4o.YapConst.CLASS_OBJECT)
						{
							return null;
						}
					}
				}
				return value;
			}

			public virtual void visit(com.db4o.nativequery.expr.NotExpression expression)
			{
				expression.expr().accept(this);
				_constraint.not();
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

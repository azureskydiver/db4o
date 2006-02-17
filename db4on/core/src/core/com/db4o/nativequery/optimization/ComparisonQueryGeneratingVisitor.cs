namespace com.db4o.nativequery.optimization
{
	internal sealed class ComparisonQueryGeneratingVisitor : com.db4o.nativequery.expr.cmp.ComparisonOperandVisitor
	{
		private object _predicate;

		private object _value = null;

		public object value()
		{
			return _value;
		}

		public void visit(com.db4o.nativequery.expr.cmp.ConstValue operand)
		{
			_value = operand.value();
		}

		public void visit(com.db4o.nativequery.expr.cmp.FieldValue operand)
		{
			operand.parent().accept(this);
			j4o.lang.Class clazz = ((operand.parent() is com.db4o.nativequery.expr.cmp.field.StaticFieldRoot
				) ? (j4o.lang.Class)_value : j4o.lang.Class.getClassForObject(_value));
			try
			{
				j4o.lang.reflect.Field field = com.db4o.nativequery.optimization.ReflectUtil.fieldFor
					(clazz, operand.fieldName());
				_value = field.get(_value);
			}
			catch (System.Exception exc)
			{
				j4o.lang.JavaSystem.printStackTrace(exc);
			}
		}

		internal object add(object a, object b)
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

		internal object subtract(object a, object b)
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

		internal object multiply(object a, object b)
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

		internal object divide(object a, object b)
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
			object left = _value;
			operand.right().accept(this);
			object right = _value;
			switch (operand.op().id())
			{
				case com.db4o.nativequery.expr.cmp.ArithmeticOperator.ADD_ID:
				{
					_value = add(left, right);
					break;
				}

				case com.db4o.nativequery.expr.cmp.ArithmeticOperator.SUBTRACT_ID:
				{
					_value = subtract(left, right);
					break;
				}

				case com.db4o.nativequery.expr.cmp.ArithmeticOperator.MULTIPLY_ID:
				{
					_value = multiply(left, right);
					break;
				}

				case com.db4o.nativequery.expr.cmp.ArithmeticOperator.DIVIDE_ID:
				{
					_value = divide(left, right);
					break;
				}
			}
		}

		public void visit(com.db4o.nativequery.expr.cmp.field.CandidateFieldRoot root)
		{
		}

		public void visit(com.db4o.nativequery.expr.cmp.field.PredicateFieldRoot root)
		{
			_value = _predicate;
		}

		public void visit(com.db4o.nativequery.expr.cmp.field.StaticFieldRoot root)
		{
			try
			{
				_value = j4o.lang.Class.forName(root.className());
			}
			catch (j4o.lang.ClassNotFoundException e)
			{
				j4o.lang.JavaSystem.printStackTrace(e);
			}
		}

		public void visit(com.db4o.nativequery.expr.cmp.ArrayAccessValue operand)
		{
			operand.parent().accept(this);
			object parent = _value;
			operand.index().accept(this);
			int index = (int)_value;
			_value = j4o.lang.reflect.JavaArray.get(parent, index);
		}

		public void visit(com.db4o.nativequery.expr.cmp.MethodCallValue operand)
		{
			operand.parent().accept(this);
			object receiver = _value;
			object[] _params = new object[operand.args().Length];
			for (int paramIdx = 0; paramIdx < operand.args().Length; paramIdx++)
			{
				operand.args()[paramIdx].accept(this);
				_params[paramIdx] = _value;
			}
			j4o.lang.Class clazz = j4o.lang.Class.getClassForObject(receiver);
			if (operand.parent().root() is com.db4o.nativequery.expr.cmp.field.StaticFieldRoot
				)
			{
				clazz = (j4o.lang.Class)receiver;
			}
			j4o.lang.reflect.Method method = com.db4o.nativequery.optimization.ReflectUtil.methodFor
				(clazz, operand.methodName(), operand.paramTypes());
			try
			{
				_value = method.invoke(receiver, _params);
			}
			catch (System.Exception exc)
			{
				j4o.lang.JavaSystem.printStackTrace(exc);
				_value = null;
			}
		}

		public ComparisonQueryGeneratingVisitor(object predicate) : base()
		{
			this._predicate = predicate;
		}
	}
}

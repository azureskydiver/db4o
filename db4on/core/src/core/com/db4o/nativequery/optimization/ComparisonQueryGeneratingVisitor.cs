namespace com.db4o.nativequery.optimization
{
	internal sealed class ComparisonQueryGeneratingVisitor : com.db4o.nativequery.expr.cmp.ComparisonOperandVisitor
	{
		private object _predicate;

		private object _value = null;

		public object Value()
		{
			return _value;
		}

		public void Visit(com.db4o.nativequery.expr.cmp.ConstValue operand)
		{
			_value = operand.Value();
		}

		public void Visit(com.db4o.nativequery.expr.cmp.FieldValue operand)
		{
			operand.Parent().Accept(this);
			j4o.lang.Class clazz = ((operand.Parent() is com.db4o.nativequery.expr.cmp.field.StaticFieldRoot
				) ? (j4o.lang.Class)_value : j4o.lang.Class.GetClassForObject(_value));
			try
			{
				j4o.lang.reflect.Field field = com.db4o.nativequery.optimization.ReflectUtil.FieldFor
					(clazz, operand.FieldName());
				_value = field.Get(_value);
			}
			catch (System.Exception exc)
			{
				j4o.lang.JavaSystem.PrintStackTrace(exc);
			}
		}

		internal object Add(object a, object b)
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

		internal object Subtract(object a, object b)
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

		internal object Multiply(object a, object b)
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

		internal object Divide(object a, object b)
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

		public void Visit(com.db4o.nativequery.expr.cmp.ArithmeticExpression operand)
		{
			operand.Left().Accept(this);
			object left = _value;
			operand.Right().Accept(this);
			object right = _value;
			switch (operand.Op().Id())
			{
				case com.db4o.nativequery.expr.cmp.ArithmeticOperator.ADD_ID:
				{
					_value = Add(left, right);
					break;
				}

				case com.db4o.nativequery.expr.cmp.ArithmeticOperator.SUBTRACT_ID:
				{
					_value = Subtract(left, right);
					break;
				}

				case com.db4o.nativequery.expr.cmp.ArithmeticOperator.MULTIPLY_ID:
				{
					_value = Multiply(left, right);
					break;
				}

				case com.db4o.nativequery.expr.cmp.ArithmeticOperator.DIVIDE_ID:
				{
					_value = Divide(left, right);
					break;
				}
			}
		}

		public void Visit(com.db4o.nativequery.expr.cmp.field.CandidateFieldRoot root)
		{
		}

		public void Visit(com.db4o.nativequery.expr.cmp.field.PredicateFieldRoot root)
		{
			_value = _predicate;
		}

		public void Visit(com.db4o.nativequery.expr.cmp.field.StaticFieldRoot root)
		{
			try
			{
				_value = j4o.lang.Class.ForName(root.ClassName());
			}
			catch (j4o.lang.ClassNotFoundException e)
			{
				j4o.lang.JavaSystem.PrintStackTrace(e);
			}
		}

		public void Visit(com.db4o.nativequery.expr.cmp.ArrayAccessValue operand)
		{
			operand.Parent().Accept(this);
			object parent = _value;
			operand.Index().Accept(this);
			int index = (int)_value;
			_value = j4o.lang.reflect.JavaArray.Get(parent, index);
		}

		public void Visit(com.db4o.nativequery.expr.cmp.MethodCallValue operand)
		{
			operand.Parent().Accept(this);
			object receiver = _value;
			object[] _params = new object[operand.Args().Length];
			for (int paramIdx = 0; paramIdx < operand.Args().Length; paramIdx++)
			{
				operand.Args()[paramIdx].Accept(this);
				_params[paramIdx] = _value;
			}
			j4o.lang.Class clazz = j4o.lang.Class.GetClassForObject(receiver);
			if (operand.Parent().Root() is com.db4o.nativequery.expr.cmp.field.StaticFieldRoot
				 && clazz.Equals(j4o.lang.Class.GetClassForType(typeof(j4o.lang.Class))))
			{
				clazz = (j4o.lang.Class)receiver;
			}
			j4o.lang.reflect.Method method = com.db4o.nativequery.optimization.ReflectUtil.MethodFor
				(clazz, operand.MethodName(), operand.ParamTypes());
			try
			{
				_value = method.Invoke(receiver, _params);
			}
			catch (System.Exception exc)
			{
				j4o.lang.JavaSystem.PrintStackTrace(exc);
				_value = null;
			}
		}

		public ComparisonQueryGeneratingVisitor(object predicate) : base()
		{
			this._predicate = predicate;
		}
	}
}

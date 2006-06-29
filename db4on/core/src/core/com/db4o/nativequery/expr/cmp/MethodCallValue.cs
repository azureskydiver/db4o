namespace com.db4o.nativequery.expr.cmp
{
	public class MethodCallValue : com.db4o.nativequery.expr.cmp.ComparisonOperandDescendant
	{
		private string _methodName;

		private j4o.lang.Class[] _paramTypes;

		private com.db4o.nativequery.expr.cmp.ComparisonOperand[] _args;

		public MethodCallValue(com.db4o.nativequery.expr.cmp.ComparisonOperandAnchor parent
			, string name, j4o.lang.Class[] paramTypes, com.db4o.nativequery.expr.cmp.ComparisonOperand[]
			 args) : base(parent)
		{
			_methodName = name;
			_paramTypes = paramTypes;
			_args = args;
		}

		public override void Accept(com.db4o.nativequery.expr.cmp.ComparisonOperandVisitor
			 visitor)
		{
			visitor.Visit(this);
		}

		public virtual string MethodName()
		{
			return _methodName;
		}

		public virtual j4o.lang.Class[] ParamTypes()
		{
			return _paramTypes;
		}

		public virtual com.db4o.nativequery.expr.cmp.ComparisonOperand[] Args()
		{
			return _args;
		}

		public override bool Equals(object obj)
		{
			if (!base.Equals(obj))
			{
				return false;
			}
			com.db4o.nativequery.expr.cmp.MethodCallValue casted = (com.db4o.nativequery.expr.cmp.MethodCallValue
				)obj;
			return _methodName.Equals(casted._methodName) && ArrayCmp(_paramTypes, casted._paramTypes
				) && ArrayCmp(_args, casted._args);
		}

		public override int GetHashCode()
		{
			int hc = base.GetHashCode();
			hc *= 29 + _methodName.GetHashCode();
			hc *= 29 + _paramTypes.GetHashCode();
			hc *= 29 + _args.GetHashCode();
			return hc;
		}

		public override string ToString()
		{
			string str = base.ToString() + "." + _methodName + "(";
			for (int paramIdx = 0; paramIdx < _paramTypes.Length; paramIdx++)
			{
				if (paramIdx > 0)
				{
					str += ",";
				}
				str += _paramTypes[paramIdx] + ":" + _args[paramIdx];
			}
			str += ")";
			return str;
		}

		private bool ArrayCmp(object[] a, object[] b)
		{
			if (a.Length != b.Length)
			{
				return false;
			}
			for (int paramIdx = 0; paramIdx < a.Length; paramIdx++)
			{
				if (!a[paramIdx].Equals(b[paramIdx]))
				{
					return false;
				}
			}
			return true;
		}
	}
}

namespace com.db4o.@internal.handlers
{
	/// <exclude></exclude>
	public sealed class DoubleHandler : com.db4o.@internal.handlers.LongHandler
	{
		private static readonly double DEFAULT_VALUE = System.Convert.ToDouble(0);

		public DoubleHandler(com.db4o.@internal.ObjectContainerBase stream) : base(stream
			)
		{
		}

		public override object Coerce(com.db4o.reflect.ReflectClass claxx, object obj)
		{
			return com.db4o.foundation.Coercion4.ToDouble(obj);
		}

		public override object DefaultValue()
		{
			return DEFAULT_VALUE;
		}

		public override int GetID()
		{
			return 5;
		}

		protected override j4o.lang.Class PrimitiveJavaClass()
		{
			return j4o.lang.JavaSystem.GetClassForType(typeof(double));
		}

		public override object PrimitiveNull()
		{
			return DEFAULT_VALUE;
		}

		internal override object Read1(com.db4o.@internal.Buffer a_bytes)
		{
			return com.db4o.@internal.Platform4.LongToDouble(ReadLong(a_bytes));
		}

		public override void Write(object a_object, com.db4o.@internal.Buffer a_bytes)
		{
			a_bytes.WriteLong(com.db4o.@internal.Platform4.DoubleToLong(((double)a_object)));
		}

		private double i_compareToDouble;

		private double Dval(object obj)
		{
			return ((double)obj);
		}

		internal override void PrepareComparison1(object obj)
		{
			i_compareToDouble = Dval(obj);
		}

		public override object Current1()
		{
			return i_compareToDouble;
		}

		internal override bool IsEqual1(object obj)
		{
			return obj is double && Dval(obj) == i_compareToDouble;
		}

		internal override bool IsGreater1(object obj)
		{
			return obj is double && Dval(obj) > i_compareToDouble;
		}

		internal override bool IsSmaller1(object obj)
		{
			return obj is double && Dval(obj) < i_compareToDouble;
		}
	}
}

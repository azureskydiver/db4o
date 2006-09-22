namespace com.db4o
{
	/// <exclude></exclude>
	public sealed class YDouble : com.db4o.YLong
	{
		private static readonly double i_primitive = System.Convert.ToDouble(0);

		public YDouble(com.db4o.YapStream stream) : base(stream)
		{
		}

		public override object Coerce(com.db4o.reflect.ReflectClass claxx, object obj)
		{
			return com.db4o.foundation.Coercion4.ToDouble(obj);
		}

		public override object DefaultValue()
		{
			return i_primitive;
		}

		public override int GetID()
		{
			return 5;
		}

		protected override j4o.lang.Class PrimitiveJavaClass()
		{
			return j4o.lang.Class.GetClassForType(typeof(double));
		}

		internal override object PrimitiveNull()
		{
			return i_primitive;
		}

		internal override object Read1(com.db4o.YapReader a_bytes)
		{
			return com.db4o.Platform4.LongToDouble(ReadLong(a_bytes));
		}

		public override void Write(object a_object, com.db4o.YapReader a_bytes)
		{
			WriteLong(com.db4o.Platform4.DoubleToLong(((double)a_object)), a_bytes);
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

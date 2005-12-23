namespace com.db4o
{
	internal sealed class YDouble : com.db4o.YLong
	{
		private static readonly double i_primitive = System.Convert.ToDouble(0);

		public YDouble(com.db4o.YapStream stream) : base(stream)
		{
		}

		public override object coerce(com.db4o.reflect.ReflectClass claxx, object obj)
		{
			return com.db4o.foundation.Coercion4.toDouble(obj);
		}

		public override object defaultValue()
		{
			return i_primitive;
		}

		public override int getID()
		{
			return 5;
		}

		protected override j4o.lang.Class primitiveJavaClass()
		{
			return j4o.lang.Class.getClassForType(typeof(double));
		}

		internal override object primitiveNull()
		{
			return i_primitive;
		}

		internal override object read1(com.db4o.YapReader a_bytes)
		{
			long ret = readLong(a_bytes);
			return com.db4o.Platform4.longToDouble(ret);
		}

		public override void write(object a_object, com.db4o.YapWriter a_bytes)
		{
			if (!com.db4o.Deploy.csharp && a_object == null)
			{
				writeLong(long.MaxValue, a_bytes);
			}
			else
			{
				writeLong(com.db4o.Platform4.doubleToLong(((double)a_object)), a_bytes);
			}
		}

		private double i_compareToDouble;

		private double dval(object obj)
		{
			return ((double)obj);
		}

		internal override void prepareComparison1(object obj)
		{
			i_compareToDouble = dval(obj);
		}

		internal override bool isEqual1(object obj)
		{
			return obj is double && dval(obj) == i_compareToDouble;
		}

		internal override bool isGreater1(object obj)
		{
			return obj is double && dval(obj) > i_compareToDouble;
		}

		internal override bool isSmaller1(object obj)
		{
			return obj is double && dval(obj) < i_compareToDouble;
		}
	}
}

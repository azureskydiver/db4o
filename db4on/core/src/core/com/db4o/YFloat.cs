namespace com.db4o
{
	internal sealed class YFloat : com.db4o.YInt
	{
		private static readonly float i_primitive = System.Convert.ToSingle(0);

		public YFloat(com.db4o.YapStream stream) : base(stream)
		{
		}

		public override object defaultValue()
		{
			return i_primitive;
		}

		public override int getID()
		{
			return 3;
		}

		protected override j4o.lang.Class primitiveJavaClass()
		{
			return j4o.lang.Class.getClassForType(typeof(float));
		}

		internal override object primitiveNull()
		{
			return i_primitive;
		}

		internal override object read1(com.db4o.YapReader a_bytes)
		{
			int ret = readInt(a_bytes);
			return j4o.lang.JavaSystem.intBitsToFloat(ret);
		}

		public override void write(object a_object, com.db4o.YapWriter a_bytes)
		{
			if (!com.db4o.Deploy.csharp && a_object == null)
			{
				writeInt(int.MaxValue, a_bytes);
			}
			else
			{
				writeInt(j4o.lang.JavaSystem.floatToIntBits(((float)a_object)), a_bytes);
			}
		}

		private float i_compareTo;

		private float valu(object obj)
		{
			return ((float)obj);
		}

		internal override void prepareComparison1(object obj)
		{
			i_compareTo = valu(obj);
		}

		internal override bool isEqual1(object obj)
		{
			return obj is float && valu(obj) == i_compareTo;
		}

		internal override bool isGreater1(object obj)
		{
			return obj is float && valu(obj) > i_compareTo;
		}

		internal override bool isSmaller1(object obj)
		{
			return obj is float && valu(obj) < i_compareTo;
		}
	}
}

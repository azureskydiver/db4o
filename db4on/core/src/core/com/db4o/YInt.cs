namespace com.db4o
{
	/// <exclude></exclude>
	public class YInt : com.db4o.YapJavaClass
	{
		private static readonly int i_primitive = 0;

		public YInt(com.db4o.YapStream stream) : base(stream)
		{
		}

		public override object coerce(com.db4o.reflect.ReflectClass claxx, object obj)
		{
			return com.db4o.foundation.Coercion4.toInt(obj);
		}

		public override object defaultValue()
		{
			return i_primitive;
		}

		public override int getID()
		{
			return 1;
		}

		protected override j4o.lang.Class primitiveJavaClass()
		{
			return j4o.lang.Class.getClassForType(typeof(int));
		}

		public override int linkLength()
		{
			return com.db4o.YapConst.YAPINT_LENGTH;
		}

		internal override object primitiveNull()
		{
			return i_primitive;
		}

		internal override object read1(com.db4o.YapReader a_bytes)
		{
			int ret = readInt(a_bytes);
			return ret;
		}

		internal static int readInt(com.db4o.YapReader a_bytes)
		{
			return a_bytes.readInt();
		}

		public override void write(object a_object, com.db4o.YapReader a_bytes)
		{
			if (!com.db4o.Deploy.csharp && a_object == null)
			{
				writeInt(int.MaxValue, a_bytes);
			}
			else
			{
				writeInt(((int)a_object), a_bytes);
			}
		}

		internal static void writeInt(int a_int, com.db4o.YapReader a_bytes)
		{
			a_bytes.writeInt(a_int);
		}

		private int i_compareTo;

		private int val(object obj)
		{
			return ((int)obj);
		}

		internal override void prepareComparison1(object obj)
		{
			i_compareTo = val(obj);
		}

		public override object current1()
		{
			return i_compareTo;
		}

		internal override bool isEqual1(object obj)
		{
			return obj is int && val(obj) == i_compareTo;
		}

		internal override bool isGreater1(object obj)
		{
			return obj is int && val(obj) > i_compareTo;
		}

		internal override bool isSmaller1(object obj)
		{
			return obj is int && val(obj) < i_compareTo;
		}
	}
}

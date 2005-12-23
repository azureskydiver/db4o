namespace com.db4o
{
	internal sealed class YShort : com.db4o.YapJavaClass
	{
		internal const int LENGTH = com.db4o.YapConst.SHORT_BYTES + com.db4o.YapConst.ADDED_LENGTH;

		private static readonly short i_primitive = (short)0;

		public YShort(com.db4o.YapStream stream) : base(stream)
		{
		}

		public override object coerce(com.db4o.reflect.ReflectClass claxx, object obj)
		{
			return com.db4o.foundation.Coercion4.toShort(obj);
		}

		public override object defaultValue()
		{
			return i_primitive;
		}

		public override int getID()
		{
			return 8;
		}

		public override int linkLength()
		{
			return LENGTH;
		}

		protected override j4o.lang.Class primitiveJavaClass()
		{
			return j4o.lang.Class.getClassForType(typeof(short));
		}

		internal override object primitiveNull()
		{
			return i_primitive;
		}

		internal override object read1(com.db4o.YapReader a_bytes)
		{
			short ret = readShort(a_bytes);
			return ret;
		}

		internal static short readShort(com.db4o.YapReader a_bytes)
		{
			int ret = 0;
			for (int i = 0; i < com.db4o.YapConst.SHORT_BYTES; i++)
			{
				ret = (ret << 8) + (a_bytes._buffer[a_bytes._offset++] & unchecked((int)(0xff)));
			}
			return (short)ret;
		}

		public override void write(object a_object, com.db4o.YapWriter a_bytes)
		{
			if (!com.db4o.Deploy.csharp && a_object == null)
			{
				writeShort(short.MaxValue, a_bytes);
			}
			else
			{
				writeShort(((short)a_object), a_bytes);
			}
		}

		internal static void writeShort(int a_short, com.db4o.YapWriter a_bytes)
		{
			for (int i = 0; i < com.db4o.YapConst.SHORT_BYTES; i++)
			{
				a_bytes._buffer[a_bytes._offset++] = (byte)(a_short >> ((com.db4o.YapConst.SHORT_BYTES
					 - 1 - i) * 8));
			}
		}

		private short i_compareTo;

		private short val(object obj)
		{
			return ((short)obj);
		}

		internal override void prepareComparison1(object obj)
		{
			i_compareTo = val(obj);
		}

		internal override bool isEqual1(object obj)
		{
			return obj is short && val(obj) == i_compareTo;
		}

		internal override bool isGreater1(object obj)
		{
			return obj is short && val(obj) > i_compareTo;
		}

		internal override bool isSmaller1(object obj)
		{
			return obj is short && val(obj) < i_compareTo;
		}
	}
}

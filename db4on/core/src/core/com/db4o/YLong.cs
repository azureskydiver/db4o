
namespace com.db4o
{
	internal class YLong : com.db4o.YapJavaClass
	{
		private static readonly long i_primitive = System.Convert.ToInt64(0);

		public YLong(com.db4o.YapStream stream) : base(stream)
		{
		}

		public override object defaultValue()
		{
			return i_primitive;
		}

		public override int getID()
		{
			return 2;
		}

		protected override j4o.lang.Class primitiveJavaClass()
		{
			return j4o.lang.Class.getClassForType(typeof(long));
		}

		public override int linkLength()
		{
			return com.db4o.YapConst.YAPLONG_LENGTH;
		}

		internal override object primitiveNull()
		{
			return i_primitive;
		}

		internal override object read1(com.db4o.YapReader a_bytes)
		{
			long ret = readLong(a_bytes);
			return ret;
		}

		internal static long readLong(com.db4o.YapReader a_bytes)
		{
			long l_return = 0;
			for (int i = 0; i < com.db4o.YapConst.LONG_BYTES; i++)
			{
				l_return = (l_return << 8) + (a_bytes._buffer[a_bytes._offset++] & 0xff);
			}
			return l_return;
		}

		public override void write(object a_object, com.db4o.YapWriter a_bytes)
		{
			if (!com.db4o.Deploy.csharp && a_object == null)
			{
				writeLong(long.MaxValue, a_bytes);
			}
			else
			{
				writeLong(((long)a_object), a_bytes);
			}
		}

		internal static void writeLong(long a_long, com.db4o.YapWriter a_bytes)
		{
			for (int i = 0; i < com.db4o.YapConst.LONG_BYTES; i++)
			{
				a_bytes._buffer[a_bytes._offset++] = (byte)(a_long >> ((com.db4o.YapConst.LONG_BYTES
					 - 1 - i) * 8));
			}
		}

		internal static void writeLong(long a_long, byte[] bytes)
		{
			for (int i = 0; i < com.db4o.YapConst.LONG_BYTES; i++)
			{
				bytes[i] = (byte)(a_long >> ((com.db4o.YapConst.LONG_BYTES - 1 - i) * 8));
			}
		}

		internal static long readLong(com.db4o.YapWriter writer)
		{
			long l_return = 0;
			for (int i = 0; i < com.db4o.YapConst.LONG_BYTES; i++)
			{
				l_return = (l_return << 8) + (writer._buffer[writer._offset++] & 0xff);
			}
			return l_return;
		}

		protected long i_compareTo;

		internal virtual long val(object obj)
		{
			return ((long)obj);
		}

		internal override void prepareComparison1(object obj)
		{
			i_compareTo = val(obj);
		}

		internal override bool isEqual1(object obj)
		{
			return obj is long && val(obj) == i_compareTo;
		}

		internal override bool isGreater1(object obj)
		{
			return obj is long && val(obj) > i_compareTo;
		}

		internal override bool isSmaller1(object obj)
		{
			return obj is long && val(obj) < i_compareTo;
		}
	}
}

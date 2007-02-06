namespace com.db4o.@internal.handlers
{
	/// <exclude></exclude>
	public class LongHandler : com.db4o.@internal.handlers.PrimitiveHandler
	{
		private static readonly long i_primitive = System.Convert.ToInt64(0);

		public LongHandler(com.db4o.@internal.ObjectContainerBase stream) : base(stream)
		{
		}

		public override object Coerce(com.db4o.reflect.ReflectClass claxx, object obj)
		{
			return com.db4o.foundation.Coercion4.ToLong(obj);
		}

		public override object DefaultValue()
		{
			return i_primitive;
		}

		public override int GetID()
		{
			return 2;
		}

		protected override j4o.lang.Class PrimitiveJavaClass()
		{
			return j4o.lang.JavaSystem.GetClassForType(typeof(long));
		}

		public override int LinkLength()
		{
			return com.db4o.@internal.Const4.LONG_LENGTH;
		}

		public override object PrimitiveNull()
		{
			return i_primitive;
		}

		internal override object Read1(com.db4o.@internal.Buffer a_bytes)
		{
			return ReadLong(a_bytes);
		}

		public static long ReadLong(com.db4o.@internal.Buffer a_bytes)
		{
			long l_return = 0;
			for (int i = 0; i < com.db4o.@internal.Const4.LONG_BYTES; i++)
			{
				l_return = (l_return << 8) + (a_bytes._buffer[a_bytes._offset++] & unchecked((int
					)(0xff)));
			}
			return l_return;
		}

		public override void Write(object a_object, com.db4o.@internal.Buffer a_bytes)
		{
			WriteLong(((long)a_object), a_bytes);
		}

		public static void WriteLong(long a_long, com.db4o.@internal.Buffer a_bytes)
		{
			for (int i = 0; i < com.db4o.@internal.Const4.LONG_BYTES; i++)
			{
				a_bytes._buffer[a_bytes._offset++] = (byte)(a_long >> ((com.db4o.@internal.Const4
					.LONG_BYTES - 1 - i) * 8));
			}
		}

		public static void WriteLong(long a_long, byte[] bytes)
		{
			for (int i = 0; i < com.db4o.@internal.Const4.LONG_BYTES; i++)
			{
				bytes[i] = (byte)(a_long >> ((com.db4o.@internal.Const4.LONG_BYTES - 1 - i) * 8));
			}
		}

		private long i_compareTo;

		protected long CurrentLong()
		{
			return i_compareTo;
		}

		internal virtual long Val(object obj)
		{
			return ((long)obj);
		}

		internal override void PrepareComparison1(object obj)
		{
			i_compareTo = Val(obj);
		}

		public override object Current1()
		{
			return i_compareTo;
		}

		internal override bool IsEqual1(object obj)
		{
			return obj is long && Val(obj) == i_compareTo;
		}

		internal override bool IsGreater1(object obj)
		{
			return obj is long && Val(obj) > i_compareTo;
		}

		internal override bool IsSmaller1(object obj)
		{
			return obj is long && Val(obj) < i_compareTo;
		}
	}
}

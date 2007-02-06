namespace com.db4o.@internal.handlers
{
	public sealed class ShortHandler : com.db4o.@internal.handlers.PrimitiveHandler
	{
		internal const int LENGTH = com.db4o.@internal.Const4.SHORT_BYTES + com.db4o.@internal.Const4
			.ADDED_LENGTH;

		private static readonly short i_primitive = (short)0;

		public ShortHandler(com.db4o.@internal.ObjectContainerBase stream) : base(stream)
		{
		}

		public override object Coerce(com.db4o.reflect.ReflectClass claxx, object obj)
		{
			return com.db4o.foundation.Coercion4.ToShort(obj);
		}

		public override object DefaultValue()
		{
			return i_primitive;
		}

		public override int GetID()
		{
			return 8;
		}

		public override int LinkLength()
		{
			return LENGTH;
		}

		protected override j4o.lang.Class PrimitiveJavaClass()
		{
			return j4o.lang.JavaSystem.GetClassForType(typeof(short));
		}

		public override object PrimitiveNull()
		{
			return i_primitive;
		}

		internal override object Read1(com.db4o.@internal.Buffer a_bytes)
		{
			return ReadShort(a_bytes);
		}

		internal static short ReadShort(com.db4o.@internal.Buffer a_bytes)
		{
			int ret = 0;
			for (int i = 0; i < com.db4o.@internal.Const4.SHORT_BYTES; i++)
			{
				ret = (ret << 8) + (a_bytes._buffer[a_bytes._offset++] & unchecked((int)(0xff)));
			}
			return (short)ret;
		}

		public override void Write(object a_object, com.db4o.@internal.Buffer a_bytes)
		{
			WriteShort(((short)a_object), a_bytes);
		}

		internal static void WriteShort(int a_short, com.db4o.@internal.Buffer a_bytes)
		{
			for (int i = 0; i < com.db4o.@internal.Const4.SHORT_BYTES; i++)
			{
				a_bytes._buffer[a_bytes._offset++] = (byte)(a_short >> ((com.db4o.@internal.Const4
					.SHORT_BYTES - 1 - i) * 8));
			}
		}

		private short i_compareTo;

		private short Val(object obj)
		{
			return ((short)obj);
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
			return obj is short && Val(obj) == i_compareTo;
		}

		internal override bool IsGreater1(object obj)
		{
			return obj is short && Val(obj) > i_compareTo;
		}

		internal override bool IsSmaller1(object obj)
		{
			return obj is short && Val(obj) < i_compareTo;
		}
	}
}

namespace com.db4o.@internal.handlers
{
	public sealed class ByteHandler : com.db4o.@internal.handlers.PrimitiveHandler
	{
		internal const int LENGTH = 1 + com.db4o.@internal.Const4.ADDED_LENGTH;

		private static readonly byte i_primitive = (byte)0;

		public ByteHandler(com.db4o.@internal.ObjectContainerBase stream) : base(stream)
		{
		}

		public override object Coerce(com.db4o.reflect.ReflectClass claxx, object obj)
		{
			return com.db4o.foundation.Coercion4.ToSByte(obj);
		}

		public override int GetID()
		{
			return 6;
		}

		public override object DefaultValue()
		{
			return i_primitive;
		}

		public override int LinkLength()
		{
			return LENGTH;
		}

		protected override j4o.lang.Class PrimitiveJavaClass()
		{
			return j4o.lang.JavaSystem.GetClassForType(typeof(byte));
		}

		public override object PrimitiveNull()
		{
			return i_primitive;
		}

		internal override object Read1(com.db4o.@internal.Buffer a_bytes)
		{
			byte ret = a_bytes.ReadByte();
			return ret;
		}

		public override void Write(object a_object, com.db4o.@internal.Buffer a_bytes)
		{
			a_bytes.Append(((byte)a_object));
		}

		public override bool ReadArray(object array, com.db4o.@internal.Buffer reader)
		{
			if (array is byte[])
			{
				reader.ReadBytes((byte[])array);
				return true;
			}
			return false;
		}

		public override bool WriteArray(object array, com.db4o.@internal.Buffer writer)
		{
			if (array is byte[])
			{
				writer.Append((byte[])array);
				return true;
			}
			return false;
		}

		private byte i_compareTo;

		private byte Val(object obj)
		{
			return ((byte)obj);
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
			return obj is byte && Val(obj) == i_compareTo;
		}

		internal override bool IsGreater1(object obj)
		{
			return obj is byte && Val(obj) > i_compareTo;
		}

		internal override bool IsSmaller1(object obj)
		{
			return obj is byte && Val(obj) < i_compareTo;
		}
	}
}

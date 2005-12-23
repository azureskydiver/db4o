namespace com.db4o
{
	internal sealed class YByte : com.db4o.YapJavaClass
	{
		internal const int LENGTH = 1 + com.db4o.YapConst.ADDED_LENGTH;

		private static readonly byte i_primitive = (byte)0;

		public YByte(com.db4o.YapStream stream) : base(stream)
		{
		}

		public override object coerce(com.db4o.reflect.ReflectClass claxx, object obj)
		{
			return com.db4o.foundation.Coercion4.toSByte(obj);
		}

		public override int getID()
		{
			return 6;
		}

		public override object defaultValue()
		{
			return i_primitive;
		}

		internal bool isNoConstraint(object obj, bool isPrimitive)
		{
			return obj.Equals(primitiveNull());
		}

		public override int linkLength()
		{
			return LENGTH;
		}

		protected override j4o.lang.Class primitiveJavaClass()
		{
			return j4o.lang.Class.getClassForType(typeof(byte));
		}

		internal override object primitiveNull()
		{
			return i_primitive;
		}

		internal override object read1(com.db4o.YapReader a_bytes)
		{
			byte ret = a_bytes.readByte();
			return ret;
		}

		public override void write(object a_object, com.db4o.YapWriter a_bytes)
		{
			byte set;
			if (a_object == null)
			{
				set = (byte)0;
			}
			else
			{
				set = ((byte)a_object);
			}
			a_bytes.append(set);
		}

		public override bool readArray(object array, com.db4o.YapWriter reader)
		{
			if (array is byte[])
			{
				reader.readBytes((byte[])array);
				return true;
			}
			return false;
		}

		public override bool writeArray(object array, com.db4o.YapWriter writer)
		{
			if (array is byte[])
			{
				writer.append((byte[])array);
				return true;
			}
			return false;
		}

		private byte i_compareTo;

		private byte val(object obj)
		{
			return ((byte)obj);
		}

		internal override void prepareComparison1(object obj)
		{
			i_compareTo = val(obj);
		}

		internal override bool isEqual1(object obj)
		{
			return obj is byte && val(obj) == i_compareTo;
		}

		internal override bool isGreater1(object obj)
		{
			return obj is byte && val(obj) > i_compareTo;
		}

		internal override bool isSmaller1(object obj)
		{
			return obj is byte && val(obj) < i_compareTo;
		}
	}
}

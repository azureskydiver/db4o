namespace com.db4o
{
	internal sealed class YChar : com.db4o.YapJavaClass
	{
		internal const int LENGTH = com.db4o.YapConst.CHAR_BYTES + com.db4o.YapConst.ADDED_LENGTH;

		private static readonly char i_primitive = (char)0;

		public YChar(com.db4o.YapStream stream) : base(stream)
		{
		}

		public override object defaultValue()
		{
			return i_primitive;
		}

		public override int getID()
		{
			return 7;
		}

		public override int linkLength()
		{
			return LENGTH;
		}

		protected override j4o.lang.Class primitiveJavaClass()
		{
			return j4o.lang.Class.getClassForType(typeof(char));
		}

		internal override object primitiveNull()
		{
			return i_primitive;
		}

		internal override object read1(com.db4o.YapReader a_bytes)
		{
			byte b1 = a_bytes.readByte();
			byte b2 = a_bytes.readByte();
			char ret = (char)((b1 & unchecked((int)(0xff))) | ((b2 & unchecked((int)(0xff))) 
				<< 8));
			return ret;
		}

		public override void write(object a_object, com.db4o.YapReader a_bytes)
		{
			char l_char;
			if (!com.db4o.Deploy.csharp && a_object == null)
			{
				l_char = char.MaxValue;
			}
			else
			{
				l_char = ((char)a_object);
			}
			a_bytes.append((byte)(l_char & unchecked((int)(0xff))));
			a_bytes.append((byte)(l_char >> 8));
		}

		private char i_compareTo;

		private char val(object obj)
		{
			return ((char)obj);
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
			return obj is char && val(obj) == i_compareTo;
		}

		internal override bool isGreater1(object obj)
		{
			return obj is char && val(obj) > i_compareTo;
		}

		internal override bool isSmaller1(object obj)
		{
			return obj is char && val(obj) < i_compareTo;
		}
	}
}

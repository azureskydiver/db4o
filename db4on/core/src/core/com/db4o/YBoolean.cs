
namespace com.db4o
{
	internal sealed class YBoolean : com.db4o.YapJavaClass
	{
		internal const int LENGTH = 1 + com.db4o.YapConst.ADDED_LENGTH;

		private const byte TRUE = (byte)'T';

		private const byte FALSE = (byte)'F';

		private const byte NULL = (byte)'N';

		private static readonly bool i_primitive = false;

		public YBoolean(com.db4o.YapStream stream) : base(stream)
		{
		}

		public override int getID()
		{
			return 4;
		}

		public override object defaultValue()
		{
			return i_primitive;
		}

		public override int linkLength()
		{
			return LENGTH;
		}

		protected override j4o.lang.Class primitiveJavaClass()
		{
			return j4o.lang.Class.getClassForType(typeof(bool));
		}

		internal override object primitiveNull()
		{
			return i_primitive;
		}

		internal override object read1(com.db4o.YapReader a_bytes)
		{
			byte ret = a_bytes.readByte();
			if (ret == TRUE)
			{
				return true;
			}
			if (ret == FALSE)
			{
				return false;
			}
			return null;
		}

		public override void write(object a_object, com.db4o.YapWriter a_bytes)
		{
			byte set;
			if (a_object == null)
			{
				set = NULL;
			}
			else
			{
				if (((bool)a_object))
				{
					set = TRUE;
				}
				else
				{
					set = FALSE;
				}
			}
			a_bytes.append(set);
		}

		private bool i_compareTo;

		private bool val(object obj)
		{
			return ((bool)obj);
		}

		internal override void prepareComparison1(object obj)
		{
			i_compareTo = val(obj);
		}

		internal override bool isEqual1(object obj)
		{
			return obj is bool && val(obj) == i_compareTo;
		}

		internal override bool isGreater1(object obj)
		{
			if (i_compareTo)
			{
				return false;
			}
			return obj is bool && val(obj);
		}

		internal override bool isSmaller1(object obj)
		{
			if (!i_compareTo)
			{
				return false;
			}
			return obj is bool && !val(obj);
		}
	}
}


namespace com.db4o
{
	/// <exclude></exclude>
	public abstract class YapTypeAbstract : com.db4o.YapJavaClass, com.db4o.YapType
	{
		public YapTypeAbstract(com.db4o.YapStream stream) : base(stream)
		{
		}

		private int i_linkLength;

		private object i_compareTo;

		public abstract int compare(object compare, object with);

		public virtual string dotNetClassName()
		{
			string className = j4o.lang.Class.getClassForObject(this).getName();
			int pos = className.IndexOf(".Net");
			if (pos >= 0)
			{
				return "System." + className.Substring(pos + 4) + ", mscorlib";
			}
			return j4o.lang.Class.getClassForObject(defaultValue()).getName();
		}

		public abstract bool isEqual(object compare, object with);

		internal virtual void initialize()
		{
			byte[] bytes = new byte[65];
			for (int i = 0; i < bytes.Length; i++)
			{
				bytes[i] = 55;
			}
			write(primitiveNull(), bytes, 0);
			for (int i = 0; i < bytes.Length; i++)
			{
				if (bytes[i] == 55)
				{
					i_linkLength = i;
					break;
				}
			}
		}

		public override int getID()
		{
			return typeID();
		}

		public virtual string getName()
		{
			return dotNetClassName();
		}

		public override int linkLength()
		{
			return i_linkLength;
		}

		protected override j4o.lang.Class primitiveJavaClass()
		{
			return null;
		}

		internal override object primitiveNull()
		{
			return defaultValue();
		}

		public abstract object read(byte[] bytes, int offset);

		internal override object read1(com.db4o.YapReader a_bytes)
		{
			int offset = a_bytes._offset;
			object ret = read(a_bytes._buffer, a_bytes._offset);
			a_bytes._offset = offset + linkLength();
			return ret;
		}

		public abstract int typeID();

		public abstract void write(object obj, byte[] bytes, int offset);

		public override void write(object a_object, com.db4o.YapWriter a_bytes)
		{
			int offset = a_bytes._offset;
			if (a_object != null)
			{
				write(a_object, a_bytes._buffer, a_bytes._offset);
			}
			a_bytes._offset = offset + linkLength();
		}

		internal override void prepareComparison1(object obj)
		{
			i_compareTo = obj;
		}

		internal override bool isEqual1(object obj)
		{
			return isEqual(i_compareTo, obj);
		}

		internal override bool isGreater1(object obj)
		{
			if (classReflector().isInstance(obj) && !isEqual(i_compareTo, obj))
			{
				return compare(i_compareTo, obj) > 0;
			}
			return false;
		}

		internal override bool isSmaller1(object obj)
		{
			if (classReflector().isInstance(obj) && !isEqual(i_compareTo, obj))
			{
				return compare(i_compareTo, obj) < 0;
			}
			return false;
		}
	}
}

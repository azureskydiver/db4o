namespace com.db4o
{
	/// <exclude></exclude>
	public class YInt : com.db4o.YapJavaClass
	{
		private static readonly int i_primitive = 0;

		public YInt(com.db4o.YapStream stream) : base(stream)
		{
		}

		public override object Coerce(com.db4o.reflect.ReflectClass claxx, object obj)
		{
			return com.db4o.foundation.Coercion4.ToInt(obj);
		}

		public override object DefaultValue()
		{
			return i_primitive;
		}

		public override int GetID()
		{
			return 1;
		}

		protected override j4o.lang.Class PrimitiveJavaClass()
		{
			return j4o.lang.JavaSystem.GetClassForType(typeof(int));
		}

		public override int LinkLength()
		{
			return com.db4o.YapConst.INT_LENGTH;
		}

		public static int Max(int x, int y)
		{
			return (x < y) ? y : x;
		}

		internal override object PrimitiveNull()
		{
			return i_primitive;
		}

		internal override object Read1(com.db4o.YapReader a_bytes)
		{
			return a_bytes.ReadInt();
		}

		internal static int ReadInt(com.db4o.YapReader a_bytes)
		{
			return a_bytes.ReadInt();
		}

		public override void Write(object obj, com.db4o.YapReader writer)
		{
			Write(((int)obj), writer);
		}

		public virtual void Write(int intValue, com.db4o.YapReader writer)
		{
			WriteInt(intValue, writer);
		}

		internal static void WriteInt(int a_int, com.db4o.YapReader a_bytes)
		{
			a_bytes.WriteInt(a_int);
		}

		private int i_compareTo;

		private int Val(object obj)
		{
			return ((int)obj);
		}

		public virtual int CompareTo(int other)
		{
			return other - i_compareTo;
		}

		public virtual void PrepareComparison(int i)
		{
			i_compareTo = i;
		}

		internal override void PrepareComparison1(object obj)
		{
			PrepareComparison(Val(obj));
		}

		public override object Current1()
		{
			return CurrentInt();
		}

		public virtual int CurrentInt()
		{
			return i_compareTo;
		}

		internal override bool IsEqual1(object obj)
		{
			return obj is int && Val(obj) == i_compareTo;
		}

		internal override bool IsGreater1(object obj)
		{
			return obj is int && Val(obj) > i_compareTo;
		}

		internal override bool IsSmaller1(object obj)
		{
			return obj is int && Val(obj) < i_compareTo;
		}

		public override void DefragIndexEntry(com.db4o.ReaderPair readers)
		{
			readers.CopyID();
		}
	}
}

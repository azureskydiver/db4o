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
			return j4o.lang.Class.GetClassForType(typeof(int));
		}

		public override int LinkLength()
		{
			return com.db4o.YapConst.YAPINT_LENGTH;
		}

		internal override object PrimitiveNull()
		{
			return i_primitive;
		}

		internal override object Read1(com.db4o.YapReader a_bytes)
		{
			return ReadInt(a_bytes);
		}

		internal static int ReadInt(com.db4o.YapReader a_bytes)
		{
			return a_bytes.ReadInt();
		}

		public override void Write(object a_object, com.db4o.YapReader a_bytes)
		{
			WriteInt(((int)a_object), a_bytes);
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
	}
}

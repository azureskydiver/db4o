namespace com.db4o.@internal.handlers
{
	/// <exclude></exclude>
	public class IntHandler : com.db4o.@internal.handlers.PrimitiveHandler
	{
		private static readonly int i_primitive = 0;

		public IntHandler(com.db4o.@internal.ObjectContainerBase stream) : base(stream)
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
			return com.db4o.@internal.Const4.INT_LENGTH;
		}

		public override object PrimitiveNull()
		{
			return i_primitive;
		}

		internal override object Read1(com.db4o.@internal.Buffer a_bytes)
		{
			return a_bytes.ReadInt();
		}

		public static int ReadInt(com.db4o.@internal.Buffer a_bytes)
		{
			return a_bytes.ReadInt();
		}

		public override void Write(object obj, com.db4o.@internal.Buffer writer)
		{
			Write(((int)obj), writer);
		}

		public virtual void Write(int intValue, com.db4o.@internal.Buffer writer)
		{
			WriteInt(intValue, writer);
		}

		public static void WriteInt(int a_int, com.db4o.@internal.Buffer a_bytes)
		{
			a_bytes.WriteInt(a_int);
		}

		private int i_compareTo;

		protected int Val(object obj)
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

		public override void DefragIndexEntry(com.db4o.@internal.ReaderPair readers)
		{
			readers.IncrementIntSize();
		}
	}
}

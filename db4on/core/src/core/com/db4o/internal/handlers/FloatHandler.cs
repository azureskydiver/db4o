namespace com.db4o.@internal.handlers
{
	public sealed class FloatHandler : com.db4o.@internal.handlers.IntHandler
	{
		private static readonly float i_primitive = System.Convert.ToSingle(0);

		public FloatHandler(com.db4o.@internal.ObjectContainerBase stream) : base(stream)
		{
		}

		public override object Coerce(com.db4o.reflect.ReflectClass claxx, object obj)
		{
			return com.db4o.foundation.Coercion4.ToFloat(obj);
		}

		public override object DefaultValue()
		{
			return i_primitive;
		}

		public override int GetID()
		{
			return 3;
		}

		protected override j4o.lang.Class PrimitiveJavaClass()
		{
			return j4o.lang.JavaSystem.GetClassForType(typeof(float));
		}

		public override object PrimitiveNull()
		{
			return i_primitive;
		}

		internal override object Read1(com.db4o.@internal.Buffer a_bytes)
		{
			return j4o.lang.JavaSystem.IntBitsToFloat(a_bytes.ReadInt());
		}

		public override void Write(object a_object, com.db4o.@internal.Buffer a_bytes)
		{
			WriteInt(j4o.lang.JavaSystem.FloatToIntBits(((float)a_object)), a_bytes);
		}

		private float i_compareTo;

		private float Valu(object obj)
		{
			return ((float)obj);
		}

		internal override void PrepareComparison1(object obj)
		{
			i_compareTo = Valu(obj);
		}

		public override object Current1()
		{
			return i_compareTo;
		}

		internal override bool IsEqual1(object obj)
		{
			return obj is float && Valu(obj) == i_compareTo;
		}

		internal override bool IsGreater1(object obj)
		{
			return obj is float && Valu(obj) > i_compareTo;
		}

		internal override bool IsSmaller1(object obj)
		{
			return obj is float && Valu(obj) < i_compareTo;
		}
	}
}

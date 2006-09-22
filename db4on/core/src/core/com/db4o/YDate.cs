namespace com.db4o
{
	internal sealed class YDate : com.db4o.YLong
	{
		private static readonly j4o.util.Date PROTO = new j4o.util.Date(0);

		public YDate(com.db4o.YapStream stream) : base(stream)
		{
		}

		public override object Coerce(com.db4o.reflect.ReflectClass claxx, object obj)
		{
			return CanHold(claxx) ? obj : com.db4o.foundation.No4.INSTANCE;
		}

		public override void CopyValue(object a_from, object a_to)
		{
			try
			{
				((j4o.util.Date)a_to).SetTime(((j4o.util.Date)a_from).GetTime());
			}
			catch (System.Exception e)
			{
			}
		}

		public override object DefaultValue()
		{
			return PROTO;
		}

		public override int GetID()
		{
			return 10;
		}

		public override bool IndexNullHandling()
		{
			return true;
		}

		protected override j4o.lang.Class PrimitiveJavaClass()
		{
			return null;
		}

		internal override object PrimitiveNull()
		{
			return null;
		}

		internal override object Read1(com.db4o.YapReader a_bytes)
		{
			return new j4o.util.Date(ReadLong(a_bytes));
		}

		public override void Write(object a_object, com.db4o.YapReader a_bytes)
		{
			if (a_object == null)
			{
				a_object = new j4o.util.Date(0);
			}
			WriteLong(((j4o.util.Date)a_object).GetTime(), a_bytes);
		}

		public override object Current1()
		{
			return new j4o.util.Date(CurrentLong());
		}

		internal static string Now()
		{
			return com.db4o.Platform4.Format(new j4o.util.Date(), true);
		}

		internal override long Val(object obj)
		{
			return ((j4o.util.Date)obj).GetTime();
		}

		internal override bool IsEqual1(object obj)
		{
			return obj is j4o.util.Date && Val(obj) == CurrentLong();
		}

		internal override bool IsGreater1(object obj)
		{
			return obj is j4o.util.Date && Val(obj) > CurrentLong();
		}

		internal override bool IsSmaller1(object obj)
		{
			return obj is j4o.util.Date && Val(obj) < CurrentLong();
		}
	}
}

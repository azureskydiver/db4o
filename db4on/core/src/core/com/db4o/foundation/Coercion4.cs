using System;

namespace com.db4o.foundation
{
	public class Coercion4
	{
		public static object ToSByte(object obj)
		{
			if (obj is byte) return obj;

			IConvertible convertible = obj as IConvertible;
			if (null != convertible) return convertible.ToSByte(null);
			return com.db4o.foundation.No4.INSTANCE;
		}

		public static object ToShort(object obj)
		{
			if (obj is short) return obj;

			IConvertible convertible = obj as IConvertible;
			if (null != convertible) return convertible.ToInt16(null);
			return com.db4o.foundation.No4.INSTANCE;
		}

		public static object ToInt(object obj)
		{
			if (obj is int) return obj;

			IConvertible convertible = obj as IConvertible;
			if (null != convertible) return convertible.ToInt32(null);
			return com.db4o.foundation.No4.INSTANCE;
		}

		public static object ToLong(object obj)
		{
			if (obj is long) return obj;

			IConvertible convertible = obj as IConvertible;
			if (null != convertible) return convertible.ToInt64(null);
			return com.db4o.foundation.No4.INSTANCE;
		}

		public static object ToFloat(object obj)
		{
			if (obj is float) return obj;

			IConvertible convertible = obj as IConvertible;
			if (null != convertible) return convertible.ToSingle(null);
			return com.db4o.foundation.No4.INSTANCE;
		}

		public static object ToDouble(object obj)
		{
			if (obj is double) return obj;

			IConvertible convertible = obj as IConvertible;
			if (null != convertible) return convertible.ToDouble(null);
			return com.db4o.foundation.No4.INSTANCE;
		}
	}
}


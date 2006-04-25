namespace com.db4o.foundation
{
	internal class HashtableByteArrayEntry : com.db4o.foundation.HashtableObjectEntry
	{
		public HashtableByteArrayEntry(byte[] bytes, object value) : base(hash(bytes), bytes
			, value)
		{
		}

		public override bool hasKey(object key)
		{
			if (key is byte[])
			{
				return areEqual((byte[])i_objectKey, (byte[])key);
			}
			return false;
		}

		internal static int hash(byte[] bytes)
		{
			int ret = 0;
			for (int i = 0; i < bytes.Length; i++)
			{
				ret = ret * 31 + bytes[i];
			}
			return ret;
		}

		internal static bool areEqual(byte[] lhs, byte[] rhs)
		{
			if (rhs.Length != lhs.Length)
			{
				return false;
			}
			for (int i = 0; i < rhs.Length; i++)
			{
				if (rhs[i] != lhs[i])
				{
					return false;
				}
			}
			return true;
		}
	}
}

namespace com.db4o.foundation
{
	internal class HashtableByteArrayEntry : com.db4o.foundation.HashtableObjectEntry
	{
		public HashtableByteArrayEntry(byte[] bytes, object value) : base(Hash(bytes), bytes
			, value)
		{
		}

		private HashtableByteArrayEntry() : base()
		{
		}

		public override object DeepClone(object obj)
		{
			return DeepCloneInternal(new com.db4o.foundation.HashtableByteArrayEntry(), obj);
		}

		public override bool HasKey(object key)
		{
			if (key is byte[])
			{
				return AreEqual((byte[])i_objectKey, (byte[])key);
			}
			return false;
		}

		internal static int Hash(byte[] bytes)
		{
			int ret = 0;
			for (int i = 0; i < bytes.Length; i++)
			{
				ret = ret * 31 + bytes[i];
			}
			return ret;
		}

		internal static bool AreEqual(byte[] lhs, byte[] rhs)
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

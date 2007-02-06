namespace com.db4o.db4ounit.common.types.arrays
{
	public class ByteArrayTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public interface IByteArrayHolder
		{
			byte[] GetBytes();
		}

		[System.Serializable]
		public class SerializableByteArrayHolder : com.db4o.db4ounit.common.types.arrays.ByteArrayTestCase.IByteArrayHolder
		{
			private const long serialVersionUID = 1L;

			internal byte[] _bytes;

			public SerializableByteArrayHolder(byte[] bytes)
			{
				this._bytes = bytes;
			}

			public virtual byte[] GetBytes()
			{
				return _bytes;
			}
		}

		public class ByteArrayHolder : com.db4o.db4ounit.common.types.arrays.ByteArrayTestCase.IByteArrayHolder
		{
			public byte[] _bytes;

			public ByteArrayHolder(byte[] bytes)
			{
				this._bytes = bytes;
			}

			public virtual byte[] GetBytes()
			{
				return _bytes;
			}
		}

		internal const int INSTANCES = 2;

		internal const int ARRAY_LENGTH = 1024 * 512;

		#if !CF_1_0 && !CF_2_0
		protected override void Configure(com.db4o.config.Configuration config)
		{
			config.ObjectClass(typeof(com.db4o.db4ounit.common.types.arrays.ByteArrayTestCase.SerializableByteArrayHolder)
				).Translate(new com.db4o.config.TSerializable());
		}
		#endif // !CF_1_0 && !CF_2_0

		protected override void Store()
		{
			for (int i = 0; i < INSTANCES; ++i)
			{
				Db().Set(new com.db4o.db4ounit.common.types.arrays.ByteArrayTestCase.ByteArrayHolder
					(CreateByteArray()));
				Db().Set(new com.db4o.db4ounit.common.types.arrays.ByteArrayTestCase.SerializableByteArrayHolder
					(CreateByteArray()));
			}
		}

		#if !CF_1_0 && !CF_2_0
		public virtual void TestByteArrayHolder()
		{
			TimeQueryLoop("raw byte array", typeof(com.db4o.db4ounit.common.types.arrays.ByteArrayTestCase.ByteArrayHolder)
				);
		}
		#endif // !CF_1_0 && !CF_2_0

		#if !CF_1_0 && !CF_2_0
		public virtual void TestSerializableByteArrayHolder()
		{
			TimeQueryLoop("TSerializable", typeof(com.db4o.db4ounit.common.types.arrays.ByteArrayTestCase.SerializableByteArrayHolder)
				);
		}
		#endif // !CF_1_0 && !CF_2_0

		private void TimeQueryLoop(string label, System.Type clazz)
		{
			com.db4o.query.Query query = NewQuery(clazz);
			com.db4o.ObjectSet os = query.Execute();
			Db4oUnit.Assert.AreEqual(INSTANCES, os.Size());
			while (os.HasNext())
			{
				Db4oUnit.Assert.AreEqual(ARRAY_LENGTH, ((com.db4o.db4ounit.common.types.arrays.ByteArrayTestCase.IByteArrayHolder
					)os.Next()).GetBytes().Length, label);
			}
		}

		internal virtual byte[] CreateByteArray()
		{
			byte[] bytes = new byte[ARRAY_LENGTH];
			for (int i = 0; i < bytes.Length; ++i)
			{
				bytes[i] = (byte)(i % 256);
			}
			return bytes;
		}
	}
}

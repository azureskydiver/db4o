namespace com.db4o.test
{
#if !CF_1_0 && !CF_2_0
	using System;
	using com.db4o;
	using com.db4o.config;
	using com.db4o.query;

	interface IByteArrayHolder
	{
		byte[] Bytes
		{
			get;
		}
	}

	class ByteArrayHolder : IByteArrayHolder
	{
		byte[] _bytes;
		
		public ByteArrayHolder(byte[] bytes)
		{
			_bytes = bytes;
		}

		public byte[] Bytes
		{
			get
			{
				return _bytes;
			}
		}
	}

	[Serializable]
	class SerializableByteArrayHolder : IByteArrayHolder
	{
		byte[] _bytes;
		
		public SerializableByteArrayHolder(byte[] bytes)
		{
			_bytes = bytes;
		}

		public byte[] Bytes
		{
			get
			{
				return _bytes;
			}
		}
	}

	public class ByteArray
	{	
		const int ITERATIONS = 10;
	
		const int INSTANCES = 2;
	
		const int ARRAY_LENGTH = 1024*512;
	
		public void Store()
		{
			Configure();
			for (int i=0; i<INSTANCES; ++i)
			{
				Tester.Store(new ByteArrayHolder(CreateByteArray()));
				Tester.Store(new SerializableByteArrayHolder(CreateByteArray()));
			}
		}

		public void Configure()
		{
			com.db4o.Db4o.Configure().ObjectClass(typeof(SerializableByteArrayHolder)).Translate(new TSerializable());
		}

		public void TestEmptyFunction()
		{
			Time("empty loop", new Function(Empty));
		}

		void Empty()
		{
		}

		public void TestSerializableByteArrayHolder()
		{
			Time("TSerializable", new Function(QuerySerializableByteArrayHolder));
		}

		private void QuerySerializableByteArrayHolder()
		{
			ExecuteQuery(typeof(SerializableByteArrayHolder));
		}
		
		public void TestByteArrayHolder()
		{
			Time("raw byte array", new Function(QueryByteArrayHolder));
		}

		private void QueryByteArrayHolder()
		{
			ExecuteQuery(typeof(ByteArrayHolder));
		}

		void ExecuteQuery(Type type)
		{
			Query query = Tester.Query();
			query.Constrain(type);
			
			ObjectSet os = query.Execute();
			Tester.Ensure(INSTANCES == os.Size());
			
			while (os.HasNext())
			{
				Tester.Ensure(ARRAY_LENGTH == ((IByteArrayHolder)os.Next()).Bytes.Length);
			}
		}

		delegate void Function();

		// HACK: parameter changed to _f for pascalcase conversion purposes
		void Time(string label, Function _f)
		{
			DateTime start = DateTime.Now;
			for (int i=0; i<ITERATIONS; ++i)
			{
				Tester.Close();
				Tester.Open();
				_f();
			}
			DateTime end = DateTime.Now;
			System.Console.WriteLine(label + ": {0} iterations took {1}ms", ITERATIONS, (end-start).TotalMilliseconds);
		}
		
		byte[] CreateByteArray()
		{
			byte[] bytes = new byte[ARRAY_LENGTH];
			for (int i=0; i<bytes.Length; ++i)
			{
				bytes[i] = (byte)(i % 256);
			}
			return bytes;
		}
	}
#endif
}

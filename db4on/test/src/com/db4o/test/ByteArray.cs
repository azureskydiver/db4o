namespace com.db4o.test
{
	using System;
	using com.db4o;
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
	
		public void store()
		{
			configure();
			for (int i=0; i<INSTANCES; ++i)
			{
				Test.store(new ByteArrayHolder(createByteArray()));
				Test.store(new SerializableByteArrayHolder(createByteArray()));
			}
		}

		public void configure()
		{
			com.db4o.Db4o.configure().objectClass(typeof(SerializableByteArrayHolder)).translate(new TSerializable());
		}

		public void testEmptyFunction()
		{
			time("empty loop", new Function(empty));
		}

		void empty()
		{
		}

		public void testSerializableByteArrayHolder()
		{
			time("TSerializable", new Function(querySerializableByteArrayHolder));
		}

		private void querySerializableByteArrayHolder()
		{
			executeQuery(typeof(SerializableByteArrayHolder));
		}
		
		public void testByteArrayHolder()
		{
			time("raw byte array", new Function(queryByteArrayHolder));
		}

		public void queryByteArrayHolder()
		{
			executeQuery(typeof(ByteArrayHolder));
		}

		void executeQuery(Type type)
		{
			Query query = Test.query();
			query.constrain(type);
			
			ObjectSet os = query.execute();
			Test.ensure(INSTANCES == os.size());
			
			while (os.hasNext())
			{
				Test.ensure(ARRAY_LENGTH == ((IByteArrayHolder)os.next()).Bytes.Length);
			}
		}

		delegate void Function();

		void time(string label, Function function)
		{
			DateTime start = DateTime.Now;
			for (int i=0; i<ITERATIONS; ++i)
			{
				Test.close();
				Test.open();
				function();
			}
			DateTime end = DateTime.Now;
			Console.WriteLine(label + ": {0} iterations took {1}ms", ITERATIONS, (end-start).TotalMilliseconds);
		}
		
		byte[] createByteArray()
		{
			byte[] bytes = new byte[ARRAY_LENGTH];
			for (int i=0; i<bytes.Length; ++i)
			{
				bytes[i] = (byte)(i % 256);
			}
			return bytes;
		}
	}
}

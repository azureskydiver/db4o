namespace com.db4o.test
{
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
	
		public void store()
		{
			configure();
			for (int i=0; i<INSTANCES; ++i)
			{
				Tester.store(new ByteArrayHolder(createByteArray()));
				Tester.store(new SerializableByteArrayHolder(createByteArray()));
			}
		}

		public void configure()
		{
			com.db4o.Db4o.configure().objectClass(typeof(SerializableByteArrayHolder)).translate(new TSerializable());
		}

		public void testEmptyFunction()
		{
			time("empty loop", new Function(Empty));
		}

		void Empty()
		{
		}

		public void testSerializableByteArrayHolder()
		{
			time("TSerializable", new Function(QuerySerializableByteArrayHolder));
		}

		private void QuerySerializableByteArrayHolder()
		{
			executeQuery(typeof(SerializableByteArrayHolder));
		}
		
		public void testByteArrayHolder()
		{
			time("raw byte array", new Function(QueryByteArrayHolder));
		}

		private void QueryByteArrayHolder()
		{
			executeQuery(typeof(ByteArrayHolder));
		}

		void executeQuery(Type type)
		{
			Query query = Tester.query();
			query.constrain(type);
			
			ObjectSet os = query.execute();
			Tester.ensure(INSTANCES == os.size());
			
			while (os.hasNext())
			{
				Tester.ensure(ARRAY_LENGTH == ((IByteArrayHolder)os.next()).Bytes.Length);
			}
		}

		delegate void Function();

		// HACK: parameter changed to _f for pascalcase conversion purposes
		void time(string label, Function _f)
		{
			DateTime start = DateTime.Now;
			for (int i=0; i<ITERATIONS; ++i)
			{
				Tester.close();
				Tester.open();
				_f();
			}
			DateTime end = DateTime.Now;
			System.Console.WriteLine(label + ": {0} iterations took {1}ms", ITERATIONS, (end-start).TotalMilliseconds);
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

namespace com.db4o.db4ounit.common.assorted
{
	public class ObjectMarshallerTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.assorted.ObjectMarshallerTestCase().RunSoloAndClientServer
				();
		}

		public class Item
		{
			public int _one;

			public long _two;

			public int _three;

			public Item(int one, long two, int three)
			{
				_one = one;
				_two = two;
				_three = three;
			}

			public Item()
			{
			}
		}

		public class ItemMarshaller : com.db4o.config.ObjectMarshaller
		{
			public bool readCalled;

			public bool writeCalled;

			public virtual void Reset()
			{
				readCalled = false;
				writeCalled = false;
			}

			public virtual void WriteFields(object obj, byte[] slot, int offset)
			{
				writeCalled = true;
				com.db4o.db4ounit.common.assorted.ObjectMarshallerTestCase.Item item = (com.db4o.db4ounit.common.assorted.ObjectMarshallerTestCase.Item
					)obj;
				com.db4o.foundation.PrimitiveCodec.WriteInt(slot, offset, item._one);
				offset += com.db4o.foundation.PrimitiveCodec.INT_LENGTH;
				com.db4o.foundation.PrimitiveCodec.WriteLong(slot, offset, item._two);
				offset += com.db4o.foundation.PrimitiveCodec.LONG_LENGTH;
				com.db4o.foundation.PrimitiveCodec.WriteInt(slot, offset, item._three);
			}

			public virtual void ReadFields(object obj, byte[] slot, int offset)
			{
				readCalled = true;
				com.db4o.db4ounit.common.assorted.ObjectMarshallerTestCase.Item item = (com.db4o.db4ounit.common.assorted.ObjectMarshallerTestCase.Item
					)obj;
				item._one = com.db4o.foundation.PrimitiveCodec.ReadInt(slot, offset);
				offset += com.db4o.foundation.PrimitiveCodec.INT_LENGTH;
				item._two = com.db4o.foundation.PrimitiveCodec.ReadLong(slot, offset);
				offset += com.db4o.foundation.PrimitiveCodec.LONG_LENGTH;
				item._three = com.db4o.foundation.PrimitiveCodec.ReadInt(slot, offset);
			}

			public virtual int MarshalledFieldLength()
			{
				return com.db4o.foundation.PrimitiveCodec.INT_LENGTH * 2 + com.db4o.foundation.PrimitiveCodec
					.LONG_LENGTH;
			}
		}

		public static readonly com.db4o.db4ounit.common.assorted.ObjectMarshallerTestCase.ItemMarshaller
			 marshaller = new com.db4o.db4ounit.common.assorted.ObjectMarshallerTestCase.ItemMarshaller
			();

		protected override void Configure(com.db4o.config.Configuration config)
		{
			base.Configure(config);
			config.ObjectClass(typeof(com.db4o.db4ounit.common.assorted.ObjectMarshallerTestCase.Item)
				).MarshallWith(marshaller);
		}

		protected override void Store()
		{
			marshaller.Reset();
			Store(new com.db4o.db4ounit.common.assorted.ObjectMarshallerTestCase.Item(int.MaxValue
				, long.MaxValue, 1));
			Db4oUnit.Assert.IsTrue(marshaller.writeCalled);
		}

		public virtual void Test()
		{
			com.db4o.db4ounit.common.assorted.ObjectMarshallerTestCase.Item item = AssertRetrieve
				();
			Db4oUnit.Assert.IsTrue(marshaller.readCalled);
			marshaller.Reset();
			Db().Set(item);
			Db4oUnit.Assert.IsTrue(marshaller.writeCalled);
			Defragment();
			AssertRetrieve();
		}

		private com.db4o.db4ounit.common.assorted.ObjectMarshallerTestCase.Item AssertRetrieve
			()
		{
			marshaller.Reset();
			com.db4o.db4ounit.common.assorted.ObjectMarshallerTestCase.Item item = (com.db4o.db4ounit.common.assorted.ObjectMarshallerTestCase.Item
				)RetrieveOnlyInstance(typeof(com.db4o.db4ounit.common.assorted.ObjectMarshallerTestCase.Item)
				);
			Db4oUnit.Assert.AreEqual(int.MaxValue, item._one);
			Db4oUnit.Assert.AreEqual(long.MaxValue, item._two);
			Db4oUnit.Assert.AreEqual(1, item._three);
			return item;
		}
	}
}

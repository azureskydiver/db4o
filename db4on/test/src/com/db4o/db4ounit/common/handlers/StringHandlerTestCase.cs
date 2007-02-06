namespace com.db4o.db4ounit.common.handlers
{
	public class StringHandlerTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public virtual void TestIndexMarshalling()
		{
			com.db4o.@internal.Buffer reader = new com.db4o.@internal.Buffer(2 * com.db4o.@internal.Const4
				.INT_LENGTH);
			com.db4o.@internal.ObjectContainerBase stream = (com.db4o.@internal.ObjectContainerBase
				)Db();
			com.db4o.@internal.handlers.StringHandler handler = new com.db4o.@internal.handlers.StringHandler
				(stream, stream.StringIO());
			com.db4o.@internal.slots.Slot original = new com.db4o.@internal.slots.Slot(unchecked(
				(int)(0xdb)), unchecked((int)(0x40)));
			handler.WriteIndexEntry(reader, original);
			reader._offset = 0;
			com.db4o.@internal.slots.Slot retrieved = (com.db4o.@internal.slots.Slot)handler.
				ReadIndexEntry(reader);
			Db4oUnit.Assert.AreEqual(original._address, retrieved._address);
			Db4oUnit.Assert.AreEqual(original._length, retrieved._length);
		}
	}
}

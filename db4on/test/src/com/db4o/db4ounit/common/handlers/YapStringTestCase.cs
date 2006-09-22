namespace com.db4o.db4ounit.common.handlers
{
	public class YapStringTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public virtual void TestIndexMarshalling()
		{
			com.db4o.YapReader reader = new com.db4o.YapReader(2 * com.db4o.YapConst.INT_LENGTH
				);
			com.db4o.YapStream stream = (com.db4o.YapStream)Db();
			com.db4o.YapString handler = new com.db4o.YapString(stream, stream.StringIO());
			com.db4o.inside.slots.Slot original = new com.db4o.inside.slots.Slot(unchecked((int
				)(0xdb)), unchecked((int)(0x40)));
			handler.WriteIndexEntry(reader, original);
			reader._offset = 0;
			com.db4o.inside.slots.Slot retrieved = (com.db4o.inside.slots.Slot)handler.ReadIndexEntry
				(reader);
			Db4oUnit.Assert.AreEqual(original._address, retrieved._address);
			Db4oUnit.Assert.AreEqual(original._length, retrieved._length);
		}
	}
}

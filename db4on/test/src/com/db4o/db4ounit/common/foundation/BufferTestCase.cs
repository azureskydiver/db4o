namespace com.db4o.db4ounit.common.foundation
{
	public class BufferTestCase : Db4oUnit.TestCase
	{
		private const int READERLENGTH = 64;

		public virtual void TestCopy()
		{
			com.db4o.@internal.Buffer from = new com.db4o.@internal.Buffer(READERLENGTH);
			for (int i = 0; i < READERLENGTH; i++)
			{
				from.Append((byte)i);
			}
			com.db4o.@internal.Buffer to = new com.db4o.@internal.Buffer(READERLENGTH - 1);
			from.CopyTo(to, 1, 2, 10);
			Db4oUnit.Assert.AreEqual(0, to.ReadByte());
			Db4oUnit.Assert.AreEqual(0, to.ReadByte());
			for (int i = 1; i <= 10; i++)
			{
				Db4oUnit.Assert.AreEqual((byte)i, to.ReadByte());
			}
			for (int i = 12; i < READERLENGTH - 1; i++)
			{
				Db4oUnit.Assert.AreEqual(0, to.ReadByte());
			}
		}
	}
}

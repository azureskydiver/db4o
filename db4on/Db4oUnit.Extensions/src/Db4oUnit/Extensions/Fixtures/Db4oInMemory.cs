namespace Db4oUnit.Extensions.Fixtures
{
	public class Db4oInMemory : Db4oUnit.Extensions.Fixtures.AbstractDb4oFixture
	{
		private com.db4o.ext.MemoryFile _memoryFile;

		public override void Open()
		{
			if (null == _memoryFile)
			{
				_memoryFile = new com.db4o.ext.MemoryFile();
			}
			Db(com.db4o.ext.ExtDb4o.OpenMemoryFile(_memoryFile).Ext());
		}

		public override void Clean()
		{
			_memoryFile = null;
		}
	}
}

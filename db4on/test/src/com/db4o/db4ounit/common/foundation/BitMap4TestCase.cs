namespace com.db4o.db4ounit.common.foundation
{
	public class BitMap4TestCase : Db4oUnit.TestCase
	{
		public virtual void Test()
		{
			byte[] buffer = new byte[100];
			for (int i = 0; i < 17; i++)
			{
				com.db4o.foundation.BitMap4 map = new com.db4o.foundation.BitMap4(i);
				map.WriteTo(buffer, 11);
				com.db4o.foundation.BitMap4 reReadMap = new com.db4o.foundation.BitMap4(buffer, 11
					, i);
				for (int j = 0; j < i; j++)
				{
					TBit(map, j);
					TBit(reReadMap, j);
				}
			}
		}

		private void TBit(com.db4o.foundation.BitMap4 map, int bit)
		{
			map.SetTrue(bit);
			Db4oUnit.Assert.IsTrue(map.IsTrue(bit));
			map.SetFalse(bit);
			Db4oUnit.Assert.IsFalse(map.IsTrue(bit));
			map.SetTrue(bit);
			Db4oUnit.Assert.IsTrue(map.IsTrue(bit));
		}
	}
}

namespace Db4objects.Drs.Test
{
	public class ByteArrayHolder : Db4objects.Drs.Test.IIByteArrayHolder
	{
		private byte[] _bytes;

		public ByteArrayHolder()
		{
		}

		public ByteArrayHolder(byte[] bytes)
		{
			this._bytes = bytes;
		}

		public virtual byte[] GetBytes()
		{
			return _bytes;
		}

		public virtual void SetBytes(byte[] bytes)
		{
			_bytes = bytes;
		}
	}
}

namespace com.db4o.foundation
{
	internal sealed class EmptyIterator : com.db4o.foundation.Iterator4
	{
		internal EmptyIterator()
		{
		}

		public bool MoveNext()
		{
			return false;
		}

		public object Current()
		{
			throw new System.InvalidOperationException();
		}
	}
}

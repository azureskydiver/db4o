namespace com.db4o.foundation
{
	internal sealed class EmptyIterator : com.db4o.foundation.Iterator4Impl
	{
		internal EmptyIterator() : base(null)
		{
		}

		public sealed override bool HasNext()
		{
			return false;
		}
	}
}

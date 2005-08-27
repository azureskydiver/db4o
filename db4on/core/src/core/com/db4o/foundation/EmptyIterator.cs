
namespace com.db4o.foundation
{
	internal sealed class EmptyIterator : com.db4o.foundation.Iterator4
	{
		internal EmptyIterator() : base(null)
		{
		}

		public sealed override bool hasNext()
		{
			return false;
		}
	}
}

namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public class IDHandler : com.db4o.@internal.PrimitiveIntHandler
	{
		public IDHandler(com.db4o.@internal.ObjectContainerBase stream) : base(stream)
		{
		}

		public override void DefragIndexEntry(com.db4o.@internal.ReaderPair readers)
		{
			readers.CopyID(true, false);
		}
	}
}

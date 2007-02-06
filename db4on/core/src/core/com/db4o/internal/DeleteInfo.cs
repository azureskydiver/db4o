namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public class DeleteInfo : com.db4o.@internal.TreeInt
	{
		internal int _cascade;

		public com.db4o.@internal.ObjectReference _reference;

		public DeleteInfo(int id, com.db4o.@internal.ObjectReference reference, int cascade
			) : base(id)
		{
			_reference = reference;
			_cascade = cascade;
		}

		public override object ShallowClone()
		{
			com.db4o.@internal.DeleteInfo deleteinfo = new com.db4o.@internal.DeleteInfo(0, _reference
				, _cascade);
			return ShallowCloneInternal(deleteinfo);
		}
	}
}

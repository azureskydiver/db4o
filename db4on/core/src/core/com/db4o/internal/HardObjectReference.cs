namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public class HardObjectReference
	{
		public static readonly com.db4o.@internal.HardObjectReference INVALID = new com.db4o.@internal.HardObjectReference
			(null, null);

		public readonly com.db4o.@internal.ObjectReference _reference;

		public readonly object _object;

		public HardObjectReference(com.db4o.@internal.ObjectReference @ref, object obj)
		{
			_reference = @ref;
			_object = obj;
		}

		public static com.db4o.@internal.HardObjectReference PeekPersisted(com.db4o.@internal.Transaction
			 trans, int id, int depth)
		{
			com.db4o.@internal.ObjectReference @ref = new com.db4o.@internal.ObjectReference(
				id);
			object obj = @ref.PeekPersisted(trans, depth);
			return new com.db4o.@internal.HardObjectReference(@ref, obj);
		}
	}
}

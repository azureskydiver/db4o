namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public class LazyObjectReference : com.db4o.ext.ObjectInfo
	{
		private readonly com.db4o.@internal.ObjectContainerBase _container;

		private readonly int _id;

		public LazyObjectReference(com.db4o.@internal.ObjectContainerBase container, int 
			id)
		{
			_container = container;
			_id = id;
		}

		public virtual long GetInternalID()
		{
			return _id;
		}

		public virtual object GetObject()
		{
			return Reference().GetObject();
		}

		public virtual com.db4o.ext.Db4oUUID GetUUID()
		{
			return Reference().GetUUID();
		}

		public virtual long GetVersion()
		{
			return Reference().GetVersion();
		}

		private com.db4o.@internal.ObjectReference Reference()
		{
			com.db4o.@internal.HardObjectReference hardReference = _container.GetHardObjectReferenceById
				(_id);
			if (hardReference == null)
			{
				return null;
			}
			return hardReference._reference;
		}
	}
}

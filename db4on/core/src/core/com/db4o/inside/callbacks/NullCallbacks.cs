namespace com.db4o.inside.callbacks
{
	public class NullCallbacks : com.db4o.inside.callbacks.Callbacks
	{
		public virtual void OnQueryFinished(com.db4o.query.Query query)
		{
		}

		public virtual void OnQueryStarted(com.db4o.query.Query query)
		{
		}

		public virtual bool ObjectCanNew(object obj)
		{
			return true;
		}

		public virtual bool ObjectCanActivate(object obj)
		{
			return true;
		}

		public virtual bool ObjectCanUpdate(object obj)
		{
			return true;
		}

		public virtual bool ObjectCanDelete(object obj)
		{
			return true;
		}

		public virtual bool ObjectCanDeactivate(object obj)
		{
			return true;
		}

		public virtual void ObjectOnNew(object obj)
		{
		}

		public virtual void ObjectOnActivate(object obj)
		{
		}

		public virtual void ObjectOnUpdate(object obj)
		{
		}

		public virtual void ObjectOnDelete(object obj)
		{
		}

		public virtual void ObjectOnDeactivate(object obj)
		{
		}
	}
}

namespace com.db4o.events.impl
{
	/// <exclude></exclude>
	public class EventRegistryImpl : com.db4o.inside.callbacks.Callbacks, com.db4o.events.EventRegistry
	{
		protected QueryEventHandler _queryStarted;

		protected QueryEventHandler _queryFinished;

		protected CancellableObjectEventHandler _creating;

		protected CancellableObjectEventHandler _activating;

		protected CancellableObjectEventHandler _updating;

		protected CancellableObjectEventHandler _deleting;

		protected CancellableObjectEventHandler _deactivating;

		protected ObjectEventHandler _created;

		protected ObjectEventHandler _activated;

		protected ObjectEventHandler _updated;

		protected ObjectEventHandler _deleted;

		protected ObjectEventHandler _deactivated;

		public virtual void OnQueryFinished(com.db4o.query.Query query)
		{
			com.db4o.events.impl.EventPlatform.TriggerQueryEvent(_queryFinished, query);
		}

		public virtual void OnQueryStarted(com.db4o.query.Query query)
		{
			com.db4o.events.impl.EventPlatform.TriggerQueryEvent(_queryStarted, query);
		}

		public virtual bool ObjectCanNew(object obj)
		{
			return com.db4o.events.impl.EventPlatform.TriggerCancellableObjectEventArgs(_creating
				, obj);
		}

		public virtual bool ObjectCanActivate(object obj)
		{
			return com.db4o.events.impl.EventPlatform.TriggerCancellableObjectEventArgs(_activating
				, obj);
		}

		public virtual bool ObjectCanUpdate(object obj)
		{
			return com.db4o.events.impl.EventPlatform.TriggerCancellableObjectEventArgs(_updating
				, obj);
		}

		public virtual bool ObjectCanDelete(object obj)
		{
			return com.db4o.events.impl.EventPlatform.TriggerCancellableObjectEventArgs(_deleting
				, obj);
		}

		public virtual bool ObjectCanDeactivate(object obj)
		{
			return com.db4o.events.impl.EventPlatform.TriggerCancellableObjectEventArgs(_deactivating
				, obj);
		}

		public virtual void ObjectOnActivate(object obj)
		{
			com.db4o.events.impl.EventPlatform.TriggerObjectEvent(_activated, obj);
		}

		public virtual void ObjectOnNew(object obj)
		{
			com.db4o.events.impl.EventPlatform.TriggerObjectEvent(_created, obj);
		}

		public virtual void ObjectOnUpdate(object obj)
		{
			com.db4o.events.impl.EventPlatform.TriggerObjectEvent(_updated, obj);
		}

		public virtual void ObjectOnDelete(object obj)
		{
			com.db4o.events.impl.EventPlatform.TriggerObjectEvent(_deleted, obj);
		}

		public virtual void ObjectOnDeactivate(object obj)
		{
			com.db4o.events.impl.EventPlatform.TriggerObjectEvent(_deactivated, obj);
		}

		public virtual event QueryEventHandler QueryFinished
		{
			add
			{
				_queryFinished = (QueryEventHandler)System.Delegate.Combine(_queryFinished, value
					);
			}
			remove
			{
				_queryFinished = (QueryEventHandler)System.Delegate.Remove(_queryFinished, value);
			}
		}

		public virtual event QueryEventHandler QueryStarted
		{
			add
			{
				_queryStarted = (QueryEventHandler)System.Delegate.Combine(_queryStarted, value);
			}
			remove
			{
				_queryStarted = (QueryEventHandler)System.Delegate.Remove(_queryStarted, value);
			}
		}

		public virtual event CancellableObjectEventHandler Creating
		{
			add
			{
				_creating = (CancellableObjectEventHandler)System.Delegate.Combine(_creating, value
					);
			}
			remove
			{
				_creating = (CancellableObjectEventHandler)System.Delegate.Remove(_creating, value
					);
			}
		}

		public virtual event CancellableObjectEventHandler Activating
		{
			add
			{
				_activating = (CancellableObjectEventHandler)System.Delegate.Combine(_activating, 
					value);
			}
			remove
			{
				_activating = (CancellableObjectEventHandler)System.Delegate.Remove(_activating, 
					value);
			}
		}

		public virtual event CancellableObjectEventHandler Updating
		{
			add
			{
				_updating = (CancellableObjectEventHandler)System.Delegate.Combine(_updating, value
					);
			}
			remove
			{
				_updating = (CancellableObjectEventHandler)System.Delegate.Remove(_updating, value
					);
			}
		}

		public virtual event CancellableObjectEventHandler Deleting
		{
			add
			{
				_deleting = (CancellableObjectEventHandler)System.Delegate.Combine(_deleting, value
					);
			}
			remove
			{
				_deleting = (CancellableObjectEventHandler)System.Delegate.Remove(_deleting, value
					);
			}
		}

		public virtual event CancellableObjectEventHandler Deactivating
		{
			add
			{
				_deactivating = (CancellableObjectEventHandler)System.Delegate.Combine(_deactivating
					, value);
			}
			remove
			{
				_deactivating = (CancellableObjectEventHandler)System.Delegate.Remove(_deactivating
					, value);
			}
		}

		public virtual event ObjectEventHandler Created
		{
			add
			{
				_created = (ObjectEventHandler)System.Delegate.Combine(_created, value);
			}
			remove
			{
				_created = (ObjectEventHandler)System.Delegate.Remove(_created, value);
			}
		}

		public virtual event ObjectEventHandler Activated
		{
			add
			{
				_activated = (ObjectEventHandler)System.Delegate.Combine(_activated, value);
			}
			remove
			{
				_activated = (ObjectEventHandler)System.Delegate.Remove(_activated, value);
			}
		}

		public virtual event ObjectEventHandler Updated
		{
			add
			{
				_updated = (ObjectEventHandler)System.Delegate.Combine(_updated, value);
			}
			remove
			{
				_updated = (ObjectEventHandler)System.Delegate.Remove(_updated, value);
			}
		}

		public virtual event ObjectEventHandler Deleted
		{
			add
			{
				_deleted = (ObjectEventHandler)System.Delegate.Combine(_deleted, value);
			}
			remove
			{
				_deleted = (ObjectEventHandler)System.Delegate.Remove(_deleted, value);
			}
		}

		public virtual event ObjectEventHandler Deactivated
		{
			add
			{
				_deactivated = (ObjectEventHandler)System.Delegate.Combine(_deactivated, value);
			}
			remove
			{
				_deactivated = (ObjectEventHandler)System.Delegate.Remove(_deactivated, value);
			}
		}
	}
}

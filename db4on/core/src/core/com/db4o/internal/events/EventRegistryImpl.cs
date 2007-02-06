namespace com.db4o.@internal.events
{
	/// <exclude></exclude>
	public class EventRegistryImpl : com.db4o.@internal.callbacks.Callbacks, com.db4o.events.EventRegistry
	{
		protected com.db4o.events.QueryEventHandler _queryStarted;

		protected com.db4o.events.QueryEventHandler _queryFinished;

		protected com.db4o.events.CancellableObjectEventHandler _creating;

		protected com.db4o.events.CancellableObjectEventHandler _activating;

		protected com.db4o.events.CancellableObjectEventHandler _updating;

		protected com.db4o.events.CancellableObjectEventHandler _deleting;

		protected com.db4o.events.CancellableObjectEventHandler _deactivating;

		protected com.db4o.events.ObjectEventHandler _created;

		protected com.db4o.events.ObjectEventHandler _activated;

		protected com.db4o.events.ObjectEventHandler _updated;

		protected com.db4o.events.ObjectEventHandler _deleted;

		protected com.db4o.events.ObjectEventHandler _deactivated;

		public virtual void OnQueryFinished(com.db4o.query.Query query)
		{
			com.db4o.@internal.events.EventPlatform.TriggerQueryEvent(_queryFinished, query);
		}

		public virtual void OnQueryStarted(com.db4o.query.Query query)
		{
			com.db4o.@internal.events.EventPlatform.TriggerQueryEvent(_queryStarted, query);
		}

		public virtual bool ObjectCanNew(object obj)
		{
			return com.db4o.@internal.events.EventPlatform.TriggerCancellableObjectEventArgs(
				_creating, obj);
		}

		public virtual bool ObjectCanActivate(object obj)
		{
			return com.db4o.@internal.events.EventPlatform.TriggerCancellableObjectEventArgs(
				_activating, obj);
		}

		public virtual bool ObjectCanUpdate(object obj)
		{
			return com.db4o.@internal.events.EventPlatform.TriggerCancellableObjectEventArgs(
				_updating, obj);
		}

		public virtual bool ObjectCanDelete(object obj)
		{
			return com.db4o.@internal.events.EventPlatform.TriggerCancellableObjectEventArgs(
				_deleting, obj);
		}

		public virtual bool ObjectCanDeactivate(object obj)
		{
			return com.db4o.@internal.events.EventPlatform.TriggerCancellableObjectEventArgs(
				_deactivating, obj);
		}

		public virtual void ObjectOnActivate(object obj)
		{
			com.db4o.@internal.events.EventPlatform.TriggerObjectEvent(_activated, obj);
		}

		public virtual void ObjectOnNew(object obj)
		{
			com.db4o.@internal.events.EventPlatform.TriggerObjectEvent(_created, obj);
		}

		public virtual void ObjectOnUpdate(object obj)
		{
			com.db4o.@internal.events.EventPlatform.TriggerObjectEvent(_updated, obj);
		}

		public virtual void ObjectOnDelete(object obj)
		{
			com.db4o.@internal.events.EventPlatform.TriggerObjectEvent(_deleted, obj);
		}

		public virtual void ObjectOnDeactivate(object obj)
		{
			com.db4o.@internal.events.EventPlatform.TriggerObjectEvent(_deactivated, obj);
		}

		public virtual event com.db4o.events.QueryEventHandler QueryFinished
		{
			add
			{
				_queryFinished = (com.db4o.events.QueryEventHandler)System.Delegate.Combine(_queryFinished
					, value);
			}
			remove
			{
				_queryFinished = (com.db4o.events.QueryEventHandler)System.Delegate.Remove(_queryFinished
					, value);
			}
		}

		public virtual event com.db4o.events.QueryEventHandler QueryStarted
		{
			add
			{
				_queryStarted = (com.db4o.events.QueryEventHandler)System.Delegate.Combine(_queryStarted
					, value);
			}
			remove
			{
				_queryStarted = (com.db4o.events.QueryEventHandler)System.Delegate.Remove(_queryStarted
					, value);
			}
		}

		public virtual event com.db4o.events.CancellableObjectEventHandler Creating
		{
			add
			{
				_creating = (com.db4o.events.CancellableObjectEventHandler)System.Delegate.Combine
					(_creating, value);
			}
			remove
			{
				_creating = (com.db4o.events.CancellableObjectEventHandler)System.Delegate.Remove
					(_creating, value);
			}
		}

		public virtual event com.db4o.events.CancellableObjectEventHandler Activating
		{
			add
			{
				_activating = (com.db4o.events.CancellableObjectEventHandler)System.Delegate.Combine
					(_activating, value);
			}
			remove
			{
				_activating = (com.db4o.events.CancellableObjectEventHandler)System.Delegate.Remove
					(_activating, value);
			}
		}

		public virtual event com.db4o.events.CancellableObjectEventHandler Updating
		{
			add
			{
				_updating = (com.db4o.events.CancellableObjectEventHandler)System.Delegate.Combine
					(_updating, value);
			}
			remove
			{
				_updating = (com.db4o.events.CancellableObjectEventHandler)System.Delegate.Remove
					(_updating, value);
			}
		}

		public virtual event com.db4o.events.CancellableObjectEventHandler Deleting
		{
			add
			{
				_deleting = (com.db4o.events.CancellableObjectEventHandler)System.Delegate.Combine
					(_deleting, value);
			}
			remove
			{
				_deleting = (com.db4o.events.CancellableObjectEventHandler)System.Delegate.Remove
					(_deleting, value);
			}
		}

		public virtual event com.db4o.events.CancellableObjectEventHandler Deactivating
		{
			add
			{
				_deactivating = (com.db4o.events.CancellableObjectEventHandler)System.Delegate.Combine
					(_deactivating, value);
			}
			remove
			{
				_deactivating = (com.db4o.events.CancellableObjectEventHandler)System.Delegate.Remove
					(_deactivating, value);
			}
		}

		public virtual event com.db4o.events.ObjectEventHandler Created
		{
			add
			{
				_created = (com.db4o.events.ObjectEventHandler)System.Delegate.Combine(_created, 
					value);
			}
			remove
			{
				_created = (com.db4o.events.ObjectEventHandler)System.Delegate.Remove(_created, value
					);
			}
		}

		public virtual event com.db4o.events.ObjectEventHandler Activated
		{
			add
			{
				_activated = (com.db4o.events.ObjectEventHandler)System.Delegate.Combine(_activated
					, value);
			}
			remove
			{
				_activated = (com.db4o.events.ObjectEventHandler)System.Delegate.Remove(_activated
					, value);
			}
		}

		public virtual event com.db4o.events.ObjectEventHandler Updated
		{
			add
			{
				_updated = (com.db4o.events.ObjectEventHandler)System.Delegate.Combine(_updated, 
					value);
			}
			remove
			{
				_updated = (com.db4o.events.ObjectEventHandler)System.Delegate.Remove(_updated, value
					);
			}
		}

		public virtual event com.db4o.events.ObjectEventHandler Deleted
		{
			add
			{
				_deleted = (com.db4o.events.ObjectEventHandler)System.Delegate.Combine(_deleted, 
					value);
			}
			remove
			{
				_deleted = (com.db4o.events.ObjectEventHandler)System.Delegate.Remove(_deleted, value
					);
			}
		}

		public virtual event com.db4o.events.ObjectEventHandler Deactivated
		{
			add
			{
				_deactivated = (com.db4o.events.ObjectEventHandler)System.Delegate.Combine(_deactivated
					, value);
			}
			remove
			{
				_deactivated = (com.db4o.events.ObjectEventHandler)System.Delegate.Remove(_deactivated
					, value);
			}
		}
	}
}

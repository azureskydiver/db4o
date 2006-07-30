namespace com.db4o
{
	/// <summary>base class for all database aware objects</summary>
	/// <exclude></exclude>
	/// <persistent></persistent>
	public class P1Object : com.db4o.Db4oTypeImpl
	{
		[com.db4o.Transient]
		private com.db4o.Transaction i_trans;

		[com.db4o.Transient]
		private com.db4o.YapObject i_yapObject;

		public P1Object()
		{
		}

		internal P1Object(com.db4o.Transaction a_trans)
		{
			i_trans = a_trans;
		}

		public virtual void Activate(object a_obj, int a_depth)
		{
			if (i_trans != null)
			{
				if (a_depth < 0)
				{
					i_trans.i_stream.Activate1(i_trans, a_obj);
				}
				else
				{
					i_trans.i_stream.Activate1(i_trans, a_obj, a_depth);
				}
			}
		}

		public virtual int ActivationDepth()
		{
			return 1;
		}

		public virtual int AdjustReadDepth(int a_depth)
		{
			return a_depth;
		}

		public virtual bool CanBind()
		{
			return false;
		}

		public virtual void CheckActive()
		{
			if (i_trans != null)
			{
				if (i_yapObject == null)
				{
					i_yapObject = i_trans.i_stream.GetYapObject(this);
					if (i_yapObject == null)
					{
						i_trans.i_stream.Set(this);
						i_yapObject = i_trans.i_stream.GetYapObject(this);
					}
				}
				if (ValidYapObject())
				{
					i_yapObject.Activate(i_trans, this, ActivationDepth(), false);
				}
			}
		}

		public virtual object CreateDefault(com.db4o.Transaction a_trans)
		{
			throw com.db4o.inside.Exceptions4.VirtualException();
		}

		internal virtual void Deactivate()
		{
			if (ValidYapObject())
			{
				i_yapObject.Deactivate(i_trans, ActivationDepth());
			}
		}

		internal virtual void Delete()
		{
			if (i_trans != null)
			{
				if (i_yapObject == null)
				{
					i_yapObject = i_trans.i_stream.GetYapObject(this);
				}
				if (ValidYapObject())
				{
					i_trans.i_stream.Delete3(i_trans, i_yapObject, this, 0, false);
				}
			}
		}

		protected virtual void Delete(object a_obj)
		{
			if (i_trans != null)
			{
				i_trans.i_stream.Delete(a_obj);
			}
		}

		protected virtual long GetIDOf(object a_obj)
		{
			if (i_trans == null)
			{
				return 0;
			}
			return i_trans.i_stream.GetID(a_obj);
		}

		protected virtual com.db4o.Transaction GetTrans()
		{
			return i_trans;
		}

		public virtual bool HasClassIndex()
		{
			return false;
		}

		public virtual void PreDeactivate()
		{
		}

		protected virtual object Replicate(com.db4o.Transaction fromTrans, com.db4o.Transaction
			 toTrans)
		{
			com.db4o.YapStream fromStream = fromTrans.i_stream;
			com.db4o.YapStream toStream = toTrans.i_stream;
			com.db4o.inside.replication.MigrationConnection mgc = fromStream.i_handlers.i_migration;
			lock (fromStream.Lock())
			{
				int id = toStream.OldReplicationHandles(this);
				if (id == -1)
				{
					return this;
				}
				if (id > 0)
				{
					return toStream.GetByID(id);
				}
				if (mgc != null)
				{
					object otherObj = mgc.IdentityFor(this);
					if (otherObj != null)
					{
						return otherObj;
					}
				}
				com.db4o.P1Object replica = (com.db4o.P1Object)CreateDefault(toTrans);
				if (mgc != null)
				{
					mgc.MapReference(replica, i_yapObject);
					mgc.MapIdentity(this, replica);
				}
				replica.Store(0);
				return replica;
			}
		}

		public virtual void ReplicateFrom(object obj)
		{
		}

		public virtual void SetTrans(com.db4o.Transaction a_trans)
		{
			i_trans = a_trans;
		}

		public virtual void SetYapObject(com.db4o.YapObject a_yapObject)
		{
			i_yapObject = a_yapObject;
		}

		protected virtual void Store(object a_obj)
		{
			if (i_trans != null)
			{
				i_trans.i_stream.SetInternal(i_trans, a_obj, true);
			}
		}

		public virtual object StoredTo(com.db4o.Transaction a_trans)
		{
			i_trans = a_trans;
			return this;
		}

		internal virtual object StreamLock()
		{
			if (i_trans != null)
			{
				i_trans.i_stream.CheckClosed();
				return i_trans.i_stream.Lock();
			}
			return this;
		}

		public virtual void Store(int a_depth)
		{
			if (i_trans != null)
			{
				if (i_yapObject == null)
				{
					i_yapObject = i_trans.i_stream.GetYapObject(this);
					if (i_yapObject == null)
					{
						i_trans.i_stream.SetInternal(i_trans, this, true);
						i_yapObject = i_trans.i_stream.GetYapObject(this);
						return;
					}
				}
				Update(a_depth);
			}
		}

		internal virtual void Update()
		{
			Update(ActivationDepth());
		}

		internal virtual void Update(int depth)
		{
			if (ValidYapObject())
			{
				i_trans.i_stream.BeginEndSet(i_trans);
				i_yapObject.WriteUpdate(i_trans, depth);
				i_trans.i_stream.CheckStillToSet();
				i_trans.i_stream.BeginEndSet(i_trans);
			}
		}

		internal virtual void UpdateInternal()
		{
			UpdateInternal(ActivationDepth());
		}

		internal virtual void UpdateInternal(int depth)
		{
			if (ValidYapObject())
			{
				i_yapObject.WriteUpdate(i_trans, depth);
				i_trans.i_stream.RememberJustSet(i_yapObject.GetID());
				i_trans.i_stream.CheckStillToSet();
			}
		}

		private bool ValidYapObject()
		{
			return (i_trans != null) && (i_yapObject != null) && (i_yapObject.GetID() > 0);
		}
	}
}

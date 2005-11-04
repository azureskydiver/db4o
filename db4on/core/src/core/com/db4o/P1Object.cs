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

		public virtual void activate(object a_obj, int a_depth)
		{
			if (i_trans != null)
			{
				if (a_depth < 0)
				{
					i_trans.i_stream.activate1(i_trans, a_obj);
				}
				else
				{
					i_trans.i_stream.activate1(i_trans, a_obj, a_depth);
				}
			}
		}

		public virtual int activationDepth()
		{
			return 1;
		}

		public virtual int adjustReadDepth(int a_depth)
		{
			return a_depth;
		}

		public virtual bool canBind()
		{
			return false;
		}

		internal virtual void checkActive()
		{
			if (i_trans != null)
			{
				if (i_yapObject == null)
				{
					i_yapObject = i_trans.i_stream.getYapObject(this);
					if (i_yapObject == null)
					{
						i_trans.i_stream.set(this);
						i_yapObject = i_trans.i_stream.getYapObject(this);
					}
				}
				if (validYapObject())
				{
					i_yapObject.activate(i_trans, this, activationDepth(), false);
				}
			}
		}

		public virtual object createDefault(com.db4o.Transaction a_trans)
		{
			throw com.db4o.YapConst.virtualException();
		}

		internal virtual void deactivate()
		{
			if (validYapObject())
			{
				i_yapObject.deactivate(i_trans, activationDepth());
			}
		}

		internal virtual void delete()
		{
			if (i_trans != null)
			{
				if (i_yapObject == null)
				{
					i_yapObject = i_trans.i_stream.getYapObject(this);
				}
				if (validYapObject())
				{
					i_trans.i_stream.delete3(i_trans, i_yapObject, this, 0, false);
				}
			}
		}

		protected virtual void delete(object a_obj)
		{
			if (i_trans != null)
			{
				i_trans.i_stream.delete(a_obj);
			}
		}

		protected virtual long getIDOf(object a_obj)
		{
			if (i_trans == null)
			{
				return 0;
			}
			return i_trans.i_stream.getID(a_obj);
		}

		protected virtual com.db4o.Transaction getTrans()
		{
			return i_trans;
		}

		public virtual bool hasClassIndex()
		{
			return false;
		}

		public virtual void preDeactivate()
		{
		}

		protected virtual object replicate(com.db4o.Transaction fromTrans, com.db4o.Transaction
			 toTrans)
		{
			com.db4o.YapStream fromStream = fromTrans.i_stream;
			com.db4o.YapStream toStream = toTrans.i_stream;
			lock (fromStream.Lock())
			{
				int id = toStream.replicationHandles(this);
				if (id == -1)
				{
					return this;
				}
				if (id > 0)
				{
					return toStream.getByID(id);
				}
				com.db4o.P1Object replica = (com.db4o.P1Object)createDefault(toTrans);
				if (fromStream.i_handlers.i_migration != null)
				{
					fromStream.i_handlers.i_migration.mapReference(replica, i_yapObject);
				}
				replica.store(0);
				return replica;
			}
		}

		public virtual void replicateFrom(object obj)
		{
		}

		public virtual void setTrans(com.db4o.Transaction a_trans)
		{
			i_trans = a_trans;
		}

		public virtual void setYapObject(com.db4o.YapObject a_yapObject)
		{
			i_yapObject = a_yapObject;
		}

		protected virtual void store(object a_obj)
		{
			if (i_trans != null)
			{
				i_trans.i_stream.setInternal(i_trans, a_obj, true);
			}
		}

		public virtual object storedTo(com.db4o.Transaction a_trans)
		{
			i_trans = a_trans;
			return this;
		}

		internal virtual object streamLock()
		{
			if (i_trans != null)
			{
				i_trans.i_stream.checkClosed();
				return i_trans.i_stream.Lock();
			}
			return this;
		}

		public virtual void store(int a_depth)
		{
			if (i_trans != null)
			{
				if (i_yapObject == null)
				{
					i_yapObject = i_trans.i_stream.getYapObject(this);
					if (i_yapObject == null)
					{
						i_trans.i_stream.setInternal(i_trans, this, true);
						i_yapObject = i_trans.i_stream.getYapObject(this);
						return;
					}
				}
				update(a_depth);
			}
		}

		internal virtual void update()
		{
			update(activationDepth());
		}

		internal virtual void update(int depth)
		{
			if (validYapObject())
			{
				i_trans.i_stream.beginEndSet(i_trans);
				i_yapObject.writeUpdate(i_trans, depth);
				i_trans.i_stream.checkStillToSet();
				i_trans.i_stream.beginEndSet(i_trans);
			}
		}

		internal virtual void updateInternal()
		{
			updateInternal(activationDepth());
		}

		internal virtual void updateInternal(int depth)
		{
			if (validYapObject())
			{
				i_yapObject.writeUpdate(i_trans, depth);
				i_trans.i_stream.rememberJustSet(i_yapObject.getID());
				i_trans.i_stream.checkStillToSet();
			}
		}

		private bool validYapObject()
		{
			return (i_trans != null) && (i_yapObject != null) && (i_yapObject.getID() > 0);
		}
	}
}

namespace com.db4o.drs.db4o
{
	/// <summary>
	/// using YapObject's hc_tree functionality, only exposing the methods
	/// that are to be used in this class.
	/// </summary>
	/// <remarks>
	/// using YapObject's hc_tree functionality, only exposing the methods
	/// that are to be used in this class. Do not use superclass methods.
	/// <p/>
	/// Implementation details that are difficult to read:
	/// The hc_xxx variables are used for the sorted tree.
	/// The virtualAttributes is used to
	/// </remarks>
	public class Db4oReplicationReferenceImpl : com.db4o.YapObject, com.db4o.drs.inside.ReplicationReference
		, com.db4o.inside.replication.Db4oReplicationReference
	{
		private object _counterPart;

		private bool _markedForReplicating;

		private bool _markedForDeleting;

		internal Db4oReplicationReferenceImpl(com.db4o.ext.ObjectInfo objectInfo)
		{
			com.db4o.YapObject yo = (com.db4o.YapObject)objectInfo;
			com.db4o.Transaction trans = yo.GetTrans();
			com.db4o.VirtualAttributes va = yo.VirtualAttributes(trans);
			if (va != null)
			{
				SetVirtualAttributes((com.db4o.VirtualAttributes)va.ShallowClone());
			}
			else
			{
				SetVirtualAttributes(new com.db4o.VirtualAttributes());
			}
			object obj = yo.GetObject();
			SetObject(obj);
			Hc_init(obj);
		}

		public Db4oReplicationReferenceImpl(object myObject, com.db4o.ext.Db4oDatabase db
			, long longPart, long version)
		{
			SetObject(myObject);
			Hc_init(myObject);
			com.db4o.VirtualAttributes va = new com.db4o.VirtualAttributes();
			va.i_database = db;
			va.i_uuid = longPart;
			va.i_version = version;
			SetVirtualAttributes(va);
		}

		public virtual com.db4o.drs.db4o.Db4oReplicationReferenceImpl Add(com.db4o.drs.db4o.Db4oReplicationReferenceImpl
			 newNode)
		{
			return (com.db4o.drs.db4o.Db4oReplicationReferenceImpl)Hc_add(newNode);
		}

		public virtual com.db4o.drs.db4o.Db4oReplicationReferenceImpl Find(object obj)
		{
			return (com.db4o.drs.db4o.Db4oReplicationReferenceImpl)Hc_find(obj);
		}

		public virtual void Traverse(com.db4o.foundation.Visitor4 visitor)
		{
			Hc_traverse(visitor);
		}

		public virtual com.db4o.ext.Db4oUUID Uuid()
		{
			com.db4o.ext.Db4oDatabase db = SignaturePart();
			if (db == null)
			{
				return null;
			}
			return new com.db4o.ext.Db4oUUID(LongPart(), db.GetSignature());
		}

		public virtual long Version()
		{
			return VirtualAttributes().i_version;
		}

		public virtual object Object()
		{
			return GetObject();
		}

		public virtual object Counterpart()
		{
			return _counterPart;
		}

		public virtual void SetCounterpart(object obj)
		{
			_counterPart = obj;
		}

		public virtual void MarkForReplicating()
		{
			_markedForReplicating = true;
		}

		public virtual bool IsMarkedForReplicating()
		{
			return _markedForReplicating;
		}

		public virtual void MarkForDeleting()
		{
			_markedForDeleting = true;
		}

		public virtual bool IsMarkedForDeleting()
		{
			return _markedForDeleting;
		}

		public virtual void MarkCounterpartAsNew()
		{
			throw new System.NotSupportedException("TODO");
		}

		public virtual bool IsCounterpartNew()
		{
			throw new System.NotSupportedException("TODO");
		}

		public virtual com.db4o.ext.Db4oDatabase SignaturePart()
		{
			return VirtualAttributes().i_database;
		}

		public virtual long LongPart()
		{
			return VirtualAttributes().i_uuid;
		}

		private com.db4o.VirtualAttributes VirtualAttributes()
		{
			return VirtualAttributes(null);
		}

		public sealed override bool Equals(object o)
		{
			if (this == o)
			{
				return true;
			}
			if (o == null || o.GetType().BaseType != o.GetType().BaseType)
			{
				return false;
			}
			com.db4o.drs.inside.ReplicationReference that = (com.db4o.drs.inside.ReplicationReference
				)o;
			if (Version() != that.Version())
			{
				return false;
			}
			return Uuid().Equals(that.Uuid());
		}

		public sealed override int GetHashCode()
		{
			int result;
			result = Uuid().GetHashCode();
			result = 29 * result + (int)(Version() ^ ((Version()) >> (32 & 0x1f)));
			return result;
		}
	}
}

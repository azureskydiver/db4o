namespace com.db4o.drs.db4o
{
	public class Db4oReplicationProvider : com.db4o.drs.inside.TestableReplicationProvider
		, com.db4o.inside.replication.Db4oReplicationReferenceProvider, com.db4o.drs.inside.TestableReplicationProviderInside
	{
		private com.db4o.drs.inside.ReadonlyReplicationProviderSignature _mySignature;

		private readonly com.db4o.YapStream _stream;

		private readonly com.db4o.reflect.Reflector _reflector;

		private com.db4o.ReplicationRecord _replicationRecord;

		private com.db4o.drs.db4o.Db4oReplicationReferenceImpl _referencesByObject;

		private com.db4o.drs.db4o.Db4oSignatureMap _signatureMap;

		private com.db4o.foundation.Tree _idsReplicatedInThisSession;

		private readonly string _name;

		public Db4oReplicationProvider(com.db4o.ObjectContainer objectContainer) : this(objectContainer
			, "null")
		{
		}

		public Db4oReplicationProvider(com.db4o.ObjectContainer objectContainer, string name
			)
		{
			com.db4o.config.Configuration cfg = objectContainer.Ext().Configure();
			cfg.ObjectClass(typeof(object)).CascadeOnDelete(false);
			cfg.Callbacks(false);
			_name = name;
			_stream = (com.db4o.YapStream)objectContainer;
			_reflector = _stream.Reflector();
			_signatureMap = new com.db4o.drs.db4o.Db4oSignatureMap(_stream);
		}

		public virtual com.db4o.ObjectContainer ObjectContainer()
		{
			return _stream;
		}

		public virtual com.db4o.drs.inside.ReadonlyReplicationProviderSignature GetSignature
			()
		{
			if (_mySignature == null)
			{
				_mySignature = new com.db4o.drs.db4o.Db4oReplicationProviderSignature(_stream.Identity
					());
			}
			return _mySignature;
		}

		public virtual object GetMonitor()
		{
			return _stream.Lock();
		}

		public virtual void StartReplicationTransaction(com.db4o.drs.inside.ReadonlyReplicationProviderSignature
			 peerSignature)
		{
			ClearAllReferences();
			lock (GetMonitor())
			{
				com.db4o.Transaction trans = _stream.GetTransaction();
				com.db4o.ext.Db4oDatabase myIdentity = _stream.Identity();
				_signatureMap.Put(myIdentity);
				com.db4o.ext.Db4oDatabase otherIdentity = _signatureMap.Produce(peerSignature.GetSignature
					(), peerSignature.GetCreated());
				com.db4o.ext.Db4oDatabase younger = null;
				com.db4o.ext.Db4oDatabase older = null;
				if (myIdentity.IsOlderThan(otherIdentity))
				{
					younger = otherIdentity;
					older = myIdentity;
				}
				else
				{
					younger = myIdentity;
					older = otherIdentity;
				}
				_replicationRecord = com.db4o.ReplicationRecord.QueryForReplicationRecord(_stream
					, younger, older);
				if (_replicationRecord == null)
				{
					_replicationRecord = new com.db4o.ReplicationRecord(younger, older);
					_replicationRecord.Store(_stream);
				}
				long localInitialVersion = _stream.Version();
			}
		}

		public virtual void SyncVersionWithPeer(long version)
		{
			long versionTest = GetCurrentVersion();
			_replicationRecord._version = version;
			_replicationRecord.Store(_stream);
		}

		public virtual void CommitReplicationTransaction(long raisedDatabaseVersion)
		{
			long versionTest = GetCurrentVersion();
			_stream.RaiseVersion(raisedDatabaseVersion);
			_stream.Commit();
			_idsReplicatedInThisSession = null;
		}

		public virtual void RollbackReplication()
		{
			_stream.Rollback();
			_referencesByObject = null;
			_idsReplicatedInThisSession = null;
		}

		public virtual long GetCurrentVersion()
		{
			return _stream.Version();
		}

		public virtual long GetLastReplicationVersion()
		{
			return _replicationRecord._version;
		}

		public virtual void StoreReplica(object obj)
		{
			lock (GetMonitor())
			{
				_stream.SetByNewReplication(this, obj);
				com.db4o.TreeInt node = new com.db4o.TreeInt((int)_stream.GetID(obj));
				if (_idsReplicatedInThisSession == null)
				{
					_idsReplicatedInThisSession = node;
				}
				else
				{
					_idsReplicatedInThisSession = _idsReplicatedInThisSession.Add(node);
				}
			}
		}

		public virtual void Activate(object obj)
		{
			if (obj == null)
			{
				return;
			}
			com.db4o.reflect.ReflectClass claxx = _reflector.ForObject(obj);
			int level = claxx.IsCollection() ? 3 : 1;
			_stream.Activate(obj, level);
		}

		public virtual com.db4o.inside.replication.Db4oReplicationReference ReferenceFor(
			object obj)
		{
			if (_referencesByObject == null)
			{
				return null;
			}
			return _referencesByObject.Find(obj);
		}

		public virtual com.db4o.drs.inside.ReplicationReference ProduceReference(object obj
			, object unused, string unused2)
		{
			if (obj == null)
			{
				return null;
			}
			if (_referencesByObject != null)
			{
				com.db4o.drs.db4o.Db4oReplicationReferenceImpl existingNode = _referencesByObject
					.Find(obj);
				if (existingNode != null)
				{
					return existingNode;
				}
			}
			Refresh(obj);
			com.db4o.ext.ObjectInfo objectInfo = _stream.GetObjectInfo(obj);
			if (objectInfo == null)
			{
				return null;
			}
			com.db4o.ext.Db4oUUID uuid = objectInfo.GetUUID();
			if (uuid == null)
			{
				throw new System.ArgumentNullException();
			}
			com.db4o.drs.db4o.Db4oReplicationReferenceImpl newNode = new com.db4o.drs.db4o.Db4oReplicationReferenceImpl
				(objectInfo);
			AddReference(newNode);
			return newNode;
		}

		private void Refresh(object obj)
		{
			if (_stream is com.db4o.YapClient)
			{
				_stream.Refresh(obj, 1);
			}
		}

		private void AddReference(com.db4o.drs.db4o.Db4oReplicationReferenceImpl newNode)
		{
			if (_referencesByObject == null)
			{
				_referencesByObject = newNode;
			}
			else
			{
				_referencesByObject = _referencesByObject.Add(newNode);
			}
		}

		public virtual com.db4o.drs.inside.ReplicationReference ReferenceNewObject(object
			 obj, com.db4o.drs.inside.ReplicationReference counterpartReference, com.db4o.drs.inside.ReplicationReference
			 referencingObjCounterPartRef, string fieldName)
		{
			com.db4o.ext.Db4oUUID uuid = counterpartReference.Uuid();
			if (uuid == null)
			{
				return null;
			}
			byte[] signature = uuid.GetSignaturePart();
			long longPart = uuid.GetLongPart();
			long version = counterpartReference.Version();
			com.db4o.ext.Db4oDatabase db = _signatureMap.Produce(signature, 0);
			com.db4o.drs.db4o.Db4oReplicationReferenceImpl @ref = new com.db4o.drs.db4o.Db4oReplicationReferenceImpl
				(obj, db, longPart, version);
			AddReference(@ref);
			return @ref;
		}

		public virtual com.db4o.drs.inside.ReplicationReference ProduceReferenceByUUID(com.db4o.ext.Db4oUUID
			 uuid, System.Type hint)
		{
			if (uuid == null)
			{
				return null;
			}
			object obj = _stream.GetByUUID(uuid);
			if (obj == null)
			{
				return null;
			}
			if (!_stream.IsActive(obj))
			{
				_stream.Activate(obj, 1);
			}
			return ProduceReference(obj, null, null);
		}

		public virtual void VisitCachedReferences(com.db4o.foundation.Visitor4 visitor)
		{
			if (_referencesByObject != null)
			{
				_referencesByObject.Traverse(new _AnonymousInnerClass261(this, visitor));
			}
		}

		private sealed class _AnonymousInnerClass261 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass261(Db4oReplicationProvider _enclosing, com.db4o.foundation.Visitor4
				 visitor)
			{
				this._enclosing = _enclosing;
				this.visitor = visitor;
			}

			public void Visit(object obj)
			{
				com.db4o.drs.db4o.Db4oReplicationReferenceImpl node = (com.db4o.drs.db4o.Db4oReplicationReferenceImpl
					)obj;
				visitor.Visit(node);
			}

			private readonly Db4oReplicationProvider _enclosing;

			private readonly com.db4o.foundation.Visitor4 visitor;
		}

		public virtual void ClearAllReferences()
		{
			_referencesByObject = null;
		}

		public virtual com.db4o.ObjectSet ObjectsChangedSinceLastReplication()
		{
			com.db4o.query.Query q = _stream.Query();
			WhereModified(q);
			return q.Execute();
		}

		public virtual com.db4o.ObjectSet ObjectsChangedSinceLastReplication(System.Type 
			clazz)
		{
			com.db4o.query.Query q = _stream.Query();
			q.Constrain(clazz);
			WhereModified(q);
			return q.Execute();
		}

		/// <summary>
		/// adds a constraint to the passed Query to query only for objects that
		/// were modified since the last replication process between this and the
		/// other ObjectContainer involved in the current replication process.
		/// </summary>
		/// <remarks>
		/// adds a constraint to the passed Query to query only for objects that
		/// were modified since the last replication process between this and the
		/// other ObjectContainer involved in the current replication process.
		/// </remarks>
		/// <param name="query">the Query to be constrained</param>
		public virtual void WhereModified(com.db4o.query.Query query)
		{
			query.Descend(com.db4o.ext.VirtualField.VERSION).Constrain(GetLastReplicationVersion
				()).Greater();
		}

		public virtual com.db4o.ObjectSet GetStoredObjects(System.Type type)
		{
			com.db4o.query.Query query = _stream.Query();
			query.Constrain(type);
			return query.Execute();
		}

		public virtual void StoreNew(object o)
		{
			_stream.Set(o);
		}

		public virtual void Update(object o)
		{
			_stream.Set(o);
		}

		public virtual string GetName()
		{
			return _name;
		}

		public virtual void UpdateCounterpart(object updated)
		{
			throw new j4o.lang.RuntimeException("TODO");
		}

		public virtual void Destroy()
		{
		}

		public virtual void Commit()
		{
			_stream.Commit();
		}

		public virtual void DeleteAllInstances(System.Type clazz)
		{
			com.db4o.query.Query q = _stream.Query();
			q.Constrain(clazz);
			com.db4o.ObjectSet objectSet = q.Execute();
			while (objectSet.HasNext())
			{
				Delete(objectSet.Next());
			}
		}

		public virtual void Delete(object obj)
		{
			_stream.Delete(obj);
		}

		public virtual bool WasModifiedSinceLastReplication(com.db4o.drs.inside.ReplicationReference
			 reference)
		{
			if (_idsReplicatedInThisSession != null)
			{
				int id = (int)_stream.GetID(reference.Object());
				if (_idsReplicatedInThisSession.Find(new com.db4o.TreeInt(id)) != null)
				{
					return false;
				}
			}
			return reference.Version() > GetLastReplicationVersion();
		}

		public virtual bool SupportsMultiDimensionalArrays()
		{
			return true;
		}

		public virtual bool SupportsHybridCollection()
		{
			return true;
		}

		public virtual bool SupportsRollback()
		{
			return false;
		}

		public virtual bool SupportsCascadeDelete()
		{
			return true;
		}

		public override string ToString()
		{
			return GetName();
		}

		public virtual void ReplicateDeletion(com.db4o.ext.Db4oUUID uuid)
		{
			object obj = _stream.GetByUUID(uuid);
			if (obj == null)
			{
				return;
			}
			_stream.Delete(obj);
		}

		public virtual com.db4o.ext.ExtObjectContainer GetObjectContainer()
		{
			return _stream;
		}
	}
}

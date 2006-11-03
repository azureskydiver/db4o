namespace com.db4o
{
	/// <exclude></exclude>
	public class ReplicationImpl : com.db4o.replication.ReplicationProcess
	{
		internal readonly com.db4o.YapStream _peerA;

		internal readonly com.db4o.Transaction _transA;

		internal readonly com.db4o.YapStream _peerB;

		internal readonly com.db4o.Transaction _transB;

		internal readonly com.db4o.replication.ReplicationConflictHandler _conflictHandler;

		internal readonly com.db4o.ReplicationRecord _record;

		private int _direction;

		private const int IGNORE = 0;

		private const int TO_B = -1;

		private const int TO_A = 1;

		private const int CHECK_CONFLICT = -99;

		public ReplicationImpl(com.db4o.YapStream peerA, com.db4o.ObjectContainer peerB, 
			com.db4o.replication.ReplicationConflictHandler conflictHandler)
		{
			if (conflictHandler == null)
			{
				throw new System.ArgumentNullException();
			}
			lock (peerA.Ext().Lock())
			{
				lock (peerB.Ext().Lock())
				{
					_peerA = peerA;
					_transA = peerA.CheckTransaction(null);
					_peerB = (com.db4o.YapStream)peerB;
					_transB = _peerB.CheckTransaction(null);
					com.db4o.inside.replication.MigrationConnection mgc = new com.db4o.inside.replication.MigrationConnection
						(_peerA, _peerB);
					_peerA.i_handlers.i_migration = mgc;
					_peerA.i_handlers.i_replication = this;
					_peerA._replicationCallState = com.db4o.YapConst.OLD;
					_peerB.i_handlers.i_migration = mgc;
					_peerB.i_handlers.i_replication = this;
					_peerB._replicationCallState = com.db4o.YapConst.OLD;
					_conflictHandler = conflictHandler;
					_record = com.db4o.ReplicationRecord.BeginReplication(_transA, _transB);
				}
			}
		}

		private int BindAndSet(com.db4o.Transaction trans, com.db4o.YapStream peer, com.db4o.YapObject
			 @ref, object sourceObject)
		{
			if (sourceObject is com.db4o.Db4oTypeImpl)
			{
				com.db4o.Db4oTypeImpl db4oType = (com.db4o.Db4oTypeImpl)sourceObject;
				if (!db4oType.CanBind())
				{
					com.db4o.Db4oTypeImpl targetObject = (com.db4o.Db4oTypeImpl)@ref.GetObject();
					targetObject.ReplicateFrom(sourceObject);
					return @ref.GetID();
				}
			}
			peer.Bind2(@ref, sourceObject);
			return peer.SetAfterReplication(trans, sourceObject, 1, true);
		}

		public virtual void CheckConflict(object obj)
		{
			int temp = _direction;
			_direction = CHECK_CONFLICT;
			Replicate(obj);
			_direction = temp;
		}

		public virtual void Commit()
		{
			lock (_peerA.Lock())
			{
				lock (_peerB.Lock())
				{
					_peerA.Commit();
					_peerB.Commit();
					EndReplication();
					long versionA = _peerA.CurrentVersion();
					long versionB = _peerB.CurrentVersion();
					_record._version = (versionA > versionB) ? versionA : versionB;
					_peerA.RaiseVersion(_record._version + 1);
					_peerB.RaiseVersion(_record._version + 1);
					_record.Store(_peerA);
					_record.Store(_peerB);
				}
			}
		}

		private void EndReplication()
		{
			_peerA._replicationCallState = com.db4o.YapConst.NONE;
			_peerA.i_handlers.i_migration = null;
			_peerA.i_handlers.i_replication = null;
			_peerA._replicationCallState = com.db4o.YapConst.NONE;
			_peerB.i_handlers.i_migration = null;
			_peerB.i_handlers.i_replication = null;
		}

		private int IdInCaller(com.db4o.YapStream caller, com.db4o.YapObject referenceA, 
			com.db4o.YapObject referenceB)
		{
			return (caller == _peerA) ? referenceA.GetID() : referenceB.GetID();
		}

		private int IgnoreOrCheckConflict()
		{
			if (_direction == CHECK_CONFLICT)
			{
				return CHECK_CONFLICT;
			}
			return IGNORE;
		}

		private bool IsInConflict(long versionA, long versionB)
		{
			if (versionA > _record._version && versionB > _record._version)
			{
				return true;
			}
			if (versionB > _record._version && _direction == TO_B)
			{
				return true;
			}
			if (versionA > _record._version && _direction == TO_A)
			{
				return true;
			}
			return false;
		}

		private long LastSynchronization()
		{
			return _record._version;
		}

		public virtual com.db4o.ObjectContainer PeerA()
		{
			return _peerA;
		}

		public virtual com.db4o.ObjectContainer PeerB()
		{
			return _peerB;
		}

		public virtual void Replicate(object obj)
		{
			com.db4o.YapStream stream = _peerB;
			if (_peerB.IsStored(obj))
			{
				if (!_peerA.IsStored(obj))
				{
					stream = _peerA;
				}
			}
			stream.Set(obj);
		}

		public virtual void Rollback()
		{
			_peerA.Rollback();
			_peerB.Rollback();
			EndReplication();
		}

		public virtual void SetDirection(com.db4o.ObjectContainer replicateFrom, com.db4o.ObjectContainer
			 replicateTo)
		{
			if (replicateFrom == _peerA && replicateTo == _peerB)
			{
				_direction = TO_B;
			}
			if (replicateFrom == _peerB && replicateTo == _peerA)
			{
				_direction = TO_A;
			}
		}

		private void ShareBinding(com.db4o.YapObject sourceReference, com.db4o.YapObject 
			referenceA, object objectA, com.db4o.YapObject referenceB, object objectB)
		{
			if (sourceReference == null)
			{
				return;
			}
			if (objectA is com.db4o.Db4oTypeImpl)
			{
				if (!((com.db4o.Db4oTypeImpl)objectA).CanBind())
				{
					return;
				}
			}
			if (sourceReference == referenceA)
			{
				_peerB.Bind2(referenceB, objectA);
			}
			else
			{
				_peerA.Bind2(referenceA, objectB);
			}
		}

		private int ToA()
		{
			if (_direction == CHECK_CONFLICT)
			{
				return CHECK_CONFLICT;
			}
			if (_direction != TO_B)
			{
				return TO_A;
			}
			return IGNORE;
		}

		private int ToB()
		{
			if (_direction == CHECK_CONFLICT)
			{
				return CHECK_CONFLICT;
			}
			if (_direction != TO_A)
			{
				return TO_B;
			}
			return IGNORE;
		}

		/// <summary>called by YapStream.set()</summary>
		/// <returns>
		/// id of reference in caller or 0 if not handled or -1
		/// if #set() should stop processing because of a direction
		/// setting.
		/// </returns>
		internal virtual int TryToHandle(com.db4o.YapStream caller, object obj)
		{
			int notProcessed = 0;
			com.db4o.YapStream other = null;
			com.db4o.YapObject sourceReference = null;
			if (caller == _peerA)
			{
				other = _peerB;
				if (_direction == TO_B)
				{
					notProcessed = -1;
				}
			}
			else
			{
				other = _peerA;
				if (_direction == TO_A)
				{
					notProcessed = -1;
				}
			}
			lock (other.i_lock)
			{
				object objectA = obj;
				object objectB = obj;
				com.db4o.YapObject referenceA = _peerA.GetYapObject(obj);
				com.db4o.YapObject referenceB = _peerB.GetYapObject(obj);
				com.db4o.VirtualAttributes attA = null;
				com.db4o.VirtualAttributes attB = null;
				if (referenceA == null)
				{
					if (referenceB == null)
					{
						return notProcessed;
					}
					sourceReference = referenceB;
					attB = referenceB.VirtualAttributes(_transB);
					if (attB == null)
					{
						return notProcessed;
					}
					object[] arr = _transA.ObjectAndYapObjectBySignature(attB.i_uuid, attB.i_database
						.i_signature);
					if (arr[0] == null)
					{
						return notProcessed;
					}
					referenceA = (com.db4o.YapObject)arr[1];
					objectA = arr[0];
					attA = referenceA.VirtualAttributes(_transA);
				}
				else
				{
					attA = referenceA.VirtualAttributes(_transA);
					if (attA == null)
					{
						return notProcessed;
					}
					if (referenceB == null)
					{
						sourceReference = referenceA;
						object[] arr = _transB.ObjectAndYapObjectBySignature(attA.i_uuid, attA.i_database
							.i_signature);
						if (arr[0] == null)
						{
							return notProcessed;
						}
						referenceB = (com.db4o.YapObject)arr[1];
						objectB = arr[0];
					}
					attB = referenceB.VirtualAttributes(_transB);
				}
				if (attA == null || attB == null)
				{
					return notProcessed;
				}
				if (objectA == objectB)
				{
					if (caller == _peerA && _direction == TO_B)
					{
						return -1;
					}
					if (caller == _peerB && _direction == TO_A)
					{
						return -1;
					}
					return IdInCaller(caller, referenceA, referenceB);
				}
				_peerA.Refresh(objectA, 1);
				_peerB.Refresh(objectB, 1);
				if (attA.i_version <= _record._version && attB.i_version <= _record._version)
				{
					if (_direction != CHECK_CONFLICT)
					{
						ShareBinding(sourceReference, referenceA, objectA, referenceB, objectB);
					}
					return IdInCaller(caller, referenceA, referenceB);
				}
				int direction = IgnoreOrCheckConflict();
				if (IsInConflict(attA.i_version, attB.i_version))
				{
					object prevailing = _conflictHandler.ResolveConflict(this, objectA, objectB);
					if (prevailing == objectA)
					{
						direction = (_direction == TO_A) ? IGNORE : ToB();
					}
					if (prevailing == objectB)
					{
						direction = (_direction == TO_B) ? IGNORE : ToA();
					}
					if (direction == IGNORE)
					{
						return -1;
					}
				}
				else
				{
					direction = attB.i_version > _record._version ? ToA() : ToB();
				}
				if (direction == TO_A)
				{
					if (!referenceB.IsActive())
					{
						referenceB.Activate(_transB, objectB, 1, false);
					}
					int idA = BindAndSet(_transA, _peerA, referenceA, objectB);
					if (caller == _peerA)
					{
						return idA;
					}
				}
				if (direction == TO_B)
				{
					if (!referenceA.IsActive())
					{
						referenceA.Activate(_transA, objectA, 1, false);
					}
					int idB = BindAndSet(_transB, _peerB, referenceB, objectA);
					if (caller == _peerB)
					{
						return idB;
					}
				}
				return IdInCaller(caller, referenceA, referenceB);
			}
		}

		public virtual void WhereModified(com.db4o.query.Query query)
		{
			query.Descend(com.db4o.ext.VirtualField.VERSION).Constrain(LastSynchronization())
				.Greater();
		}
	}
}

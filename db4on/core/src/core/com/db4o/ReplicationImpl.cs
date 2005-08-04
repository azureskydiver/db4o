namespace com.db4o
{
	internal class ReplicationImpl : com.db4o.replication.ReplicationProcess
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

		internal ReplicationImpl(com.db4o.YapStream peerA, com.db4o.ObjectContainer peerB
			, com.db4o.replication.ReplicationConflictHandler conflictHandler)
		{
			if (conflictHandler == null)
			{
				throw new System.ArgumentNullException();
			}
			lock (peerA.ext().Lock())
			{
				lock (peerB.ext().Lock())
				{
					_peerA = peerA;
					_transA = peerA.checkTransaction(null);
					_peerB = (com.db4o.YapStream)peerB;
					_transB = _peerB.checkTransaction(null);
					com.db4o.MigrationConnection mgc = new com.db4o.MigrationConnection();
					_peerA.i_handlers.i_migration = mgc;
					_peerA.i_handlers.i_replication = this;
					_peerA.i_migrateFrom = _peerB;
					_peerB.i_handlers.i_migration = mgc;
					_peerB.i_handlers.i_replication = this;
					_peerB.i_migrateFrom = _peerA;
					_conflictHandler = conflictHandler;
					_record = com.db4o.ReplicationRecord.beginReplication(_transA, _transB);
				}
			}
		}

		private int bindAndSet(com.db4o.Transaction trans, com.db4o.YapStream peer, com.db4o.YapObject
			 _ref, object sourceObject)
		{
			if (sourceObject is com.db4o.Db4oTypeImpl)
			{
				com.db4o.Db4oTypeImpl db4oType = (com.db4o.Db4oTypeImpl)sourceObject;
				if (!db4oType.canBind())
				{
					com.db4o.Db4oTypeImpl targetObject = (com.db4o.Db4oTypeImpl)_ref.getObject();
					targetObject.replicateFrom(sourceObject);
					return _ref.getID();
				}
			}
			peer.bind2(_ref, sourceObject);
			return peer.setAfterReplication(trans, sourceObject, 1, true);
		}

		public virtual void checkConflict(object obj)
		{
			int temp = _direction;
			_direction = CHECK_CONFLICT;
			replicate(obj);
			_direction = temp;
		}

		public virtual void commit()
		{
			lock (_peerA.Lock())
			{
				lock (_peerB.Lock())
				{
					_peerA.commit();
					_peerB.commit();
					endReplication();
					long versionA = _peerA.currentVersion() - 1;
					long versionB = _peerB.currentVersion() - 1;
					_record._version = versionB;
					if (versionA > versionB)
					{
						_record._version = versionA;
						_peerB.raiseVersion(_record._version);
					}
					else
					{
						if (versionB > versionA)
						{
							_peerA.raiseVersion(_record._version);
						}
					}
					_record.store(_peerA);
					_record.store(_peerB);
				}
			}
		}

		private void endReplication()
		{
			_peerA.i_migrateFrom = null;
			_peerA.i_handlers.i_migration = null;
			_peerA.i_handlers.i_replication = null;
			_peerB.i_migrateFrom = null;
			_peerB.i_handlers.i_migration = null;
			_peerB.i_handlers.i_replication = null;
		}

		private int idInCaller(com.db4o.YapStream caller, com.db4o.YapObject referenceA, 
			com.db4o.YapObject referenceB)
		{
			return (caller == _peerA) ? referenceA.getID() : referenceB.getID();
		}

		private int ignoreOrCheckConflict()
		{
			if (_direction == CHECK_CONFLICT)
			{
				return CHECK_CONFLICT;
			}
			return IGNORE;
		}

		private bool isInConflict(long versionA, long versionB)
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

		private long lastSynchronization()
		{
			return _record._version;
		}

		public virtual com.db4o.ObjectContainer peerA()
		{
			return _peerA;
		}

		public virtual com.db4o.ObjectContainer peerB()
		{
			return _peerB;
		}

		public virtual void replicate(object obj)
		{
			com.db4o.YapStream stream = _peerB;
			if (_peerB.isStored(obj))
			{
				if (!_peerA.isStored(obj))
				{
					stream = _peerA;
				}
			}
			stream.set(obj);
		}

		public virtual void rollback()
		{
			_peerA.rollback();
			_peerB.rollback();
			endReplication();
		}

		public virtual void setDirection(com.db4o.ObjectContainer replicateFrom, com.db4o.ObjectContainer
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

		private void shareBinding(com.db4o.YapObject sourceReference, com.db4o.YapObject 
			referenceA, object objectA, com.db4o.YapObject referenceB, object objectB)
		{
			if (sourceReference == null)
			{
				return;
			}
			if (objectA is com.db4o.Db4oTypeImpl)
			{
				if (!((com.db4o.Db4oTypeImpl)objectA).canBind())
				{
					return;
				}
			}
			if (sourceReference == referenceA)
			{
				_peerB.bind2(referenceB, objectA);
			}
			else
			{
				_peerA.bind2(referenceA, objectB);
			}
		}

		private int toA()
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

		private int toB()
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
		internal virtual int tryToHandle(com.db4o.YapStream caller, object obj)
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
				com.db4o.YapObject referenceA = _peerA.getYapObject(obj);
				com.db4o.YapObject referenceB = _peerB.getYapObject(obj);
				com.db4o.VirtualAttributes attA = null;
				com.db4o.VirtualAttributes attB = null;
				if (referenceA == null)
				{
					if (referenceB == null)
					{
						return notProcessed;
					}
					sourceReference = referenceB;
					attB = referenceB.virtualAttributes(_transB);
					if (attB == null)
					{
						return notProcessed;
					}
					object[] arr = _transA.objectAndYapObjectBySignature(attB.i_uuid, attB.i_database
						.i_signature);
					if (arr[0] == null)
					{
						return notProcessed;
					}
					referenceA = (com.db4o.YapObject)arr[1];
					objectA = arr[0];
					attA = referenceA.virtualAttributes(_transA);
				}
				else
				{
					attA = referenceA.virtualAttributes(_transA);
					if (attA == null)
					{
						return notProcessed;
					}
					if (referenceB == null)
					{
						sourceReference = referenceA;
						object[] arr = _transB.objectAndYapObjectBySignature(attA.i_uuid, attA.i_database
							.i_signature);
						if (arr[0] == null)
						{
							return notProcessed;
						}
						referenceB = (com.db4o.YapObject)arr[1];
						objectB = arr[0];
					}
					else
					{
						sourceReference = null;
					}
					attB = referenceB.virtualAttributes(_transB);
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
					return idInCaller(caller, referenceA, referenceB);
				}
				_peerA.refresh(objectA, 1);
				_peerB.refresh(objectB, 1);
				if (attA.i_version <= _record._version && attB.i_version <= _record._version)
				{
					if (_direction != CHECK_CONFLICT)
					{
						shareBinding(sourceReference, referenceA, objectA, referenceB, objectB);
					}
					return idInCaller(caller, referenceA, referenceB);
				}
				int direction = ignoreOrCheckConflict();
				if (isInConflict(attA.i_version, attB.i_version))
				{
					object prevailing = _conflictHandler.resolveConflict(this, objectA, objectB);
					if (prevailing == objectA)
					{
						direction = (_direction == TO_A) ? IGNORE : toB();
					}
					if (prevailing == objectB)
					{
						direction = (_direction == TO_B) ? IGNORE : toA();
					}
					if (direction == IGNORE)
					{
						return -1;
					}
				}
				else
				{
					direction = attB.i_version > _record._version ? toA() : toB();
				}
				if (direction == TO_A)
				{
					if (!referenceB.isActive())
					{
						referenceB.activate(_transB, objectB, 1, false);
					}
					int idA = bindAndSet(_transA, _peerA, referenceA, objectB);
					if (caller == _peerA)
					{
						return idA;
					}
				}
				if (direction == TO_B)
				{
					if (!referenceA.isActive())
					{
						referenceA.activate(_transA, objectA, 1, false);
					}
					int idB = bindAndSet(_transB, _peerB, referenceB, objectA);
					if (caller == _peerB)
					{
						return idB;
					}
				}
				return idInCaller(caller, referenceA, referenceB);
			}
		}

		public virtual void whereModified(com.db4o.query.Query query)
		{
			query.descend(com.db4o.ext.VirtualField.VERSION).constrain(lastSynchronization())
				.greater();
		}
	}
}

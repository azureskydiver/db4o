namespace com.db4o.drs.inside
{
	internal class InstanceReplicationPreparer : com.db4o.drs.inside.traversal.Visitor
	{
		private readonly com.db4o.drs.inside.ReplicationProviderInside _providerA;

		private readonly com.db4o.drs.inside.ReplicationProviderInside _providerB;

		private readonly com.db4o.drs.ReplicationProvider _directionTo;

		private readonly com.db4o.drs.ReplicationEventListener _listener;

		private readonly bool _isReplicatingOnlyDeletions;

		private readonly long _lastReplicationVersion;

		private readonly com.db4o.foundation.Hashtable4 _uuidsProcessedInSession;

		private readonly com.db4o.drs.inside.traversal.Traverser _traverser;

		private readonly com.db4o.drs.inside.ReplicationReflector _reflector;

		private readonly com.db4o.drs.inside.CollectionHandler _collectionHandler;

		/// <summary>
		/// Purpose: handle circular references
		/// TODO Big Refactoring: Evolve this to handle ALL reference logic (!) and remove it from the providers.
		/// </summary>
		/// <remarks>
		/// Purpose: handle circular references
		/// TODO Big Refactoring: Evolve this to handle ALL reference logic (!) and remove it from the providers.
		/// </remarks>
		private readonly com.db4o.foundation.Hashtable4 _objectsPreparedToReplicate = new 
			com.db4o.foundation.Hashtable4(10000);

		/// <summary>
		/// key = object originated from one provider
		/// value = the counterpart ReplicationReference of the original object
		/// </summary>
		private com.db4o.foundation.Hashtable4 _counterpartRefsByOriginal = new com.db4o.foundation.Hashtable4
			(10000);

		private readonly com.db4o.drs.inside.ReplicationEventImpl _event;

		private readonly com.db4o.drs.inside.ObjectStateImpl _stateInA;

		private readonly com.db4o.drs.inside.ObjectStateImpl _stateInB;

		private object _obj;

		private object _referencingObject;

		private string _fieldName;

		internal InstanceReplicationPreparer(com.db4o.drs.inside.ReplicationProviderInside
			 providerA, com.db4o.drs.inside.ReplicationProviderInside providerB, com.db4o.drs.ReplicationProvider
			 directionTo, com.db4o.drs.ReplicationEventListener listener, bool isReplicatingOnlyDeletions
			, long lastReplicationVersion, com.db4o.foundation.Hashtable4 uuidsProcessedInSession
			, com.db4o.drs.inside.traversal.Traverser traverser, com.db4o.drs.inside.ReplicationReflector
			 reflector, com.db4o.drs.inside.CollectionHandler collectionHandler)
		{
			_event = new com.db4o.drs.inside.ReplicationEventImpl();
			_stateInA = _event._stateInProviderA;
			_stateInB = _event._stateInProviderB;
			_providerA = providerA;
			_providerB = providerB;
			_directionTo = directionTo;
			_listener = listener;
			_isReplicatingOnlyDeletions = isReplicatingOnlyDeletions;
			_lastReplicationVersion = lastReplicationVersion;
			_uuidsProcessedInSession = uuidsProcessedInSession;
			_traverser = traverser;
			_reflector = reflector;
			_collectionHandler = collectionHandler;
		}

		public bool Visit(object obj)
		{
			if (_objectsPreparedToReplicate.Get(obj) != null)
			{
				return false;
			}
			_objectsPreparedToReplicate.Put(obj, obj);
			return PrepareObjectToBeReplicated(obj, null, null);
		}

		private bool PrepareObjectToBeReplicated(object obj, object referencingObject, string
			 fieldName)
		{
			_obj = obj;
			_referencingObject = referencingObject;
			_fieldName = fieldName;
			com.db4o.drs.inside.ReplicationReference refA = _providerA.ProduceReference(_obj, 
				_referencingObject, _fieldName);
			com.db4o.drs.inside.ReplicationReference refB = _providerB.ProduceReference(_obj, 
				_referencingObject, _fieldName);
			if (refA == null && refB == null)
			{
				throw new j4o.lang.RuntimeException("" + _obj.GetType() + " " + _obj + " must be stored in one of the databases being replicated."
					);
			}
			if (refA != null && refB != null)
			{
				throw new j4o.lang.RuntimeException("" + _obj.GetType() + " " + _obj + " cannot be referenced by both databases being replicated."
					);
			}
			com.db4o.drs.inside.ReplicationProviderInside owner = refA == null ? _providerB : 
				_providerA;
			com.db4o.drs.inside.ReplicationReference ownerRef = refA == null ? refB : refA;
			com.db4o.drs.inside.ReplicationProviderInside other = Other(owner);
			com.db4o.ext.Db4oUUID uuid = ownerRef.Uuid();
			com.db4o.drs.inside.ReplicationReference otherRef = other.ProduceReferenceByUUID(
				uuid, _obj.GetType());
			if (refA == null)
			{
				refA = otherRef;
			}
			else
			{
				refB = otherRef;
			}
			if (otherRef == null)
			{
				if (WasProcessed(uuid))
				{
					return false;
				}
				MarkAsProcessed(uuid);
				long creationTime = ownerRef.Uuid().GetLongPart();
				if (creationTime > _lastReplicationVersion)
				{
					if (_isReplicatingOnlyDeletions)
					{
						return false;
					}
					return HandleNewObject(_obj, ownerRef, owner, other, _referencingObject, _fieldName
						, true, false);
				}
				else
				{
					return HandleMissingObjectInOther(_obj, ownerRef, owner, other, _referencingObject
						, _fieldName);
				}
			}
			if (_isReplicatingOnlyDeletions)
			{
				return false;
			}
			ownerRef.SetCounterpart(otherRef.Object());
			if (WasProcessed(uuid))
			{
				return false;
			}
			MarkAsProcessed(uuid);
			object objectA = refA.Object();
			object objectB = refB.Object();
			bool changedInA = _providerA.WasModifiedSinceLastReplication(refA);
			bool changedInB = _providerB.WasModifiedSinceLastReplication(refB);
			if (!changedInA && !changedInB)
			{
				return false;
			}
			bool conflict = false;
			if (changedInA && changedInB)
			{
				conflict = true;
			}
			if (changedInA && _directionTo == _providerA)
			{
				conflict = true;
			}
			if (changedInB && _directionTo == _providerB)
			{
				conflict = true;
			}
			object prevailing = _obj;
			_providerA.Activate(objectA);
			_providerB.Activate(objectB);
			_event.ResetAction();
			_event._isConflict = conflict;
			_event._creationDate = com.db4o.foundation.TimeStampIdGenerator.IdToMilliseconds(
				uuid.GetLongPart());
			_stateInA.SetAll(objectA, false, changedInA, com.db4o.foundation.TimeStampIdGenerator
				.IdToMilliseconds(ownerRef.Version()));
			_stateInB.SetAll(objectB, false, changedInB, com.db4o.foundation.TimeStampIdGenerator
				.IdToMilliseconds(otherRef.Version()));
			_listener.OnReplicate(_event);
			if (conflict)
			{
				if (!_event._actionWasChosen)
				{
					ThrowReplicationConflictException();
				}
				if (_event._actionChosenState == null)
				{
					return false;
				}
				if (_event._actionChosenState == _stateInA)
				{
					prevailing = objectA;
				}
				if (_event._actionChosenState == _stateInB)
				{
					prevailing = objectB;
				}
			}
			else
			{
				if (_event._actionWasChosen)
				{
					if (_event._actionChosenState == _stateInA)
					{
						prevailing = objectA;
					}
					if (_event._actionChosenState == _stateInB)
					{
						prevailing = objectB;
					}
					if (_event._actionChosenState == null)
					{
						return false;
					}
				}
				else
				{
					if (changedInA)
					{
						prevailing = objectA;
					}
					if (changedInB)
					{
						prevailing = objectB;
					}
				}
			}
			com.db4o.drs.inside.ReplicationProviderInside prevailingPeer = prevailing == objectA
				 ? _providerA : _providerB;
			if (_directionTo == prevailingPeer)
			{
				return false;
			}
			if (!conflict)
			{
				prevailingPeer.Activate(prevailing);
			}
			if (prevailing != _obj)
			{
				otherRef.SetCounterpart(_obj);
				otherRef.MarkForReplicating();
				MarkAsNotProcessed(uuid);
				_traverser.ExtendTraversalTo(prevailing);
			}
			else
			{
				ownerRef.MarkForReplicating();
			}
			return !_event._actionShouldStopTraversal;
		}

		private void MarkAsNotProcessed(com.db4o.ext.Db4oUUID uuid)
		{
			_uuidsProcessedInSession.Remove(uuid);
		}

		private void MarkAsProcessed(com.db4o.ext.Db4oUUID uuid)
		{
			if (_uuidsProcessedInSession.Get(uuid) != null)
			{
				throw new j4o.lang.RuntimeException("illegal state");
			}
			_uuidsProcessedInSession.Put(uuid, uuid);
		}

		private bool WasProcessed(com.db4o.ext.Db4oUUID uuid)
		{
			return _uuidsProcessedInSession.Get(uuid) != null;
		}

		private com.db4o.drs.inside.ReplicationProviderInside Other(com.db4o.drs.inside.ReplicationProviderInside
			 peer)
		{
			return peer == _providerA ? _providerB : _providerA;
		}

		private bool HandleMissingObjectInOther(object obj, com.db4o.drs.inside.ReplicationReference
			 ownerRef, com.db4o.drs.inside.ReplicationProviderInside owner, com.db4o.drs.inside.ReplicationProviderInside
			 other, object referencingObject, string fieldName)
		{
			bool isConflict = false;
			bool wasModified = owner.WasModifiedSinceLastReplication(ownerRef);
			if (wasModified)
			{
				isConflict = true;
			}
			if (_directionTo == other)
			{
				isConflict = true;
			}
			object prevailing = null;
			if (isConflict)
			{
				owner.Activate(obj);
			}
			_event.ResetAction();
			_event._isConflict = isConflict;
			_event._creationDate = com.db4o.foundation.TimeStampIdGenerator.IdToMilliseconds(
				ownerRef.Uuid().GetLongPart());
			long modificationDate = com.db4o.foundation.TimeStampIdGenerator.IdToMilliseconds
				(ownerRef.Version());
			if (owner == _providerA)
			{
				_stateInA.SetAll(obj, false, wasModified, modificationDate);
				_stateInB.SetAll(null, false, false, -1);
			}
			else
			{
				_stateInA.SetAll(null, false, false, -1);
				_stateInB.SetAll(obj, false, wasModified, modificationDate);
			}
			_listener.OnReplicate(_event);
			if (isConflict && !_event._actionWasChosen)
			{
				ThrowReplicationConflictException();
			}
			if (_event._actionWasChosen)
			{
				if (_event._actionChosenState == null)
				{
					return false;
				}
				if (_event._actionChosenState == _stateInA)
				{
					prevailing = _stateInA.GetObject();
				}
				if (_event._actionChosenState == _stateInB)
				{
					prevailing = _stateInB.GetObject();
				}
			}
			if (prevailing == null)
			{
				if (_directionTo == other)
				{
					return false;
				}
				ownerRef.MarkForDeleting();
				return !_event._actionShouldStopTraversal;
			}
			bool needsToBeActivated = !isConflict;
			return HandleNewObject(obj, ownerRef, owner, other, referencingObject, fieldName, 
				needsToBeActivated, true);
		}

		private bool HandleNewObject(object obj, com.db4o.drs.inside.ReplicationReference
			 ownerRef, com.db4o.drs.inside.ReplicationProviderInside owner, com.db4o.drs.inside.ReplicationProviderInside
			 other, object referencingObject, string fieldName, bool needsToBeActivated, bool
			 listenerAlreadyNotified)
		{
			if (_directionTo == owner)
			{
				return false;
			}
			if (needsToBeActivated)
			{
				owner.Activate(obj);
			}
			if (!listenerAlreadyNotified)
			{
				_event.ResetAction();
				_event._isConflict = false;
				_event._creationDate = com.db4o.foundation.TimeStampIdGenerator.IdToMilliseconds(
					ownerRef.Uuid().GetLongPart());
				if (owner == _providerA)
				{
					_stateInA.SetAll(obj, true, false, -1);
					_stateInB.SetAll(null, false, false, -1);
				}
				else
				{
					_stateInA.SetAll(null, false, false, -1);
					_stateInB.SetAll(obj, true, false, -1);
				}
				_listener.OnReplicate(_event);
				if (_event._actionWasChosen)
				{
					if (_event._actionChosenState == null)
					{
						return false;
					}
					if (_event._actionChosenState.GetObject() != obj)
					{
						return false;
					}
				}
			}
			object counterpart = EmptyClone(owner, obj);
			ownerRef.SetCounterpart(counterpart);
			ownerRef.MarkForReplicating();
			com.db4o.drs.inside.ReplicationReference otherRef = other.ReferenceNewObject(counterpart
				, ownerRef, GetCounterpartRef(referencingObject), fieldName);
			PutCounterpartRef(obj, otherRef);
			if (_event._actionShouldStopTraversal)
			{
				return false;
			}
			return true;
		}

		private void ThrowReplicationConflictException()
		{
			throw new com.db4o.drs.ReplicationConflictException("A replication conflict ocurred and the ReplicationEventListener, if any, did not choose which state should override the other."
				);
		}

		private object EmptyClone(com.db4o.drs.inside.ReplicationProviderInside sourceProvider
			, object obj)
		{
			if (obj == null)
			{
				return null;
			}
			com.db4o.reflect.ReflectClass claxx = _reflector.ForObject(obj);
			if (claxx.IsSecondClass())
			{
				throw new j4o.lang.RuntimeException("IllegalState");
			}
			if (claxx.IsArray())
			{
				throw new j4o.lang.RuntimeException("IllegalState");
			}
			if (_collectionHandler.CanHandle(claxx))
			{
				return CollectionClone(obj, claxx);
			}
			claxx.SkipConstructor(true);
			object result = claxx.NewInstance();
			if (result == null)
			{
				throw new j4o.lang.RuntimeException("Unable to create a new instance of " + obj.GetType
					());
			}
			return result;
		}

		private object CollectionClone(object original, com.db4o.reflect.ReflectClass claxx
			)
		{
			return _collectionHandler.EmptyClone(original, claxx);
		}

		private com.db4o.drs.inside.ReplicationReference GetCounterpartRef(object original
			)
		{
			return (com.db4o.drs.inside.ReplicationReference)_counterpartRefsByOriginal.Get(original
				);
		}

		private void PutCounterpartRef(object obj, com.db4o.drs.inside.ReplicationReference
			 otherRef)
		{
			if (_counterpartRefsByOriginal.Get(obj) != null)
			{
				throw new j4o.lang.RuntimeException("illegal state");
			}
			_counterpartRefsByOriginal.Put(obj, otherRef);
		}
	}
}

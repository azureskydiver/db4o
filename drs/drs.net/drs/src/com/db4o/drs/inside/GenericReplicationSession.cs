namespace com.db4o.drs.inside
{
	public sealed class GenericReplicationSession : com.db4o.drs.ReplicationSession
	{
		private const int SIZE = 10000;

		private readonly com.db4o.drs.inside.ReplicationReflector _reflector;

		private readonly com.db4o.drs.inside.CollectionHandler _collectionHandler;

		private com.db4o.drs.inside.ReplicationProviderInside _providerA;

		private com.db4o.drs.inside.ReplicationProviderInside _providerB;

		private com.db4o.drs.ReplicationProvider _directionTo;

		private readonly com.db4o.drs.ReplicationEventListener _listener;

		private readonly com.db4o.drs.inside.traversal.Traverser _traverser;

		private long _lastReplicationVersion;

		private com.db4o.foundation.Hashtable4 _processedUuids;

		private bool _isReplicatingOnlyDeletions;

		public GenericReplicationSession(com.db4o.drs.inside.ReplicationProviderInside _peerA
			, com.db4o.drs.inside.ReplicationProviderInside _peerB) : this(_peerA, _peerB, new 
			com.db4o.drs.inside.DefaultReplicationEventListener())
		{
		}

		public GenericReplicationSession(com.db4o.drs.ReplicationProvider providerA, com.db4o.drs.ReplicationProvider
			 providerB, com.db4o.drs.ReplicationEventListener listener)
		{
			_reflector = com.db4o.drs.inside.ReplicationReflector.GetInstance();
			_collectionHandler = new com.db4o.drs.inside.CollectionHandlerImpl(_reflector.Reflector
				());
			_traverser = new com.db4o.drs.inside.traversal.GenericTraverser(_reflector.Reflector
				(), _collectionHandler);
			_providerA = (com.db4o.drs.inside.ReplicationProviderInside)providerA;
			_providerB = (com.db4o.drs.inside.ReplicationProviderInside)providerB;
			_listener = listener;
			lock (_providerA.GetMonitor())
			{
				lock (_providerB.GetMonitor())
				{
					_providerA.StartReplicationTransaction(_providerB.GetSignature());
					_providerB.StartReplicationTransaction(_providerA.GetSignature());
					if (_providerA.GetLastReplicationVersion() != _providerB.GetLastReplicationVersion
						())
					{
						throw new j4o.lang.RuntimeException("Version numbers must be the same");
					}
					_lastReplicationVersion = _providerA.GetLastReplicationVersion();
				}
			}
			ResetProcessedUuids();
		}

		public void CheckConflict(object root)
		{
			try
			{
				PrepareGraphToBeReplicated(root);
			}
			finally
			{
				_providerA.ClearAllReferences();
				_providerB.ClearAllReferences();
			}
		}

		public void Close()
		{
			_providerA.Destroy();
			_providerB.Destroy();
			_providerA = null;
			_providerB = null;
			_processedUuids = null;
		}

		public void Commit()
		{
			lock (_providerA.GetMonitor())
			{
				lock (_providerB.GetMonitor())
				{
					long maxVersion = _providerA.GetCurrentVersion() > _providerB.GetCurrentVersion()
						 ? _providerA.GetCurrentVersion() : _providerB.GetCurrentVersion();
					_providerA.SyncVersionWithPeer(maxVersion);
					_providerB.SyncVersionWithPeer(maxVersion);
					maxVersion++;
					_providerA.CommitReplicationTransaction(maxVersion);
					_providerB.CommitReplicationTransaction(maxVersion);
				}
			}
		}

		public com.db4o.drs.ReplicationProvider ProviderA()
		{
			return _providerA;
		}

		public com.db4o.drs.ReplicationProvider ProviderB()
		{
			return _providerB;
		}

		public void Replicate(object root)
		{
			try
			{
				PrepareGraphToBeReplicated(root);
				CopyStateAcross(_providerA);
				CopyStateAcross(_providerB);
				StoreChangedObjectsIn(_providerA);
				StoreChangedObjectsIn(_providerB);
			}
			finally
			{
				_providerA.ClearAllReferences();
				_providerB.ClearAllReferences();
			}
		}

		public void ReplicateDeletions(System.Type extent)
		{
			ReplicateDeletions(extent, _providerA);
			ReplicateDeletions(extent, _providerB);
		}

		private void ReplicateDeletions(System.Type extent, com.db4o.drs.inside.ReplicationProviderInside
			 provider)
		{
			_isReplicatingOnlyDeletions = true;
			try
			{
				com.db4o.ObjectSet instances = provider.GetStoredObjects(extent);
				while (instances.HasNext())
				{
					Replicate(instances.Next());
				}
			}
			finally
			{
				_isReplicatingOnlyDeletions = false;
			}
		}

		public void Rollback()
		{
			_providerA.RollbackReplication();
			_providerB.RollbackReplication();
		}

		public void SetDirection(com.db4o.drs.ReplicationProvider replicateFrom, com.db4o.drs.ReplicationProvider
			 replicateTo)
		{
			if (replicateFrom == _providerA && replicateTo == _providerB)
			{
				_directionTo = _providerB;
			}
			if (replicateFrom == _providerB && replicateTo == _providerA)
			{
				_directionTo = _providerA;
			}
		}

		private void PrepareGraphToBeReplicated(object root)
		{
			_traverser.TraverseGraph(root, new com.db4o.drs.inside.InstanceReplicationPreparer
				(_providerA, _providerB, _directionTo, _listener, _isReplicatingOnlyDeletions, _lastReplicationVersion
				, _processedUuids, _traverser, _reflector, _collectionHandler));
		}

		private object ArrayClone(object original, com.db4o.reflect.ReflectClass claxx, com.db4o.drs.inside.ReplicationProviderInside
			 sourceProvider)
		{
			com.db4o.reflect.ReflectClass componentType = _reflector.GetComponentType(claxx);
			int[] dimensions = _reflector.ArrayDimensions(original);
			object result = _reflector.NewArrayInstance(componentType, dimensions);
			object[] flatContents = _reflector.ArrayContents(original);
			if (!claxx.IsSecondClass())
			{
				ReplaceWithCounterparts(flatContents, sourceProvider);
			}
			_reflector.ArrayShape(flatContents, 0, result, dimensions, 0);
			return result;
		}

		private void CopyFieldValuesAcross(object src, object dest, com.db4o.reflect.ReflectClass
			 claxx, com.db4o.drs.inside.ReplicationProviderInside sourceProvider)
		{
			com.db4o.reflect.ReflectField[] fields;
			fields = claxx.GetDeclaredFields();
			for (int i = 0; i < fields.Length; i++)
			{
				com.db4o.reflect.ReflectField field = fields[i];
				if (field.IsStatic())
				{
					continue;
				}
				if (field.IsTransient())
				{
					continue;
				}
				field.SetAccessible();
				object value = field.Get(src);
				field.Set(dest, FindCounterpart(value, sourceProvider));
			}
			com.db4o.reflect.ReflectClass superclass = claxx.GetSuperclass();
			if (superclass == null)
			{
				return;
			}
			CopyFieldValuesAcross(src, dest, superclass, sourceProvider);
		}

		private void CopyStateAcross(com.db4o.drs.inside.ReplicationProviderInside sourceProvider
			)
		{
			if (_directionTo == sourceProvider)
			{
				return;
			}
			sourceProvider.VisitCachedReferences(new _AnonymousInnerClass191(this, sourceProvider
				));
		}

		private sealed class _AnonymousInnerClass191 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass191(GenericReplicationSession _enclosing, com.db4o.drs.inside.ReplicationProviderInside
				 sourceProvider)
			{
				this._enclosing = _enclosing;
				this.sourceProvider = sourceProvider;
			}

			public void Visit(object obj)
			{
				this._enclosing.CopyStateAcross((com.db4o.drs.inside.ReplicationReference)obj, sourceProvider
					);
			}

			private readonly GenericReplicationSession _enclosing;

			private readonly com.db4o.drs.inside.ReplicationProviderInside sourceProvider;
		}

		private void CopyStateAcross(com.db4o.drs.inside.ReplicationReference sourceRef, 
			com.db4o.drs.inside.ReplicationProviderInside sourceProvider)
		{
			if (!sourceRef.IsMarkedForReplicating())
			{
				return;
			}
			CopyStateAcross(sourceRef.Object(), sourceRef.Counterpart(), sourceProvider);
		}

		private void CopyStateAcross(object source, object dest, com.db4o.drs.inside.ReplicationProviderInside
			 sourceProvider)
		{
			com.db4o.reflect.ReflectClass claxx = _reflector.ForObject(source);
			CopyFieldValuesAcross(source, dest, claxx, sourceProvider);
		}

		private void DeleteInDestination(com.db4o.drs.inside.ReplicationReference reference
			, com.db4o.drs.inside.ReplicationProviderInside destination)
		{
			if (!reference.IsMarkedForDeleting())
			{
				return;
			}
			destination.ReplicateDeletion(reference.Uuid());
		}

		private object FindCounterpart(object value, com.db4o.drs.inside.ReplicationProviderInside
			 sourceProvider)
		{
			if (value == null)
			{
				return null;
			}
			com.db4o.reflect.ReflectClass claxx = _reflector.ForObject(value);
			if (claxx.IsArray())
			{
				return ArrayClone(value, claxx, sourceProvider);
			}
			if (claxx.IsSecondClass())
			{
				return value;
			}
			if (_collectionHandler.CanHandle(value))
			{
				return CollectionClone(value, claxx, sourceProvider);
			}
			com.db4o.drs.inside.ReplicationReference _ref = sourceProvider.ProduceReference(value
				, null, null);
			if (_ref == null)
			{
				throw new System.ArgumentNullException("unable to find the ref of " + value + " of class "
					 + value.GetType());
			}
			object result = _ref.Counterpart();
			if (result == null)
			{
				throw new System.ArgumentNullException("unable to find the counterpart of " + value
					 + " of class " + value.GetType());
			}
			return result;
		}

		private object CollectionClone(object original, com.db4o.reflect.ReflectClass claxx
			, com.db4o.drs.inside.ReplicationProviderInside sourceProvider)
		{
			return _collectionHandler.CloneWithCounterparts(original, claxx, new _AnonymousInnerClass235
				(this, sourceProvider));
		}

		private sealed class _AnonymousInnerClass235 : com.db4o.drs.inside.CounterpartFinder
		{
			public _AnonymousInnerClass235(GenericReplicationSession _enclosing, com.db4o.drs.inside.ReplicationProviderInside
				 sourceProvider)
			{
				this._enclosing = _enclosing;
				this.sourceProvider = sourceProvider;
			}

			public object FindCounterpart(object original)
			{
				return this._enclosing.FindCounterpart(original, sourceProvider);
			}

			private readonly GenericReplicationSession _enclosing;

			private readonly com.db4o.drs.inside.ReplicationProviderInside sourceProvider;
		}

		private com.db4o.drs.inside.ReplicationProviderInside Other(com.db4o.drs.inside.ReplicationProviderInside
			 peer)
		{
			return peer == _providerA ? _providerB : _providerA;
		}

		private void ReplaceWithCounterparts(object[] objects, com.db4o.drs.inside.ReplicationProviderInside
			 sourceProvider)
		{
			for (int i = 0; i < objects.Length; i++)
			{
				object _object = objects[i];
				if (_object == null)
				{
					continue;
				}
				com.db4o.drs.inside.ReplicationReference replicationReference = sourceProvider.ProduceReference
					(_object, null, null);
				if (replicationReference == null)
				{
					throw new j4o.lang.RuntimeException(sourceProvider + " cannot find ref for " + _object
						);
				}
				objects[i] = replicationReference.Counterpart();
			}
		}

		private void ResetProcessedUuids()
		{
			_processedUuids = new com.db4o.foundation.Hashtable4(SIZE);
		}

		private void StoreChangedCounterpartInDestination(com.db4o.drs.inside.ReplicationReference
			 reference, com.db4o.drs.inside.ReplicationProviderInside destination)
		{
			bool markedForReplicating = reference.IsMarkedForReplicating();
			if (!markedForReplicating)
			{
				return;
			}
			destination.StoreReplica(reference.Counterpart());
		}

		private void StoreChangedObjectsIn(com.db4o.drs.inside.ReplicationProviderInside 
			destination)
		{
			com.db4o.drs.inside.ReplicationProviderInside source = Other(destination);
			if (_directionTo == source)
			{
				return;
			}
			destination.VisitCachedReferences(new _AnonymousInnerClass277(this, destination));
			source.VisitCachedReferences(new _AnonymousInnerClass283(this, destination));
		}

		private sealed class _AnonymousInnerClass277 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass277(GenericReplicationSession _enclosing, com.db4o.drs.inside.ReplicationProviderInside
				 destination)
			{
				this._enclosing = _enclosing;
				this.destination = destination;
			}

			public void Visit(object obj)
			{
				this._enclosing.DeleteInDestination((com.db4o.drs.inside.ReplicationReference)obj
					, destination);
			}

			private readonly GenericReplicationSession _enclosing;

			private readonly com.db4o.drs.inside.ReplicationProviderInside destination;
		}

		private sealed class _AnonymousInnerClass283 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass283(GenericReplicationSession _enclosing, com.db4o.drs.inside.ReplicationProviderInside
				 destination)
			{
				this._enclosing = _enclosing;
				this.destination = destination;
			}

			public void Visit(object obj)
			{
				this._enclosing.StoreChangedCounterpartInDestination((com.db4o.drs.inside.ReplicationReference
					)obj, destination);
			}

			private readonly GenericReplicationSession _enclosing;

			private readonly com.db4o.drs.inside.ReplicationProviderInside destination;
		}
	}
}

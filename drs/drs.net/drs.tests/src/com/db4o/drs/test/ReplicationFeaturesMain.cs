namespace com.db4o.drs.test
{
	public class ReplicationFeaturesMain : com.db4o.drs.test.DrsTestCase
	{
		private static readonly string AStuff = "A";

		private static readonly string BStuff = "B";

		private readonly com.db4o.drs.test.Set4 _setA = new com.db4o.drs.test.Set4(1);

		private readonly com.db4o.drs.test.Set4 _setB = new com.db4o.drs.test.Set4(1);

		private readonly com.db4o.drs.test.Set4 _setBoth = new com.db4o.drs.test.Set4(2);

		private readonly com.db4o.drs.test.Set4 _NONE = com.db4o.drs.test.Set4.EMPTY_SET;

		private com.db4o.drs.test.Set4 _direction;

		private com.db4o.drs.test.Set4 _containersToQueryFrom;

		private com.db4o.drs.test.Set4 _containersWithNewObjects;

		private com.db4o.drs.test.Set4 _containersWithChangedObjects;

		private com.db4o.drs.test.Set4 _containersWithDeletedObjects;

		private com.db4o.drs.test.Set4 _containerStateToPrevail;

		private string _intermittentErrors = "";

		private int _testCombination;

		private static void Fail(string _string)
		{
			j4o.lang.JavaSystem.err.Println(_string);
			throw new j4o.lang.RuntimeException(_string);
		}

		private void ReplicateQueryingFrom(com.db4o.drs.ReplicationSession replication, com.db4o.drs.ReplicationProvider
			 origin, com.db4o.drs.ReplicationProvider other)
		{
			com.db4o.drs.ReplicationConflictException exception = null;
			com.db4o.ObjectSet changes = origin.ObjectsChangedSinceLastReplication();
			while (changes.HasNext())
			{
				object changed = changes.Next();
				try
				{
					replication.Replicate(changed);
				}
				catch (com.db4o.drs.ReplicationConflictException e)
				{
					exception = e;
				}
			}
			if (exception != null)
			{
				throw exception;
			}
		}

		private bool IsReplicationConflictExceptionExpectedReplicatingModifications()
		{
			return WasConflictReplicatingModifications() && IsDefaultReplicationBehaviorAllowed
				();
		}

		private bool IsReplicationConflictExceptionExpectedReplicatingDeletions()
		{
			return WasConflictReplicatingDeletions() && IsDefaultReplicationBehaviorAllowed();
		}

		private bool WasConflictReplicatingDeletions()
		{
			if (_containersWithDeletedObjects.Size() != 1)
			{
				return false;
			}
			string container = (string)FirstContainerWithDeletedObjects();
			if (HasChanges(Other(container)))
			{
				return true;
			}
			if (_direction.Size() != 1)
			{
				return false;
			}
			return _direction.Contains(container);
		}

		private string FirstContainerWithDeletedObjects()
		{
			com.db4o.foundation.Iterator4 i = _containersWithDeletedObjects.Iterator();
			i.MoveNext();
			return (string)i.Current();
		}

		private bool IsDefaultReplicationBehaviorAllowed()
		{
			return _containerStateToPrevail != null && _containerStateToPrevail.IsEmpty();
		}

		protected virtual void ActualTest()
		{
			Clean();
			_setA.Add(AStuff);
			_setB.Add(BStuff);
			_setBoth.AddAll(_setA);
			_setBoth.AddAll(_setB);
			_testCombination = 0;
			TstWithDeletedObjectsIn(_NONE);
			if (_intermittentErrors.Length > 0)
			{
				j4o.lang.JavaSystem.err.Println("Intermittent errors found in test combinations:"
					 + _intermittentErrors);
				Db4oUnit.Assert.IsTrue(false);
			}
		}

		private void ChangeObject(com.db4o.drs.inside.TestableReplicationProviderInside container
			, string name, string newName)
		{
			com.db4o.drs.test.Replicated obj = Find(container, name);
			if (obj == null)
			{
				return;
			}
			obj.SetName(newName);
			container.Update(obj);
			Out("CHANGED: " + container + ": " + name + " => " + newName + " - " + obj);
		}

		private void CheckEmpty(com.db4o.drs.inside.TestableReplicationProviderInside provider
			)
		{
			if (provider.GetStoredObjects(typeof(com.db4o.drs.test.Replicated)).HasNext())
			{
				throw new j4o.lang.RuntimeException(provider.GetName() + " is not empty");
			}
		}

		private int checkNameCount = 0;

		private void CheckName(com.db4o.drs.inside.TestableReplicationProviderInside container
			, string name, bool isExpected)
		{
			Out("");
			Out(name + (isExpected ? " " : " NOT") + " expected in container " + ContainerName
				(container));
			com.db4o.drs.test.Replicated obj = Find(container, name);
			Out(checkNameCount.ToString());
			checkNameCount++;
			if (isExpected)
			{
				Db4oUnit.Assert.IsNotNull(obj);
			}
			else
			{
				Db4oUnit.Assert.IsNull(obj);
			}
		}

		private string ContainerName(com.db4o.drs.inside.TestableReplicationProviderInside
			 container)
		{
			return container == A().Provider() ? "A" : "B";
		}

		private void CheckNames()
		{
			CheckNames(AStuff, AStuff);
			CheckNames(AStuff, BStuff);
			CheckNames(BStuff, AStuff);
			CheckNames(BStuff, BStuff);
		}

		private void CheckNames(string origin, string inspected)
		{
			CheckName(Container(inspected), "oldFrom" + origin, IsOldNameExpected(inspected));
			CheckName(Container(inspected), "newFrom" + origin, IsNewNameExpected(origin, inspected
				));
			CheckName(Container(inspected), "oldFromAChangedIn" + origin, IsChangedNameExpected
				(origin, inspected));
			CheckName(Container(inspected), "oldFromBChangedIn" + origin, IsChangedNameExpected
				(origin, inspected));
		}

//		public override void Configure()
//		{
//			com.db4o.Db4o.Configure().GenerateUUIDs(int.MaxValue);
//			com.db4o.Db4o.Configure().GenerateVersionNumbers(int.MaxValue);
//		}

		private com.db4o.drs.inside.TestableReplicationProviderInside Container(string aOrB
			)
		{
			return aOrB.Equals(AStuff) ? A().Provider() : B().Provider();
		}

		private void DeleteObject(com.db4o.drs.inside.TestableReplicationProviderInside container
			, string name)
		{
			com.db4o.drs.test.Replicated obj = Find(container, name);
			container.Delete(obj);
		}

		private void DoIt()
		{
			InitState();
			PrintProvidersContent("before changes");
			PerformChanges();
			PrintProvidersContent("after changes");
			com.db4o.drs.ReplicationSession replication = new com.db4o.drs.inside.GenericReplicationSession
				(A().Provider(), B().Provider(), new _AnonymousInnerClass176(this));
			if (_direction.Size() == 1)
			{
				if (_direction.Contains(AStuff))
				{
					replication.SetDirection(B().Provider(), A().Provider());
				}
				if (_direction.Contains(BStuff))
				{
					replication.SetDirection(A().Provider(), B().Provider());
				}
			}
			Out("DIRECTION: " + _direction);
			bool successful = TryToReplicate(replication);
			replication.Commit();
			PrintProvidersContent("after replication");
			if (successful)
			{
				CheckNames();
			}
			Clean();
		}

		private sealed class _AnonymousInnerClass176 : com.db4o.drs.ReplicationEventListener
		{
			public _AnonymousInnerClass176(ReplicationFeaturesMain _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void OnReplicate(com.db4o.drs.ReplicationEvent e)
			{
				if (this._enclosing._containerStateToPrevail == null)
				{
					e.OverrideWith(null);
					return;
				}
				if (this._enclosing._containerStateToPrevail.IsEmpty())
				{
					return;
				}
				com.db4o.drs.ObjectState _override = this._enclosing._containerStateToPrevail.Contains
					(com.db4o.drs.test.ReplicationFeaturesMain.AStuff) ? e.StateInProviderA() : e.StateInProviderB
					();
				e.OverrideWith(_override);
			}

			private readonly ReplicationFeaturesMain _enclosing;
		}

		private void PrintProvidersContent(string msg)
		{
		}

		private void PrintProviderContent(com.db4o.drs.inside.TestableReplicationProviderInside
			 provider)
		{
//			com.db4o.ObjectContainer db = ((com.db4o.drs.db4o.Db4oReplicationProvider)provider
//				).ObjectContainer();
//			com.db4o.ObjectSet result = db.Query(typeof(com.db4o.drs.test.Replicated));
//			j4o.lang.JavaSystem._out.Println("PROVIDER: " + provider);
//			while (result.HasNext())
//			{
//				j4o.lang.JavaSystem._out.Println(result.Next());
//			}
		}

		private bool TryToReplicate(com.db4o.drs.ReplicationSession replication)
		{
			try
			{
				Replicate(replication, AStuff);
				Replicate(replication, BStuff);
				Db4oUnit.Assert.IsFalse(IsReplicationConflictExceptionExpectedReplicatingModifications
					());
			}
			catch (com.db4o.drs.ReplicationConflictException e)
			{
				Out("Conflict exception during modification replication.");
				Db4oUnit.Assert.IsTrue(IsReplicationConflictExceptionExpectedReplicatingModifications
					());
				return false;
			}
			try
			{
				if (IsDeletionReplicationTriggered())
				{
					replication.ReplicateDeletions(typeof(com.db4o.drs.test.Replicated));
				}
				Db4oUnit.Assert.IsFalse(IsReplicationConflictExceptionExpectedReplicatingDeletions
					());
			}
			catch (com.db4o.drs.ReplicationConflictException e)
			{
				Out("Conflict exception during deletion replication.");
				Db4oUnit.Assert.IsTrue(IsReplicationConflictExceptionExpectedReplicatingDeletions
					());
				return false;
			}
			return true;
		}

		private void Replicate(com.db4o.drs.ReplicationSession replication, string originName
			)
		{
			com.db4o.drs.ReplicationProvider origin = Container(originName);
			com.db4o.drs.ReplicationProvider destination = Container(Other(originName));
			if (!_containersToQueryFrom.Contains(originName))
			{
				return;
			}
			ReplicateQueryingFrom(replication, origin, destination);
		}

		private com.db4o.drs.test.Replicated Find(com.db4o.drs.inside.TestableReplicationProviderInside
			 container, string name)
		{
			com.db4o.ObjectSet storedObjects = container.GetStoredObjects(typeof(com.db4o.drs.test.Replicated
				));
			int resultCount = 0;
			com.db4o.drs.test.Replicated result = null;
			while (storedObjects.HasNext())
			{
				com.db4o.drs.test.Replicated replicated = (com.db4o.drs.test.Replicated)storedObjects
					.Next();
				if (replicated == null)
				{
					throw new j4o.lang.RuntimeException();
				}
				if (name.Equals(replicated.GetName()))
				{
					result = replicated;
					resultCount++;
				}
			}
			if (resultCount > 1)
			{
				Fail("At most one object with name " + name + " was expected.");
			}
			return result;
		}

		private bool HasChanges(string container)
		{
			return _containersWithChangedObjects.Contains(container);
		}

		private bool HasDeletions(string container)
		{
			return _containersWithDeletedObjects.Contains(container);
		}

		private void InitState()
		{
			CheckEmpty(A().Provider());
			CheckEmpty(B().Provider());
			A().Provider().StoreNew(new com.db4o.drs.test.Replicated("oldFromA"));
			B().Provider().StoreNew(new com.db4o.drs.test.Replicated("oldFromB"));
			A().Provider().Commit();
			B().Provider().Commit();
			com.db4o.drs.ReplicationSession replication = new com.db4o.drs.inside.GenericReplicationSession
				(A().Provider(), B().Provider());
			ReplicateQueryingFrom(replication, A().Provider(), B().Provider());
			ReplicateQueryingFrom(replication, B().Provider(), A().Provider());
			replication.Commit();
		}

		private bool IsChangedNameExpected(string changedContainer, string inspectedContainer
			)
		{
			if (!HasChanges(changedContainer))
			{
				return false;
			}
			if (IsDeletionExpected(inspectedContainer))
			{
				return false;
			}
			if (IsDeletionExpected(changedContainer))
			{
				return false;
			}
			if (inspectedContainer.Equals(changedContainer))
			{
				return !DidReceiveRemoteState(inspectedContainer);
			}
			return DidReceiveRemoteState(inspectedContainer);
		}

		private bool DidReceiveRemoteState(string inspectedContainer)
		{
			string other = Other(inspectedContainer);
			if (IsDirectionTo(other))
			{
				return false;
			}
			if (_containerStateToPrevail == null)
			{
				return false;
			}
			if (_containerStateToPrevail.Contains(inspectedContainer))
			{
				return false;
			}
			if (_containerStateToPrevail.Contains(other))
			{
				if (IsModificationReplicationTriggered())
				{
					return true;
				}
				if (IsDeletionReplicationTriggered())
				{
					return true;
				}
				return false;
			}
			if (HasChanges(inspectedContainer))
			{
				return false;
			}
			return IsModificationReplicationTriggered();
		}

		private bool IsDeletionReplicationTriggered()
		{
			return !_containersWithDeletedObjects.IsEmpty();
		}

		private bool IsDirectionTo(string container)
		{
			return _direction.Size() == 1 && _direction.Contains(container);
		}

		private bool WasConflictReplicatingModifications()
		{
			return WasConflictWhileReplicatingModificationsQueryingFrom(AStuff) || WasConflictWhileReplicatingModificationsQueryingFrom
				(BStuff);
		}

		private bool IsModificationReplicationTriggered()
		{
			return WasModificationReplicationTriggeredQueryingFrom(AStuff) || WasModificationReplicationTriggeredQueryingFrom
				(BStuff);
		}

		private bool IsDeletionExpected(string inspectedContainer)
		{
			if (_containerStateToPrevail == null)
			{
				return HasDeletions(inspectedContainer);
			}
			if (_containerStateToPrevail.Contains(inspectedContainer))
			{
				return HasDeletions(inspectedContainer);
			}
			string other = Other(inspectedContainer);
			if (IsDirectionTo(other))
			{
				return HasDeletions(inspectedContainer);
			}
			if (_containerStateToPrevail.Contains(other))
			{
				return HasDeletions(other);
			}
			return IsDeletionReplicationTriggered();
		}

		private bool IsNewNameExpected(string origin, string inspected)
		{
			if (!_containersWithNewObjects.Contains(origin))
			{
				return false;
			}
			if (origin.Equals(inspected))
			{
				return true;
			}
			if (_containerStateToPrevail == null)
			{
				return false;
			}
			if (_containerStateToPrevail.Contains(inspected))
			{
				return false;
			}
			if (!_containersToQueryFrom.Contains(origin))
			{
				return false;
			}
			return _direction.Contains(inspected);
		}

		private bool IsOldNameExpected(string inspectedContainer)
		{
			if (IsDeletionExpected(inspectedContainer))
			{
				return false;
			}
			if (IsChangedNameExpected(AStuff, inspectedContainer))
			{
				return false;
			}
			if (IsChangedNameExpected(BStuff, inspectedContainer))
			{
				return false;
			}
			return true;
		}

		private string Other(string aOrB)
		{
			return aOrB.Equals(AStuff) ? BStuff : AStuff;
		}

		private void PerformChanges()
		{
			if (_containersWithNewObjects.Contains(AStuff))
			{
				A().Provider().StoreNew(new com.db4o.drs.test.Replicated("newFromA"));
			}
			if (_containersWithNewObjects.Contains(BStuff))
			{
				B().Provider().StoreNew(new com.db4o.drs.test.Replicated("newFromB"));
			}
			if (HasDeletions(AStuff))
			{
				DeleteObject(A().Provider(), "oldFromA");
				DeleteObject(A().Provider(), "oldFromB");
			}
			if (HasDeletions(BStuff))
			{
				DeleteObject(B().Provider(), "oldFromA");
				DeleteObject(B().Provider(), "oldFromB");
			}
			if (HasChanges(AStuff))
			{
				ChangeObject(A().Provider(), "oldFromA", "oldFromAChangedInA");
				ChangeObject(A().Provider(), "oldFromB", "oldFromBChangedInA");
			}
			if (HasChanges(BStuff))
			{
				ChangeObject(B().Provider(), "oldFromA", "oldFromAChangedInB");
				ChangeObject(B().Provider(), "oldFromB", "oldFromBChangedInB");
			}
			A().Provider().Commit();
			B().Provider().Commit();
		}

		private string Print(com.db4o.drs.test.Set4 containerSet)
		{
			if (containerSet == null)
			{
				return "null";
			}
			if (containerSet.IsEmpty())
			{
				return "NONE";
			}
			if (containerSet.Size() == 2)
			{
				return "BOTH";
			}
			return First(containerSet);
		}

		private string First(com.db4o.drs.test.Set4 containerSet)
		{
			com.db4o.foundation.Iterator4 i = containerSet.Iterator();
			i.MoveNext();
			return (string)i.Current();
		}

		private void PrintCombination()
		{
			Out("" + _testCombination + " =================================");
			Out("New Objects In: " + Print(_containersWithNewObjects));
			Out("Changed Objects In: " + Print(_containersWithChangedObjects));
			Out("Deleted Objects In: " + Print(_containersWithDeletedObjects));
			Out("Querying From: " + Print(_containersToQueryFrom));
			Out("Direction: To " + Print(_direction));
			Out("Prevailing State: " + Print(_containerStateToPrevail));
		}

		private void RunCurrentCombination()
		{
			_testCombination++;
			Out("" + _testCombination + " =================================");
			PrintCombination();
			if (_testCombination < 0)
			{
				return;
			}
			int _errors = 0;
			while (true)
			{
				try
				{
					DoIt();
					break;
				}
				catch (j4o.lang.RuntimeException rx)
				{
					_errors++;
					if (_errors == 1)
					{
						Sleep(100);
						PrintCombination();
						throw rx;
					}
				}
			}
			if (_errors > 0)
			{
				_intermittentErrors += "\n\t Combination: " + _testCombination + " (" + _errors +
					 " errors)";
			}
		}

		private static void Out(string _string)
		{
		}

		public virtual void Test()
		{
			ActualTest();
		}

		private void TstDirection(com.db4o.drs.test.Set4 direction)
		{
			_direction = direction;
			TstQueryingFrom(_setA);
			TstQueryingFrom(_setB);
			TstQueryingFrom(_setBoth);
		}

		private void TstQueryingFrom(com.db4o.drs.test.Set4 containersToQueryFrom)
		{
			_containersToQueryFrom = containersToQueryFrom;
			TstWithNewObjectsIn(_NONE);
			TstWithNewObjectsIn(_setA);
			TstWithNewObjectsIn(_setB);
			TstWithNewObjectsIn(_setBoth);
		}

		private void TstWithChangedObjectsIn(com.db4o.drs.test.Set4 containers)
		{
			_containersWithChangedObjects = containers;
			TstWithContainerStateToPrevail(_NONE);
			TstWithContainerStateToPrevail(_setA);
			TstWithContainerStateToPrevail(_setB);
			TstWithContainerStateToPrevail(null);
		}

		private void TstWithDeletedObjectsIn(com.db4o.drs.test.Set4 containers)
		{
			_containersWithDeletedObjects = containers;
			TstDirection(_setA);
			TstDirection(_setB);
			TstDirection(_setBoth);
		}

		private void TstWithNewObjectsIn(com.db4o.drs.test.Set4 containersWithNewObjects)
		{
			_containersWithNewObjects = containersWithNewObjects;
			TstWithChangedObjectsIn(_NONE);
			TstWithChangedObjectsIn(_setA);
			TstWithChangedObjectsIn(_setB);
			TstWithChangedObjectsIn(_setBoth);
		}

		private void TstWithContainerStateToPrevail(com.db4o.drs.test.Set4 containers)
		{
			_containerStateToPrevail = containers;
			RunCurrentCombination();
		}

		private bool WasConflictWhileReplicatingModificationsQueryingFrom(string container
			)
		{
			if (!WasModificationReplicationTriggeredQueryingFrom(container))
			{
				return false;
			}
			if (_containersWithChangedObjects.ContainsAll(_direction))
			{
				return true;
			}
			return HasDeletions(Other(container));
		}

		private bool WasModificationReplicationTriggeredQueryingFrom(string container)
		{
			if (!_containersToQueryFrom.Contains(container))
			{
				return false;
			}
			if (_containersWithDeletedObjects.Contains(container))
			{
				return false;
			}
			return _containersWithChangedObjects.Contains(container);
		}
	}

	internal class Set4
	{
		public static readonly com.db4o.drs.test.Set4 EMPTY_SET = new com.db4o.drs.test.Set4
			(0);

		internal com.db4o.foundation.Hashtable4 _table;

		public Set4(int size)
		{
			_table = new com.db4o.foundation.Hashtable4(size);
		}

		public virtual void Add(object element)
		{
			_table.Put(element, element);
		}

		public virtual void AddAll(com.db4o.drs.test.Set4 other)
		{
			other._table.ForEachKey(new _AnonymousInnerClass564(this));
		}

		private sealed class _AnonymousInnerClass564 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass564(Set4 _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object element)
			{
				this._enclosing.Add(element);
			}

			private readonly Set4 _enclosing;
		}

		public virtual bool IsEmpty()
		{
			return _table.Size() == 0;
		}

		public virtual int Size()
		{
			return _table.Size();
		}

		public virtual bool Contains(object element)
		{
			return _table.Get(element) != null;
		}

		public virtual bool ContainsAll(com.db4o.drs.test.Set4 other)
		{
			com.db4o.foundation.Iterator4 i = other.Iterator();
			while (i.MoveNext())
			{
				if (!Contains(i.Current()))
				{
					return false;
				}
			}
			return true;
		}

		public virtual com.db4o.foundation.Iterator4 Iterator()
		{
			com.db4o.foundation.Collection4 elements = new com.db4o.foundation.Collection4();
			_table.ForEachKey(new _AnonymousInnerClass593(this, elements));
			return elements.Iterator();
		}

		private sealed class _AnonymousInnerClass593 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass593(Set4 _enclosing, com.db4o.foundation.Collection4 elements
				)
			{
				this._enclosing = _enclosing;
				this.elements = elements;
			}

			public void Visit(object element)
			{
				elements.Add(element);
			}

			private readonly Set4 _enclosing;

			private readonly com.db4o.foundation.Collection4 elements;
		}

		public override string ToString()
		{
			j4o.lang.StringBuffer buf = new j4o.lang.StringBuffer("[");
			bool first = true;
			for (com.db4o.foundation.Iterator4 iter = Iterator(); iter.MoveNext(); )
			{
				if (!first)
				{
					buf.Append(',');
				}
				else
				{
					first = false;
				}
				buf.Append(iter.Current().ToString());
			}
			buf.Append(']');
			return buf.ToString();
		}
	}
}

namespace com.db4o.drs.test
{
	public class ReplicationEventTest : com.db4o.drs.test.DrsTestCase
	{
		private static readonly string IN_A = "in A";

		private static readonly string MODIFIED_IN_A = "modified in A";

		private static readonly string MODIFIED_IN_B = "modified in B";

		public virtual void Test()
		{
			TstNoAction();
			Clean();
			TstNewObject();
			Clean();
			TstOverrideWhenNoConflicts();
			Clean();
			TstOverrideWhenConflicts();
			Clean();
			TstStopTraversal();
		}

		private void DeleteInProviderA()
		{
			A().Provider().DeleteAllInstances(typeof(com.db4o.drs.test.SPCParent));
			A().Provider().DeleteAllInstances(typeof(com.db4o.drs.test.SPCChild));
			A().Provider().Commit();
			EnsureNotExist(A().Provider(), typeof(com.db4o.drs.test.SPCChild));
			EnsureNotExist(A().Provider(), typeof(com.db4o.drs.test.SPCParent));
		}

		private void EnsureNames(com.db4o.drs.inside.TestableReplicationProviderInside provider
			, string parentName, string childName)
		{
			EnsureOneInstanceOfParentAndChild(provider);
			com.db4o.drs.test.SPCParent parent = (com.db4o.drs.test.SPCParent)GetOneInstance(
				provider, typeof(com.db4o.drs.test.SPCParent));
			if (!parent.GetName().Equals(parentName))
			{
				System.Console.Out.WriteLine("expected = " + parentName);
				System.Console.Out.WriteLine("actual = " + parent.GetName());
			}
			Db4oUnit.Assert.AreEqual(parent.GetName(), parentName);
			Db4oUnit.Assert.AreEqual(childName, parent.GetChild().GetName());
		}

		private void EnsureNotExist(com.db4o.drs.inside.TestableReplicationProviderInside
			 provider, System.Type type)
		{
			Db4oUnit.Assert.IsTrue(!provider.GetStoredObjects(type).HasNext());
		}

		private void EnsureOneInstanceOfParentAndChild(com.db4o.drs.inside.TestableReplicationProviderInside
			 provider)
		{
			EnsureOneInstance(provider, typeof(com.db4o.drs.test.SPCParent));
			EnsureOneInstance(provider, typeof(com.db4o.drs.test.SPCChild));
		}

		private void ModifyInProviderA()
		{
			com.db4o.drs.test.SPCParent parent = (com.db4o.drs.test.SPCParent)GetOneInstance(
				A().Provider(), typeof(com.db4o.drs.test.SPCParent));
			parent.SetName(MODIFIED_IN_A);
			com.db4o.drs.test.SPCChild child = parent.GetChild();
			child.SetName(MODIFIED_IN_A);
			A().Provider().Update(parent);
			A().Provider().Update(child);
			A().Provider().Commit();
			EnsureNames(A().Provider(), MODIFIED_IN_A, MODIFIED_IN_A);
		}

		private void ModifyInProviderB()
		{
			com.db4o.drs.test.SPCParent parent = (com.db4o.drs.test.SPCParent)GetOneInstance(
				B().Provider(), typeof(com.db4o.drs.test.SPCParent));
			parent.SetName(MODIFIED_IN_B);
			com.db4o.drs.test.SPCChild child = parent.GetChild();
			child.SetName(MODIFIED_IN_B);
			B().Provider().Update(parent);
			B().Provider().Update(child);
			B().Provider().Commit();
			EnsureNames(B().Provider(), MODIFIED_IN_B, MODIFIED_IN_B);
		}

		private void ReplicateAllToProviderBFirstTime()
		{
			ReplicateAll(A().Provider(), B().Provider());
			EnsureNames(A().Provider(), IN_A, IN_A);
			EnsureNames(B().Provider(), IN_A, IN_A);
		}

		private void StoreParentAndChildToProviderA()
		{
			com.db4o.drs.test.SPCChild child = new com.db4o.drs.test.SPCChild(IN_A);
			com.db4o.drs.test.SPCParent parent = new com.db4o.drs.test.SPCParent(child, IN_A);
			A().Provider().StoreNew(parent);
			A().Provider().Commit();
			EnsureNames(A().Provider(), IN_A, IN_A);
		}

		private void TstNewObject()
		{
			StoreParentAndChildToProviderA();
			com.db4o.drs.test.ReplicationEventTest.BooleanClosure invoked = new com.db4o.drs.test.ReplicationEventTest.BooleanClosure
				(false);
			com.db4o.drs.ReplicationEventListener listener = new _AnonymousInnerClass221(this
				, invoked);
			ReplicateAll(A().Provider(), B().Provider(), listener);
			Db4oUnit.Assert.IsTrue(invoked.GetValue());
			EnsureNames(A().Provider(), IN_A, IN_A);
			EnsureNotExist(B().Provider(), typeof(com.db4o.drs.test.SPCParent));
			EnsureNotExist(B().Provider(), typeof(com.db4o.drs.test.SPCChild));
		}

		private sealed class _AnonymousInnerClass221 : com.db4o.drs.ReplicationEventListener
		{
			public _AnonymousInnerClass221(ReplicationEventTest _enclosing, com.db4o.drs.test.ReplicationEventTest.BooleanClosure
				 invoked)
			{
				this._enclosing = _enclosing;
				this.invoked = invoked;
			}

			public void OnReplicate(com.db4o.drs.ReplicationEvent @event)
			{
				invoked.SetValue(true);
				com.db4o.drs.ObjectState stateA = @event.StateInProviderA();
				com.db4o.drs.ObjectState stateB = @event.StateInProviderB();
				Db4oUnit.Assert.IsTrue(stateA.IsNew());
				Db4oUnit.Assert.IsTrue(!stateB.IsNew());
				Db4oUnit.Assert.IsNotNull(stateA.GetObject());
				Db4oUnit.Assert.IsNull(stateB.GetObject());
				@event.OverrideWith(null);
			}

			private readonly ReplicationEventTest _enclosing;

			private readonly com.db4o.drs.test.ReplicationEventTest.BooleanClosure invoked;
		}

		private void TstNoAction()
		{
			StoreParentAndChildToProviderA();
			ReplicateAllToProviderBFirstTime();
			ModifyInProviderB();
			com.db4o.drs.ReplicationEventListener listener = new _AnonymousInnerClass252(this
				);
			ReplicateAll(B().Provider(), A().Provider(), listener);
			EnsureNames(A().Provider(), MODIFIED_IN_B, MODIFIED_IN_B);
			EnsureNames(B().Provider(), MODIFIED_IN_B, MODIFIED_IN_B);
		}

		private sealed class _AnonymousInnerClass252 : com.db4o.drs.ReplicationEventListener
		{
			public _AnonymousInnerClass252(ReplicationEventTest _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void OnReplicate(com.db4o.drs.ReplicationEvent @event)
			{
			}

			private readonly ReplicationEventTest _enclosing;
		}

		private void TstOverrideWhenConflicts()
		{
			StoreParentAndChildToProviderA();
			ReplicateAllToProviderBFirstTime();
			ModifyInProviderA();
			ModifyInProviderB();
			com.db4o.drs.ReplicationEventListener listener = new _AnonymousInnerClass272(this
				);
			ReplicateAll(A().Provider(), B().Provider(), listener);
			EnsureNames(A().Provider(), MODIFIED_IN_B, MODIFIED_IN_B);
			EnsureNames(B().Provider(), MODIFIED_IN_B, MODIFIED_IN_B);
		}

		private sealed class _AnonymousInnerClass272 : com.db4o.drs.ReplicationEventListener
		{
			public _AnonymousInnerClass272(ReplicationEventTest _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void OnReplicate(com.db4o.drs.ReplicationEvent @event)
			{
				Db4oUnit.Assert.IsTrue(@event.IsConflict());
				if (@event.IsConflict())
				{
					@event.OverrideWith(@event.StateInProviderB());
				}
			}

			private readonly ReplicationEventTest _enclosing;
		}

		private void TstOverrideWhenNoConflicts()
		{
			StoreParentAndChildToProviderA();
			ReplicateAllToProviderBFirstTime();
			ModifyInProviderB();
			com.db4o.drs.ReplicationEventListener listener = new _AnonymousInnerClass292(this
				);
			ReplicateAll(B().Provider(), A().Provider(), listener);
			EnsureNames(A().Provider(), IN_A, IN_A);
			EnsureNames(B().Provider(), IN_A, IN_A);
		}

		private sealed class _AnonymousInnerClass292 : com.db4o.drs.ReplicationEventListener
		{
			public _AnonymousInnerClass292(ReplicationEventTest _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void OnReplicate(com.db4o.drs.ReplicationEvent @event)
			{
				Db4oUnit.Assert.IsTrue(!@event.IsConflict());
				@event.OverrideWith(@event.StateInProviderB());
			}

			private readonly ReplicationEventTest _enclosing;
		}

		private void TstStopTraversal()
		{
			StoreParentAndChildToProviderA();
			ReplicateAllToProviderBFirstTime();
			ModifyInProviderA();
			ModifyInProviderB();
			com.db4o.drs.ReplicationEventListener listener = new _AnonymousInnerClass313(this
				);
			ReplicateAll(A().Provider(), B().Provider(), listener);
			EnsureNames(A().Provider(), MODIFIED_IN_A, MODIFIED_IN_A);
			EnsureNames(B().Provider(), MODIFIED_IN_B, MODIFIED_IN_B);
		}

		private sealed class _AnonymousInnerClass313 : com.db4o.drs.ReplicationEventListener
		{
			public _AnonymousInnerClass313(ReplicationEventTest _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void OnReplicate(com.db4o.drs.ReplicationEvent @event)
			{
				Db4oUnit.Assert.IsTrue(@event.IsConflict());
				@event.OverrideWith(null);
			}

			private readonly ReplicationEventTest _enclosing;
		}

		internal class BooleanClosure
		{
			private bool value;

			public BooleanClosure(bool value)
			{
				this.value = value;
			}

			internal virtual void SetValue(bool v)
			{
				value = v;
			}

			public virtual bool GetValue()
			{
				return value;
			}
		}
	}
}

using System.Collections;

namespace com.db4o.drs.test
{
	public class R0to4Runner : com.db4o.drs.test.DrsTestCase
	{
		private const int LINKERS = 4;

		public R0to4Runner() : base()
		{
		}

		protected override void Clean()
		{
			Delete(A().Provider());
			Delete(B().Provider());
		}

		protected virtual void Delete(com.db4o.drs.inside.TestableReplicationProviderInside
			 provider)
		{
            IDictionary toDelete = new Hashtable();
		    
//			j4o.util.Set toDelete = new j4o.util.HashSet();
			com.db4o.ObjectSet rr = provider.GetStoredObjects(typeof(com.db4o.drs.test.R0));
			while (rr.HasNext())
			{
				object o = rr.Next();
				com.db4o.reflect.ReflectClass claxx = com.db4o.drs.inside.ReplicationReflector.GetInstance
					().Reflector().ForObject(o);
				SetFieldsToNull(o, claxx);
				toDelete.Add(o.ToString(), o);
			}
			for (System.Collections.IEnumerator iterator = toDelete.GetEnumerator(); iterator
				.MoveNext(); )
			{
				object o = iterator.Current;
				provider.Delete(o);
			}
			provider.Commit();
		}

		private void CompareR4(com.db4o.drs.inside.TestableReplicationProviderInside a, com.db4o.drs.inside.TestableReplicationProviderInside
			 b, bool isSameExpected)
		{
			com.db4o.ObjectSet it = a.GetStoredObjects(typeof(com.db4o.drs.test.R4));
			while (it.HasNext())
			{
				string name = ((com.db4o.drs.test.R4)it.Next()).name;
				com.db4o.ObjectSet it2 = b.GetStoredObjects(typeof(com.db4o.drs.test.R4));
				bool found = false;
				while (it2.HasNext())
				{
					string name2 = ((com.db4o.drs.test.R4)it2.Next()).name;
					if (name.Equals(name2))
					{
						found = true;
					}
				}
				Db4oUnit.Assert.IsTrue(found == isSameExpected);
			}
		}

		private void CopyAllToB(com.db4o.drs.inside.TestableReplicationProviderInside peerA
			, com.db4o.drs.inside.TestableReplicationProviderInside peerB)
		{
			Db4oUnit.Assert.IsTrue(ReplicateAll(peerA, peerB, false) == LINKERS * 5);
		}

		private void EnsureCount(com.db4o.drs.inside.TestableReplicationProviderInside provider
			, int linkers)
		{
			EnsureCount(provider, typeof(com.db4o.drs.test.R0), linkers * 5);
			EnsureCount(provider, typeof(com.db4o.drs.test.R1), linkers * 4);
			EnsureCount(provider, typeof(com.db4o.drs.test.R2), linkers * 3);
			EnsureCount(provider, typeof(com.db4o.drs.test.R3), linkers * 2);
			EnsureCount(provider, typeof(com.db4o.drs.test.R4), linkers * 1);
		}

		private void EnsureCount(com.db4o.drs.inside.TestableReplicationProviderInside provider
			, System.Type clazz, int count)
		{
			com.db4o.ObjectSet instances = provider.GetStoredObjects(clazz);
			int i = count;
			while (instances.HasNext())
			{
				instances.Next();
				i--;
			}
			Db4oUnit.Assert.IsTrue(i == 0);
		}

		private void EnsureR4Different(com.db4o.drs.inside.TestableReplicationProviderInside
			 peerA, com.db4o.drs.inside.TestableReplicationProviderInside peerB)
		{
			CompareR4(peerB, peerA, false);
		}

		private void EnsureR4Same(com.db4o.drs.inside.TestableReplicationProviderInside peerA
			, com.db4o.drs.inside.TestableReplicationProviderInside peerB)
		{
			CompareR4(peerB, peerA, true);
			CompareR4(peerA, peerB, true);
		}

		private void Init(com.db4o.drs.inside.TestableReplicationProviderInside peerA)
		{
			com.db4o.drs.test.R0Linker lCircles = new com.db4o.drs.test.R0Linker();
			lCircles.SetNames("circles");
			lCircles.LinkCircles();
			lCircles.Store(peerA);
			com.db4o.drs.test.R0Linker lList = new com.db4o.drs.test.R0Linker();
			lList.SetNames("list");
			lList.LinkList();
			lList.Store(peerA);
			com.db4o.drs.test.R0Linker lThis = new com.db4o.drs.test.R0Linker();
			lThis.SetNames("this");
			lThis.LinkThis();
			lThis.Store(peerA);
			com.db4o.drs.test.R0Linker lBack = new com.db4o.drs.test.R0Linker();
			lBack.SetNames("back");
			lBack.LinkBack();
			lBack.Store(peerA);
			peerA.Commit();
		}

		private void ModifyR4(com.db4o.drs.inside.TestableReplicationProviderInside provider
			)
		{
			com.db4o.ObjectSet it = provider.GetStoredObjects(typeof(com.db4o.drs.test.R4));
			while (it.HasNext())
			{
				com.db4o.drs.test.R4 r4 = (com.db4o.drs.test.R4)it.Next();
				r4.name = r4.name + "_";
				provider.Update(r4);
			}
			provider.Commit();
		}

		private int Replicate(com.db4o.drs.inside.TestableReplicationProviderInside peerA
			, com.db4o.drs.inside.TestableReplicationProviderInside peerB)
		{
			return ReplicateAll(peerA, peerB, true);
		}

		private int ReplicateAll(com.db4o.drs.inside.TestableReplicationProviderInside peerA
			, com.db4o.drs.inside.TestableReplicationProviderInside peerB, bool modifiedOnly
			)
		{
			com.db4o.drs.ReplicationSession replication = com.db4o.drs.Replication.Begin(peerA
				, peerB);
			com.db4o.ObjectSet it = modifiedOnly ? peerA.ObjectsChangedSinceLastReplication(typeof(
				com.db4o.drs.test.R0)) : peerA.GetStoredObjects(typeof(com.db4o.drs.test.R0));
			int replicated = 0;
			while (it.HasNext())
			{
				com.db4o.drs.test.R0 r0 = (com.db4o.drs.test.R0)it.Next();
				replication.Replicate(r0);
				replicated++;
			}
			replication.Commit();
			EnsureCount(peerA, LINKERS);
			EnsureCount(peerB, LINKERS);
			return replicated;
		}

		private void ReplicateNoneModified(com.db4o.drs.inside.TestableReplicationProviderInside
			 peerA, com.db4o.drs.inside.TestableReplicationProviderInside peerB)
		{
			Db4oUnit.Assert.IsTrue(Replicate(peerA, peerB) == 0);
		}

		private void ReplicateR4(com.db4o.drs.inside.TestableReplicationProviderInside peerA
			, com.db4o.drs.inside.TestableReplicationProviderInside peerB)
		{
			int replicatedObjectsCount = ReplicateAll(peerA, peerB, true);
			Db4oUnit.Assert.IsTrue(replicatedObjectsCount == LINKERS);
		}

		private void SetFieldsToNull(object _object, com.db4o.reflect.ReflectClass claxx)
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
				field.Set(_object, null);
			}
			com.db4o.reflect.ReflectClass superclass = claxx.GetSuperclass();
			if (superclass == null)
			{
				return;
			}
			SetFieldsToNull(_object, superclass);
		}

		public virtual void Test()
		{
			ActualTest();
		}

		protected virtual void ActualTest()
		{
			Init(A().Provider());
			EnsureCount(A().Provider(), LINKERS);
			CopyAllToB(A().Provider(), B().Provider());
			ReplicateNoneModified(A().Provider(), B().Provider());
			ModifyR4(A().Provider());
			EnsureR4Different(A().Provider(), B().Provider());
			ReplicateR4(A().Provider(), B().Provider());
			EnsureR4Same(A().Provider(), B().Provider());
		}
	}
}

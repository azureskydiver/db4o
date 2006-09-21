namespace com.db4o.drs.test
{
    public class ListTest : com.db4o.drs.test.DrsTestCase
    {
        public virtual void Test()
        {
            ActualTest();
        }

        protected virtual void ActualTest()
        {
            StoreListToProviderA();
            ReplicateAllToProviderBFirstTime();
            ModifyInProviderB();
            ReplicateAllStep2();
            AddElementInProviderA();
            ReplicateHolderStep3();
        }

        private void StoreListToProviderA()
        {
            com.db4o.drs.test.ListHolder lh = CreateHolder();
            com.db4o.drs.test.ListContent lc1 = new com.db4o.drs.test.ListContent("c1");
            com.db4o.drs.test.ListContent lc2 = new com.db4o.drs.test.ListContent("c2");
            lh.Add(lc1);
            lh.Add(lc2);
            A().Provider().StoreNew(lh);
            A().Provider().Commit();
            EnsureContent(A().Provider(), new string[] { "h1" }, new string[] { "c1", "c2" });
        }

        protected virtual com.db4o.drs.test.ListHolder CreateHolder()
        {
            com.db4o.drs.test.ListHolder lh = new com.db4o.drs.test.ListHolder("h1");
            lh.SetList(new System.Collections.ArrayList());
            return lh;
        }

        private void ReplicateAllToProviderBFirstTime()
        {
            ReplicateAll(A().Provider(), B().Provider());
            EnsureContent(A().Provider(), new string[] { "h1" }, new string[] { "c1", "c2" });
            EnsureContent(B().Provider(), new string[] { "h1" }, new string[] { "c1", "c2" });
        }

        private void ModifyInProviderB()
        {
            com.db4o.drs.test.ListHolder lh = (com.db4o.drs.test.ListHolder)GetOneInstance(B().Provider(), typeof(com.db4o.drs.test.ListHolder));
            lh.SetName("h2");
            com.db4o.drs.test.ListContent lc1 = (com.db4o.drs.test.ListContent)lh.GetList()[0];//.Get(0);
            com.db4o.drs.test.ListContent lc2 = (com.db4o.drs.test.ListContent)lh.GetList()[1];//.Get(1);
            lc1.SetName("co1");
            lc2.SetName("co2");
            B().Provider().Update(lc1);
            B().Provider().Update(lc2);
            B().Provider().Update(lh.GetList());
            B().Provider().Update(lh);
            B().Provider().Commit();
            EnsureContent(B().Provider(), new string[] { "h2" }, new string[] { "co1", "co2" }
                );
        }

        private void ReplicateAllStep2()
        {
            ReplicateAll(B().Provider(), A().Provider());
            EnsureContent(B().Provider(), new string[] { "h2" }, new string[] { "co1", "co2" }
                );
            EnsureContent(A().Provider(), new string[] { "h2" }, new string[] { "co1", "co2" }
                );
        }

        private void AddElementInProviderA()
        {
            com.db4o.drs.test.ListHolder lh = (com.db4o.drs.test.ListHolder)GetOneInstance(A(
                ).Provider(), typeof(com.db4o.drs.test.ListHolder));
            lh.SetName("h3");
            com.db4o.drs.test.ListContent lc3 = new com.db4o.drs.test.ListContent("co3");
            A().Provider().StoreNew(lc3);
            lh.GetList().Add(lc3);
            A().Provider().Update(lh.GetList());
            A().Provider().Update(lh);
            A().Provider().Commit();
            EnsureContent(A().Provider(), new string[] { "h3" }, new string[] { "co1", "co2", 
				"co3" });
        }

        private void ReplicateHolderStep3()
        {
            ReplicateClass(A().Provider(), B().Provider(), typeof(com.db4o.drs.test.ListHolder
                ));
            EnsureContent(A().Provider(), new string[] { "h3" }, new string[] { "co1", "co2", 
				"co3" });
            EnsureContent(B().Provider(), new string[] { "h3" }, new string[] { "co1", "co2", 
				"co3" });
        }

        private void EnsureContent(com.db4o.drs.inside.TestableReplicationProviderInside
            provider, string[] holderNames, string[] contentNames)
        {
            int holderCount = holderNames.Length;
            EnsureInstanceCount(provider, typeof(com.db4o.drs.test.ListHolder), holderCount);
            int i = 0;
            com.db4o.ObjectSet objectSet = provider.GetStoredObjects(typeof(com.db4o.drs.test.ListHolder
                ));
            while (objectSet.HasNext())
            {
                com.db4o.drs.test.ListHolder lh = (com.db4o.drs.test.ListHolder)objectSet.Next();
                Db4oUnit.Assert.AreEqual(holderNames[i], lh.GetName());
                System.Collections.IList list = lh.GetList();
                for (int j = 0; j < contentNames.Length; j++)
                {
                    com.db4o.drs.test.ListContent lc = (com.db4o.drs.test.ListContent)list[j];//.Get(j);
                    string name = lc.GetName();
                    Db4oUnit.Assert.AreEqual(contentNames[j], name);
                }
            }
        }
    }
}

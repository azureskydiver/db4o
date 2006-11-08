namespace Db4objects.Db4o.Drs.Test
{
    public class ListTest : Db4objects.Db4o.Drs.Test.DrsTestCase
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
            Db4objects.Db4o.Drs.Test.ListHolder lh = CreateHolder();
            Db4objects.Db4o.Drs.Test.ListContent lc1 = new Db4objects.Db4o.Drs.Test.ListContent("c1");
            Db4objects.Db4o.Drs.Test.ListContent lc2 = new Db4objects.Db4o.Drs.Test.ListContent("c2");
            lh.Add(lc1);
            lh.Add(lc2);
            A().Provider().StoreNew(lh);
            A().Provider().Commit();
            EnsureContent(A().Provider(), new string[] { "h1" }, new string[] { "c1", "c2" });
        }

        protected virtual Db4objects.Db4o.Drs.Test.ListHolder CreateHolder()
        {
            Db4objects.Db4o.Drs.Test.ListHolder lh = new Db4objects.Db4o.Drs.Test.ListHolder("h1");
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
            Db4objects.Db4o.Drs.Test.ListHolder lh = (Db4objects.Db4o.Drs.Test.ListHolder)GetOneInstance(B().Provider(), typeof(Db4objects.Db4o.Drs.Test.ListHolder));
            lh.SetName("h2");
            Db4objects.Db4o.Drs.Test.ListContent lc1 = (Db4objects.Db4o.Drs.Test.ListContent)lh.GetList()[0];//.Get(0);
            Db4objects.Db4o.Drs.Test.ListContent lc2 = (Db4objects.Db4o.Drs.Test.ListContent)lh.GetList()[1];//.Get(1);
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
            Db4objects.Db4o.Drs.Test.ListHolder lh = (Db4objects.Db4o.Drs.Test.ListHolder)GetOneInstance(A(
                ).Provider(), typeof(Db4objects.Db4o.Drs.Test.ListHolder));
            lh.SetName("h3");
            Db4objects.Db4o.Drs.Test.ListContent lc3 = new Db4objects.Db4o.Drs.Test.ListContent("co3");
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
            ReplicateClass(A().Provider(), B().Provider(), typeof(Db4objects.Db4o.Drs.Test.ListHolder
                ));
            EnsureContent(A().Provider(), new string[] { "h3" }, new string[] { "co1", "co2", 
				"co3" });
            EnsureContent(B().Provider(), new string[] { "h3" }, new string[] { "co1", "co2", 
				"co3" });
        }

        private void EnsureContent(Db4objects.Db4o.Drsinside.TestableReplicationProviderInside
            provider, string[] holderNames, string[] contentNames)
        {
            int holderCount = holderNames.Length;
            EnsureInstanceCount(provider, typeof(Db4objects.Db4o.Drs.Test.ListHolder), holderCount);
            int i = 0;
            Db4objects.Db4o.ObjectSet objectSet = provider.GetStoredObjects(typeof(Db4objects.Db4o.Drs.Test.ListHolder
                ));
            while (objectSet.HasNext())
            {
                Db4objects.Db4o.Drs.Test.ListHolder lh = (Db4objects.Db4o.Drs.Test.ListHolder)objectSet.Next();
                Db4oUnit.Assert.AreEqual(holderNames[i], lh.GetName());
                System.Collections.IList list = lh.GetList();
                for (int j = 0; j < contentNames.Length; j++)
                {
                    Db4objects.Db4o.Drs.Test.ListContent lc = (Db4objects.Db4o.Drs.Test.ListContent)list[j];//.Get(j);
                    string name = lc.GetName();
                    Db4oUnit.Assert.AreEqual(contentNames[j], name);
                }
            }
        }
    }
}

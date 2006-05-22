using System;
using System.Collections;

using com.db4o.query;

namespace com.db4o.test {

    public class HoldsAnArrayList {

        public ArrayList something = new ArrayList();

        public HoldsAnArrayList (){
            something.AddRange(new char[] {'1', '2'});
        }

        public void Configure(){

            // Both of the following configuration settings work.
            // They can be used alternatively.

            Db4o.Configure().UpdateDepth(3);

            // Db4o.Configure().ObjectClass(typeof(HoldsAnArrayList)).CascadeOnUpdate(true);
        }

        public void Store(){
            Tester.Store(new HoldsAnArrayList());
        }

        public void Test(){
            Query q = Tester.Query();
            q.Constrain(typeof(HoldsAnArrayList));
            ObjectSet objectSet = q.Execute();
            while(objectSet.HasNext()){
                HoldsAnArrayList obj = (HoldsAnArrayList)objectSet.Next();
                obj.something.Add('3');
                Tester.Store(obj);

                Tester.ReOpen();
                q = Tester.Query();
                q.Constrain(typeof(HoldsAnArrayList));
                objectSet = q.Execute();
                while(objectSet.HasNext()){
                    HoldsAnArrayList haal = (HoldsAnArrayList)objectSet.Next();
                    Tester.Ensure(haal.something.Count > 2);
                }

            }
        }
    }
}
/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;

using com.db4o.query;

namespace com.db4o.test {

    public class DerivedFromArrayList : ArrayList {
        
        public void StoreOne(){
            Add("One");
            Add("Two");
        }

        public void Test(){
            Query q = Tester.Query();
            q.Constrain(typeof(DerivedFromArrayList));
            ObjectSet objectSet = q.Execute();
            while(objectSet.HasNext()){
                DerivedFromArrayList dal = (DerivedFromArrayList)objectSet.Next();
                dal.Add("Three");
                Tester.Store(dal);
            }
            Tester.ReOpen();
            q = Tester.Query();
            q.Constrain(typeof(DerivedFromArrayList));
            objectSet = q.Execute();
            while(objectSet.HasNext()){
                DerivedFromArrayList dal = (DerivedFromArrayList)objectSet.Next();
                Tester.Ensure(dal.Count > 2);
            }
        }
    }
}

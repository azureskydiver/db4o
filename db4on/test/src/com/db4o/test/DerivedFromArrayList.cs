/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;

using com.db4o.query;

namespace com.db4o.test {

    public class DerivedFromArrayList : ArrayList {
        
        public void storeOne(){
            Add("One");
            Add("Two");
        }

        public void test(){
            Query q = Tester.query();
            q.constrain(typeof(DerivedFromArrayList));
            ObjectSet objectSet = q.execute();
            while(objectSet.hasNext()){
                DerivedFromArrayList dal = (DerivedFromArrayList)objectSet.next();
                dal.Add("Three");
                Tester.store(dal);
            }
            Tester.reOpen();
            q = Tester.query();
            q.constrain(typeof(DerivedFromArrayList));
            objectSet = q.execute();
            while(objectSet.hasNext()){
                DerivedFromArrayList dal = (DerivedFromArrayList)objectSet.next();
                Tester.ensure(dal.Count > 2);
            }
        }
    }
}

/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4o.query;

namespace com.db4o.test
{

	public class UnknownClass
	{

        public void Store(){
            Tester.Store(new Atom());

//            Tester.Store(new UnknownClass());
//            Tester.Commit();
//            Query q = Tester.Query();
//            q.Constrain(typeof(UnknownClass));
//            ObjectSet objectSet = q.Execute();
//            while(objectSet.HasNext()){
//                Tester.Delete(objectSet.Next());
//            }
//            Tester.Commit();

        }
    
        public void Test(){
            Query q = Tester.Query();
            q.Constrain(typeof(UnknownClass));
            Tester.Ensure(q.Execute().Size() == 0);
        }

    }
}

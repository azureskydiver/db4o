/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4o.query;

namespace com.db4o.test
{

	public class UnknownClass
	{

        public void store(){
            Test.store(new Atom());

//            Test.store(new UnknownClass());
//            Test.commit();
//            Query q = Test.query();
//            q.constrain(typeof(UnknownClass));
//            ObjectSet objectSet = q.execute();
//            while(objectSet.hasNext()){
//                Test.delete(objectSet.next());
//            }
//            Test.commit();

        }
    
        public void test(){
            Query q = Test.query();
            q.constrain(typeof(UnknownClass));
            Test.ensure(q.execute().size() == 0);
        }

    }
}

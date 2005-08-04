/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4o.query;

namespace com.db4o.test
{

	public class UnknownClass
	{

        public void store(){
            Tester.store(new Atom());

//            Tester.store(new UnknownClass());
//            Tester.commit();
//            Query q = Tester.query();
//            q.constrain(typeof(UnknownClass));
//            ObjectSet objectSet = q.execute();
//            while(objectSet.hasNext()){
//                Tester.delete(objectSet.next());
//            }
//            Tester.commit();

        }
    
        public void test(){
            Query q = Tester.query();
            q.constrain(typeof(UnknownClass));
            Tester.ensure(q.execute().size() == 0);
        }

    }
}

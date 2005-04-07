/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4o.query;

namespace com.db4o.test {

    public class Circular1 {
    
        public void store(){
            Test.store(new C1C());
        }
    
        public void test(){
            Query q = Test.query();
            q.constrain(typeof(C1C));
            Test.ensure(q.execute().size() > 0);
        }
    
    
    }

    public class C1P{
        C1C c;
    }

    public class C1C : C1P{
    }
}

  

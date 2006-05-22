/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4o.query;

namespace com.db4o.test {

    public class Circular1 {
    
        public void Store(){
            Tester.Store(new C1C());
        }
    
        public void Test(){
            Query q = Tester.Query();
            q.Constrain(typeof(C1C));
            Tester.Ensure(q.Execute().Size() > 0);
        }
    
    
    }

    public class C1P{
        C1C c;
    }

    public class C1C : C1P{
    }
}

  

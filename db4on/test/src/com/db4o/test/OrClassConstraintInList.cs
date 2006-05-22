/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using com.db4o.query;

namespace com.db4o.test {

    public class OrClassConstraintInList {

        int cnt;
        IList list;
    
        public void Configure(){
            Db4o.Configure().ObjectClass(this).ObjectField("cnt").Indexed(true);
        }
    
        public void Store(){
            OrClassConstraintInList occ = new OrClassConstraintInList();
            occ.list = Tester.ObjectContainer().Collections().NewLinkedList();
            occ.list.Add(new Atom());
            Tester.Store(occ);
            occ = new OrClassConstraintInList();
            occ.list = Tester.ObjectContainer().Collections().NewLinkedList();
            occ.cnt = 1;
            Tester.Store(occ);
        }
    
        public void Test(){
            Query q = Tester.Query();
            q.Constrain(typeof(OrClassConstraintInList));
            Constraint c1 = q.Descend("list").Constrain(typeof(Atom));
            Constraint c2 = q.Descend("cnt").Constrain(1);
            c1.Or(c2);
            Tester.Ensure(q.Execute().Size() == 2);
        }
    }
}

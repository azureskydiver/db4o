/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using com.db4o.query;

namespace com.db4o.test {

    public class OrClassConstraintInList {

        int cnt;
        IList list;
    
        public void configure(){
            Db4o.configure().objectClass(this).objectField("cnt").indexed(true);
        }
    
        public void store(){
            OrClassConstraintInList occ = new OrClassConstraintInList();
            occ.list = Test.objectContainer().collections().newLinkedList();
            occ.list.Add(new Atom());
            Test.store(occ);
            occ = new OrClassConstraintInList();
            occ.list = Test.objectContainer().collections().newLinkedList();
            occ.cnt = 1;
            Test.store(occ);
        }
    
        public void test(){
            Query q = Test.query();
            q.constrain(typeof(OrClassConstraintInList));
            Constraint c1 = q.descend("list").constrain(typeof(Atom));
            Constraint c2 = q.descend("cnt").constrain(1);
            c1.or(c2);
            Test.ensure(q.execute().size() == 2);
        }
    }
}

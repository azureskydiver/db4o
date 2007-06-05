/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
package com.db4o.test;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;


public class PeekPersisted {
    
    public String name;
    
    public PeekPersisted child;
    
    public void storeOne(){
        PeekPersisted current = this;
        current.name = "1";
        for (int i = 2; i < 11; i++) {
            current.child = new PeekPersisted();
            current.child.name = "" + i;
            current = current.child;
        }
    }
    
    public void test(){
        Query q = Test.query();
        q.constrain(PeekPersisted.class);
        q.descend("name").constrain("1");
        ObjectSet objectSet = q.execute();
        PeekPersisted pp = (PeekPersisted)objectSet.next();
        for (int i = 0; i < 10; i++) {
            peek(pp, i);
        }
    }
    
    private void peek(PeekPersisted original, int depth){
        ExtObjectContainer oc = Test.objectContainer();
        PeekPersisted peeked = (PeekPersisted )oc.peekPersisted(original, depth, true);
        Test.ensure(peeked != null);
        Test.ensure(! oc.isStored(peeked));
        for (int i = 0; i <= depth; i++) {
            Test.ensure(peeked != null);
            Test.ensure(! oc.isStored(peeked));
            peeked = peeked.child;
        }
        Test.ensure(peeked == null);
    }

}

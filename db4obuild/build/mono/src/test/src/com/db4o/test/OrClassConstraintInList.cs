/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */

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

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
using com.db4o.query;

namespace com.db4o.test
{
    /// <summary>
    /// Summary description for IndexedByIdentity.
    /// </summary>
    public class IndexedByIdentity
    {
        Atom atom;
    
        static int COUNT = 10;
    
        public void configure()
        {
            Db4o.configure().objectClass(this).objectField("atom").indexed(true);
        }
    
        public void store()
        {
            for (int i = 0; i < COUNT; i++) 
            {
                IndexedByIdentity ibi = new IndexedByIdentity();
                ibi.atom = new Atom("ibi" + i);
                Test.store(ibi);
            } 
        }
    
        public void test()
        {
        
            for (int i = 0; i < COUNT; i++) 
            {
                Query q = Test.query();
                q.constrain(typeof(Atom));
                q.descend("name").constrain("ibi" + i);
                ObjectSet objectSet = q.execute();
                Atom child = (Atom)objectSet.next();
                // child.name = "rünzelbrünft";
                q = Test.query();
                q.constrain(typeof(IndexedByIdentity));
                q.descend("atom").constrain(child).identity();
                objectSet = q.execute();
                Test.ensure(objectSet.size() == 1);
                IndexedByIdentity ibi = (IndexedByIdentity)objectSet.next();
                Test.ensure(ibi.atom == child);
            }
        }
    
    
    }
}

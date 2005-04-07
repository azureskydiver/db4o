/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

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

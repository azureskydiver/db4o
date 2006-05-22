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
    
        public void Configure()
        {
            Db4o.Configure().ObjectClass(this).ObjectField("atom").Indexed(true);
        }
    
        public void Store()
        {
            for (int i = 0; i < COUNT; i++) 
            {
                IndexedByIdentity ibi = new IndexedByIdentity();
                ibi.atom = new Atom("ibi" + i);
                Tester.Store(ibi);
            } 
        }
    
        public void Test()
        {
        
            for (int i = 0; i < COUNT; i++) 
            {
                Query q = Tester.Query();
                q.Constrain(typeof(Atom));
                q.Descend("name").Constrain("ibi" + i);
                ObjectSet objectSet = q.Execute();
                Atom child = (Atom)objectSet.Next();
                // child.name = "rünzelbrünft";
                q = Tester.Query();
                q.Constrain(typeof(IndexedByIdentity));
                q.Descend("atom").Constrain(child).Identity();
                objectSet = q.Execute();
                Tester.Ensure(objectSet.Size() == 1);
                IndexedByIdentity ibi = (IndexedByIdentity)objectSet.Next();
                Tester.Ensure(ibi.atom == child);
            }
        }
    
    
    }
}

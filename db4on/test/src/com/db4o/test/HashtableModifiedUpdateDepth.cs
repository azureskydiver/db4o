/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using com.db4o.query;

namespace com.db4o.test
{
	public class HashtableModifiedUpdateDepth
	{
        Hashtable ht;

        public void configure() 
        {
            Db4o.configure().updateDepth(int.MaxValue);
        }

        public void storeOne() 
        {
            ht = new Hashtable();
            ht["hi"] = "five";
        }

        public void testOne() 
        {
            Test.ensure(ht["hi"].Equals("five"));
            ht["hi"] = "six";
            Test.store(this);
            Test.reOpen();
            Query q = Test.query();
            q.constrain(this.GetType());
            HashtableModifiedUpdateDepth hmud = 
                (HashtableModifiedUpdateDepth) q.execute().next();
            Test.ensure(hmud.ht["hi"].Equals("six"));
        }

	}
}

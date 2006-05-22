/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using com.db4o.query;

namespace com.db4o.test
{
	public class HashtableModifiedUpdateDepth
	{
        Hashtable ht;

        public void Configure() 
        {
            Db4o.Configure().UpdateDepth(int.MaxValue);
        }

        public void StoreOne() 
        {
            ht = new Hashtable();
            ht["hi"] = "five";
        }

        public void TestOne() 
        {
            Tester.Ensure(ht["hi"].Equals("five"));
            ht["hi"] = "six";
            Tester.Store(this);
            Tester.ReOpen();
            Query q = Tester.Query();
            q.Constrain(this.GetType());
            HashtableModifiedUpdateDepth hmud = 
                (HashtableModifiedUpdateDepth) q.Execute().Next();
            Tester.Ensure(hmud.ht["hi"].Equals("six"));
        }

	}
}

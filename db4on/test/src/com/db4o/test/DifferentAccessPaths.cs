/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4o.query;

namespace com.db4o.test
{
	public class DifferentAccessPaths
	{
        String foo;

        public void Store(){
            Tester.DeleteAllInstances(this);
            DifferentAccessPaths dap = new DifferentAccessPaths();
            dap.foo = "hi";
            Tester.Store(dap);
            dap = new DifferentAccessPaths();
            dap.foo = "hi too";
            Tester.Store(dap);
        }

        public void Test(){
            DifferentAccessPaths dap = Query();
            for(int i = 0; i < 10; i ++){
                Tester.Ensure(dap == Query());
            }
            Tester.ObjectContainer().Purge(dap);
            Tester.Ensure(dap != Query());
        }

        private DifferentAccessPaths Query(){
            Query q = Tester.Query();
            q.Constrain(typeof(DifferentAccessPaths));
            q.Descend("foo").Constrain("hi");
            ObjectSet os = q.Execute();
            DifferentAccessPaths dap = (DifferentAccessPaths)os.Next();
            return dap;
        }

	}
}

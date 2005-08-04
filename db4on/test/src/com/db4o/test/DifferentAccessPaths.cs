/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4o.query;

namespace com.db4o.test
{
	public class DifferentAccessPaths
	{
        String foo;

        public void store(){
            Tester.deleteAllInstances(this);
            DifferentAccessPaths dap = new DifferentAccessPaths();
            dap.foo = "hi";
            Tester.store(dap);
            dap = new DifferentAccessPaths();
            dap.foo = "hi too";
            Tester.store(dap);
        }

        public void test(){
            DifferentAccessPaths dap = query();
            for(int i = 0; i < 10; i ++){
                Tester.ensure(dap == query());
            }
            Tester.objectContainer().purge(dap);
            Tester.ensure(dap != query());
        }

        private DifferentAccessPaths query(){
            Query q = Tester.query();
            q.constrain(typeof(DifferentAccessPaths));
            q.descend("foo").constrain("hi");
            ObjectSet os = q.execute();
            DifferentAccessPaths dap = (DifferentAccessPaths)os.next();
            return dap;
        }

	}
}

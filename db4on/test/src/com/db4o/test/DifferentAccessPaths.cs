/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4o.query;

namespace com.db4o.test
{
	public class DifferentAccessPaths
	{
        String foo;

        public void store(){
            Test.deleteAllInstances(this);
            DifferentAccessPaths dap = new DifferentAccessPaths();
            dap.foo = "hi";
            Test.store(dap);
            dap = new DifferentAccessPaths();
            dap.foo = "hi too";
            Test.store(dap);
        }

        public void test(){
            DifferentAccessPaths dap = query();
            for(int i = 0; i < 10; i ++){
                Test.ensure(dap == query());
            }
            Test.objectContainer().purge(dap);
            Test.ensure(dap != query());
        }

        private DifferentAccessPaths query(){
            Query q = Test.query();
            q.constrain(typeof(DifferentAccessPaths));
            q.descend("foo").constrain("hi");
            ObjectSet os = q.execute();
            DifferentAccessPaths dap = (DifferentAccessPaths)os.next();
            return dap;
        }

	}
}

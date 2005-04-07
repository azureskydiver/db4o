/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.IO;
using com.db4o;
using com.db4o.query;

namespace com.db4o.test {
    public class NoInstanceStored {

        public String name;

        public void test(){
            Query q = Test.query();
            q.constrain(typeof(NoInstanceStored));
            q.descend("name").constrain("hi");
            ObjectSet objectSet = q.execute();
            Test.ensure(objectSet.size() == 0);
        }

        public static void Main(String[] args) {
            ObjectContainer objectContainer = Db4o.openFile("nis.yap");
            objectContainer.set(new Atom("jj"));
            objectContainer.close();
            objectContainer = Db4o.openFile("nis.yap");
            Query q = objectContainer.query();
            q.constrain(typeof(NoInstanceStored));
            q.descend("name").constrain("hi");
            ObjectSet objectSet = q.execute();
            Console.WriteLine(objectSet.size());
            objectContainer.close();
        }
    }
}

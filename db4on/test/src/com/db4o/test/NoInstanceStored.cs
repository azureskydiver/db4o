/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.IO;
using com.db4o;
using com.db4o.query;

namespace com.db4o.test {
    public class NoInstanceStored {

        public String name;

        public void Test(){
            Query q = Tester.Query();
            q.Constrain(typeof(NoInstanceStored));
            q.Descend("name").Constrain("hi");
            ObjectSet objectSet = q.Execute();
            Tester.Ensure(objectSet.Size() == 0);
        }

        public static void Main(String[] args) {
            ObjectContainer objectContainer = Db4o.OpenFile("nis.yap");
            objectContainer.Set(new Atom("jj"));
            objectContainer.Close();
            objectContainer = Db4o.OpenFile("nis.yap");
            Query q = objectContainer.Query();
            q.Constrain(typeof(NoInstanceStored));
            q.Descend("name").Constrain("hi");
            ObjectSet objectSet = q.Execute();
            Console.WriteLine(objectSet.Size());
            objectContainer.Close();
        }
    }
}

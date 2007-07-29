using System;
using System.Collections.Generic;
using System.Text;

using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Query;

namespace Test
{
    class Program
    {
        static void Main(string[] args)
        {
            IObjectContainer db = Db4oFactory.OpenClient("localhost", 0xdb40, "db4o", "db4o");

            TestClass o;

            o = new TestClass();

            o.data = "BLAALA";

            db.Set(o);

            db.Commit();
            Db4objects.Db4o.Query.IQuery q = db.Query();

            q.Constrain(typeof(TestClass));
            q.Descend("data").Constrain("BLAALA");
            IObjectSet result = q.Execute();

            Console.WriteLine(result.Count); //Writes "0"
            while (result.HasNext()) {
                Console.WriteLine(result.Next());
            }

            db.Close();
        }
    }
}

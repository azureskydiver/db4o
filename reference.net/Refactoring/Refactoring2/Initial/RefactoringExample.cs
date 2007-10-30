/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */
using System.IO;
using Db4objects.Db4o;


namespace Db4objects.Db4odoc.Refactoring.Initial
{
    class RefactoringExample
    {

        private const string Db4oFileName = "reference.db4o";

        public static void Main(string[] args)
        {
            StoreData();
            ReadData();
        }

        public static void StoreData()
        {
            File.Delete(Db4oFileName);
            IObjectContainer container = Db4oFactory.OpenFile(Db4oFileName);
            try
            {
                A a = new A();
                a.name = "A class";
                container.Set(a);

                B b = new B();
                b.name = "B class";
                b.number = 1;
                container.Set(b);

                C c = new C();
                c.name = "C class";
                c.number = 2;
                container.Set(c);
            }
            finally
            {
                container.Close();
            }
        }
        // end StoreData

        public static void ReadData()
        {
            IObjectContainer container = Db4oFactory.OpenFile(Db4oFileName);
            try
            {
                IObjectSet result = container.Get(new A());
                System.Console.WriteLine("A class: ");
                ListResult(result);

                result = container.Get(new B());
                System.Console.WriteLine();
                System.Console.WriteLine("B class: ");
                ListResult(result);

                result = container.Get(new C());
                System.Console.WriteLine();
                System.Console.WriteLine("C class: ");
                ListResult(result);
            }
            finally
            {
                container.Close();
            }
        }
        // end ReadData

        private static void ListResult(IObjectSet result)
        {
            System.Console.WriteLine(result.Count);
            for (int i = 0; i < result.Count; i++)
            {
                System.Console.WriteLine(result[i]);
            }
        }
        // end ListResult

    }

}


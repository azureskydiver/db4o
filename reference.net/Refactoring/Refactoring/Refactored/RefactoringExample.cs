/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */
using System.IO;
using Db4objects.Db4o;


namespace Db4objects.Db4odoc.Refactoring.Refactored
{
    class RefactoringExample
    {

        private const string Db4oFileName = "reference.db4o";

        public static void Main(string[] args)
        {
            ReadData();
        }


        public static void ReadData()
        {
            IObjectContainer container = Db4oFactory.OpenFile(Db4oFileName);
            try
            {
                IObjectSet result = container.Get(new D());
                System.Console.WriteLine();
                System.Console.WriteLine("D class: ");
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


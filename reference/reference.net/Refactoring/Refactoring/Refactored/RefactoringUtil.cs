/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */
using Db4objects.Db4o;
using Db4objects.Db4odoc.Refactoring.Initial;

namespace Db4objects.Db4odoc.Refactoring.Refactored
{
    class RefactoringUtil
    {

        private const string Db4oFileName = "reference.db4o";

        public static void Main(string[] args)
        {
            MoveValues();
        }

        public static void MoveValues()
        {
            IObjectContainer container = Db4oFactory.OpenFile(Db4oFileName);
            try
            {
                // querying for B will bring back B and C values
                IObjectSet result = container.Get(new B());
                for (int i = 0; i < result.Count; i++)
                {
                    B b = (B)result[i];
                    D d = new D();
                    d.name = b.name;
                    d.number = b.number;
                    container.Delete(b);
                    container.Set(d);
                }

            }
            finally
            {
                container.Close();
                System.Console.WriteLine("Done");
            }
        }
        // end moveValues


    }

}

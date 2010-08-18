using Db4objects.Db4o;

namespace Db4oDoc.Code.Basics
{
    public class Db4oBasics
    {
        public static void Main(string[] args)
        {
            OpenAndCloseTheContainer();

            using (IObjectContainer container = Db4oEmbedded.OpenFile("databaseFile.db4o"))
            {
                StoreObject(container);
                DeleteObject(container);
            }
        }

        private static void StoreObject(IObjectContainer container)
        {
            // #example: Store a object
            Pilot pilot = new Pilot("Joe");
            container.Store(pilot);
            // #end example
        }

        private static void DeleteObject(IObjectContainer container)
        {
            Pilot pilot = container.Query<Pilot>()[0];
            // #example: Delete a object
            container.Delete(pilot);
            // #end example
        }

        private static void OpenAndCloseTheContainer()
        {
            // #example: Open the object container to use the database
            using (IObjectContainer container = Db4oEmbedded.OpenFile("databaseFile.db4o"))
            {
                // use the object container
            }
            // #end example
        }
    }

    internal class Pilot
    {
        private string name;

        public Pilot(string name)
        {
            this.name = name;
        }

        public string Name
        {
            get { return name; }
            set { name = value; }
        }
    }
}
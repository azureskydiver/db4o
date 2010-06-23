using Db4objects.Db4o;
using Db4objects.Db4o.CS;

namespace Db4oDoc.Code.Container.Sessions
{
    public class Db4oSessions
    {
        private const string DatabaseFileName = "database.db4o";


        public void Sessions()
        {
            // #example: Session object container
            IObjectContainer rootContainer = Db4oEmbedded.OpenFile(DatabaseFileName);

            // open the db4o-session. For example at the beginning for a web-request
            using (IObjectContainer session = rootContainer.Ext().OpenSession())
            {
                // do the operations on the session-container
                session.Store(new Person("Joe"));
            }
            // #end example

            rootContainer.Dispose();
        }

        public void EmbeddedClient()
        {
            // #example: Embedded client
            IObjectServer server = Db4oClientServer.OpenServer(DatabaseFileName, 0);

            // open the db4o-embedded client. For example at the beginning for a web-request
            using (IObjectContainer container = server.OpenClient())
            {
                // do the operations on the session-container
                container.Store(new Person("Joe"));
            }
            // #end example

            server.Dispose();
        }


        private class Person
        {
            private string name;

            public Person(string name)
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
}
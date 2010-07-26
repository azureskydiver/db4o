using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.IO;

namespace Db4oDoc.Silverlight.Model
{
    public class QueriesInSilverlight
    {
        private void SodaQuery()
        {
            IEmbeddedConfiguration configuration = Db4oEmbedded.NewConfiguration();
            configuration.File.Storage = new IsolatedStorageStorage();

            IObjectContainer container = Db4oEmbedded.OpenFile(configuration, "database.db4o");
            // #example: Queries in Silverlight
            var query = container.Query();
            query.Constrain(typeof(Person));
            query.Descend("FirstName").Constrain("Roman").Contains();

            IObjectSet queryResult = query.Execute();
            foreach (Person person in queryResult)
            {
                // do something with the persons
            }
            // #end example

            
        }
    }
}
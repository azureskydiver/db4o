using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.IO;

namespace silverlight.Code.configuration.io
{
    public class IOExamples
    {
        public void useIsolatedStorage()
        {
            // #example: use the isolated storage on silverlight
            IEmbeddedConfiguration configuration = Db4oEmbedded.NewConfiguration();
            configuration.File.Storage = new IsolatedStorageStorage();
            // #end example

            IObjectContainer container = Db4oEmbedded.OpenFile(configuration, "database.db4o");
        }
    }
}
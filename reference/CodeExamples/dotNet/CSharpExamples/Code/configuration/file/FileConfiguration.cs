using Db4objects.Db4o;
using Db4objects.Db4o.Config;

namespace Db4oDoc.Code.Configuration.File
{
    public class FileConfiguration
    {
        public static void AsynchronousSync()
        {
            // #example: Allow asynchronous synchronisation of the file-flushes
            IEmbeddedConfiguration configuration = Db4oEmbedded.NewConfiguration();
            configuration.File.AsynchronousSync(true);
            // #end example
            IObjectContainer container = Db4oEmbedded.OpenFile(configuration, "database.db4o");
            container.Close();

        }

        public static void ChangeBlobPath()
        {
            // #example: Configure the blob-path
            IEmbeddedConfiguration configuration = Db4oEmbedded.NewConfiguration();
            configuration.File.BlobPath = "myBlobDirectory";
            // #end example
            IObjectContainer container = Db4oEmbedded.OpenFile(configuration, "database.db4o");
            container.Close();

        }
        public static void ReserveSpace()
        {
            // #example: Configure the growth size
            IEmbeddedConfiguration configuration = Db4oEmbedded.NewConfiguration();
            configuration.File.DatabaseGrowthSize = 4096;
            // #end example
            IObjectContainer container = Db4oEmbedded.OpenFile(configuration, "database.db4o");
            container.Close();

        }
        public static void DisableCommitRecovers()
        {
            // #example: Disable commit recovery
            IEmbeddedConfiguration configuration = Db4oEmbedded.NewConfiguration();
            configuration.File.DisableCommitRecovery();
            // #end example
            IObjectContainer container = Db4oEmbedded.OpenFile(configuration, "database.db4o");
            container.Close();

        }
        public static void ReadOnlyMode()
        {
            // #example: Set read only mode
            IEmbeddedConfiguration configuration = Db4oEmbedded.NewConfiguration();
            configuration.File.ReadOnly = true;
            // #end example
            IObjectContainer container = Db4oEmbedded.OpenFile(configuration, "database.db4o");
            container.Close();

        }
        public static void RecoveryMode()
        {
            // #example: Enable recovery mode to open a corrupted database
            IEmbeddedConfiguration configuration = Db4oEmbedded.NewConfiguration();
            configuration.File.RecoveryMode = true;
            // #end example
            IObjectContainer container = Db4oEmbedded.OpenFile(configuration, "database.db4o");
            container.Close();
        }
        public static void ReserveStorageSpace()
        {
            // #example: Reserve storage space
            IEmbeddedConfiguration configuration = Db4oEmbedded.NewConfiguration();
            configuration.File.ReserveStorageSpace = 1024 * 1024;
            // #end example
            IObjectContainer container = Db4oEmbedded.OpenFile(configuration, "database.db4o");
            container.Close();
        }
    }

}
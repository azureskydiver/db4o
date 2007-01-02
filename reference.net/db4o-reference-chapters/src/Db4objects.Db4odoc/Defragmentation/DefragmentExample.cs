using System;
using Db4objects.Db4o.Defragment;

namespace Db4objects.Db4odoc.Defragmentation
{
    class DefragmentExample
    {

        public static void Main(string[] args)
        {
            RunDefragment();
        }
        // end Main

        public static void RunDefragment()
        {
            DefragmentConfig config = new DefragmentConfig("sample.yap", "sample.bap");
            config.ForceBackupDelete(true);
            config.StoredClassFilter(new AvailableTypeFilter());
            try
            {
                Defragment.Defrag(config);
            }
            catch (Exception ex)
            {
                System.Console.WriteLine(ex.Message);
            }
        }
        // end RunDefragment
    }
}

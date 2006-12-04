using System;

namespace Db4objects.Db4odoc
{
    class ExamplesRunner
    {
        public static void Main(string[] args)
        {
            //Use this to redirect console output to file
            //System.IO.FileInfo f = new System.IO.FileInfo("Console.txt");
            //System.IO.StreamWriter tex = f.CreateText();
            //System.Console.SetOut(tex);
            Db4objects.Db4odoc.ClientServer.DeepExample.Main(args);
            Db4objects.Db4odoc.ClientServer.TransactionExample.Main(args);
            Db4objects.Db4odoc.ClientServer.ClientServerExample.Main(args);
            Db4objects.Db4odoc.Indexes.IndexedExample.Main(args);
            Db4objects.Db4odoc.Diagnostics.DiagnosticExample.Main(args);
            Db4objects.Db4odoc.Blobs.BlobExample.Main(args);
            Db4objects.Db4odoc.Lists.CollectionExample.Main(args);
            Db4objects.Db4odoc.Reflections.ReflectorExample.Main(args);
            //Db4objects.Db4odoc .Reflections.ReflectorExample.TestReflector(); //should be commented for bulk example run
            Db4objects.Db4odoc.Debugging.DebugExample.Main(args);
            Db4objects.Db4odoc.Activating.ActivationExample.Main(args);
            Db4objects.Db4odoc.StaticFields.StaticFieldExample.Main(args);
            Db4objects.Db4odoc.UUIDs.UUIDExample.Main(args);
            Db4objects.Db4odoc.IOs.IOExample.Main(args);
            Db4objects.Db4odoc.ClientServer.ExtClientExample.Main(args);
            Db4objects.Db4odoc.MetaInfo.MetaInfoExample.Main(args);
            Db4objects.Db4odoc.Remote.RemoteExample.Main(args);
            Db4objects.Db4odoc.Refactoring.RefactoringExample.Main(args);
            Db4objects.Db4odoc.Persist.PeekPersistedExample.Main(args);
            Db4objects.Db4odoc.Identity.IdentityExample.Main(args);
            Db4objects.Db4odoc.Serializing.SerializeExample.Main(args);
            Db4objects.Db4odoc.Utility.UtilityExample.Main(args);
            Db4objects.Db4odoc.SelectivePersistence.MarkTransientExample.Main(args);
            Db4objects.Db4odoc.Querymode.QueryModesExample.Main(args);

        }
    }
}

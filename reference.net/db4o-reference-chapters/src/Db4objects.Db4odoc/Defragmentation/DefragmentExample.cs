/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;

using Db4objects.Db4o;
using Db4objects.Db4o.Defragment;

namespace Db4objects.Db4odoc.Defragmentation
{
    class DefragmentExample
    {
        private const string DbFile = "test.db4o";
        private const string BackupFile = "test.bap";

        public static void Main(string[] args)
        {
            CleanFilesForTesting();
            SimplestDefragment();
            CleanFilesForTesting();
            ConfiguredDefragment();
            CleanFilesForTesting();
            DefragmentWithListener();
        }
        // end Main

        private static void CleanFilesForTesting()
        {
            File.Delete("test.bap");
            File.Delete("test.db4o.backup");
        }
        // end CleanFilesForTesting

        private static void SimplestDefragment()
        {
            try
            {
                Defragment.Defrag(DbFile);
            }
            catch (Exception ex)
            {
                System.Console.WriteLine(ex.Message);
            }
        }
        // end SimplestDefragment

        private static void ConfiguredDefragment()
        {
            DefragmentConfig config = new DefragmentConfig(DbFile, BackupFile, new TreeIDMapping());
            config.ObjectCommitFrequency(5000);
            config.Db4oConfig(Db4oFactory.CloneConfiguration());
            config.ForceBackupDelete(true);
            config.UpgradeFile(DbFile + ".upg");
            try
            {
                Defragment.Defrag(config);
            }
            catch (Exception ex)
            {
                System.Console.WriteLine(ex.Message);
            }
        }
        // end ConfiguredDefragment

        private static void DefragmentWithListener()
        {
            DefragmentConfig config = new DefragmentConfig(DbFile, BackupFile);
            try
            {
                Defragment.Defrag(config, new DefragmentListener());
            }
            catch (Exception ex)
            {
                System.Console.WriteLine(ex.Message);
            }
        }
        // end DefragmentWithListener
    }

    public class DefragmentListener : IDefragmentListener
    {
        void IDefragmentListener.NotifyDefragmentInfo(DefragmentInfo info)
        {
            throw new System.Exception("The method or operation is not implemented.");
        }
    }
    // end DefragmentListener
}

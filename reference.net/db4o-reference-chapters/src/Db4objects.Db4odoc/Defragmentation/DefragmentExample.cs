/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using Db4objects.Db4o;
using Db4objects.Db4o.Defragment;

namespace Db4objects.Db4odoc.Defragmentation
{
    class DefragmentExample
    {
        private const string DB_FILE = "test.db4o";
        private const string BACKUP_FILE = "test.bap";

        public static void Main(string[] args)
        {
            SimplestDefragment();
            ConfiguredDefragment();
            DefragmentWithListener();
        }
        // end Main

        public static void SimplestDefragment()
        {
            try
            {
                Defragment.Defrag(DB_FILE);
            }
            catch (Exception ex)
            {
                System.Console.WriteLine(ex.Message);
            }
        }
        // end SimplestDefragment

        public static void ConfiguredDefragment()
        {
            DefragmentConfig config = new DefragmentConfig(DB_FILE, BACKUP_FILE , new TreeIDMapping());
            config.ObjectCommitFrequency(5000);
            config.Db4oConfig(Db4oFactory.CloneConfiguration());
            config.ForceBackupDelete(true);
            config.UpgradeFile(DB_FILE + ".upg");
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

        public static void DefragmentWithListener()
        {
            DefragmentConfig config = new DefragmentConfig(DB_FILE, BACKUP_FILE);
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

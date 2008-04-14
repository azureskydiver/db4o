/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */
using System;
using System.Collections;
using System.Text;
using System.IO;

using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.IO;


namespace Db4objects.Db4odoc.Performance
{
    class InsertPerformanceBenchmark
    {

        private static int _count = 100000;

        private static int _commitInterval = 10000;

        private static int _depth = 3;

        private static bool _isClientServer = false;

        private static bool Tcp = true;

        private static string _filePath = "performance.db4o";

        private static readonly int Port = 4477;

        private IObjectContainer objectContainer;

        private IObjectServer objectServer;

        private DateTime startTime;


        public static void Main(string[] arguments)
        {
            //new InsertPerformanceBenchmark().RunDifferentObjectsTest();
            //new InsertPerformanceBenchmark().RunCommitTest();
            //new InsertPerformanceBenchmark().RunRamDiskTest();
            //new InsertPerformanceBenchmark().RunClientServerTest();
            //new InsertPerformanceBenchmark().RunIndexTest();
            new InsertPerformanceBenchmark().RunInheritanceTest();
        }
        // end Main

        private void RunCommitTest()
        {

            ConfigureForCommitTest();
            InitForCommitTest();

            Clean();
            System.Console.WriteLine("Storing objects as a bulk:");
            Open();
            Store();
            Close();

            Clean();
            System.Console.WriteLine("Storing objects with commit after each " + _commitInterval + " objects:");
            Open();
            StoreWithCommit();
            Close();
        }
        // end RunCommitTest

        private void RunRamDiskTest()
        {

            ConfigureRamDrive();

            InitForHardDriveTest();
            Clean();
            System.Console.WriteLine("Storing " + _count + " objects of depth " + _depth + " on a hard drive:");
            Open();
            Store();
            Close();

            InitForRamDriveTest();
            Clean();
            System.Console.WriteLine("Storing " + _count + " objects of depth " + _depth + " on a RAM disk:");
            Open();
            Store();
            Close();
        }
        // end RunRamDiskTest

        private void RunClientServerTest()
        {
            ConfigureClientServer();

            Init();
            Clean();
            System.Console.WriteLine("Storing " + _count + " objects of depth " + _depth + " locally:");
            Open();
            Store();
            Close();

            InitForClientServer();
            Clean();
            System.Console.WriteLine("Storing " + _count + " objects of depth " + _depth + " remotely:");
            Open();
            Store();
            Close();
        }
        // end RunClientServerTest

        private void RunInheritanceTest()
        {
            Configure();
            Init();
            Clean();
            System.Console.WriteLine("Storing " + _count + " objects of depth " + _depth);
            Open();
            Store();
            Close();

            Clean();
            System.Console.WriteLine("Storing " + _count + " inherited objects of depth " + _depth);
            Open();
            StoreInherited();
            Close();
        }
        // end RunInheritanceTest

        private void RunDifferentObjectsTest()
        {
            Configure();
            Init();
            System.Console.WriteLine("Storing " + _count + " objects with " + _depth + " levels of embedded objects:");

            Clean();
            System.Console.WriteLine(" - primitive object with int field");
            Open();
            StoreSimplest();
            Close();

            Open();
            System.Console.WriteLine(" - object with String field");
            Store();
            Close();

            Clean();
            Open();
            System.Console.WriteLine(" - object with StringBuilder field");
            StoreWithStringBuilder();
            Close();

            Clean();
            Open();
            System.Console.WriteLine(" - object with int array field");
            StoreWithArray();
            Close();

            Clean();
            Open();
            System.Console.WriteLine(" - object with ArrayList field");
            StoreWithArrayList();
            Close();
        }
        // end RunDifferentObjectsTest

        private void RunIndexTest()
        {
            Init();
            System.Console.WriteLine("Storing " + _count + " objects with " + _depth + " levels of embedded objects:");

            Clean();
            Configure();
            System.Console.WriteLine(" - no index");
            Open();
            Store();
            Close();

            ConfigureIndex();
            System.Console.WriteLine(" - index on String field");
            Open();
            Store();
            Close();
        }
        // end RunIndexTest

        private void Init()
        {
            _count = 10000;
            _depth = 3;
            _isClientServer = false;
        }
        // end Init

        private void InitForClientServer()
        {
            _count = 10000;
            _depth = 3;
            _isClientServer = true;
        }
        // end InitForClientServer

        private void InitForRamDriveTest()
        {
            _count = 100000;
            _depth = 3;
            _filePath = "r:\\performance.db4o";
            _isClientServer = false;
        }
        // end InitForRamDriveTest

        private void InitForHardDriveTest()
        {
            _count = 100000;
            _depth = 3;
            _filePath = "performance.db4o";
            _isClientServer = false;
        }
        // end InitForHardDriveTest

        private void InitForCommitTest()
        {
            _count = 100000;
            _commitInterval = 10000;
            _depth = 3;
            _isClientServer = false;
        }
        // end InitForCommitTest

        private void Clean()
        {
            File.Delete(_filePath);
        }
        // end Clean

        private void Configure()
        {
            IConfiguration config = Db4oFactory.Configure();
            config.LockDatabaseFile(false);
            config.WeakReferences(false);
            config.Io(new MemoryIoAdapter());
            config.FlushFileBuffers(false);
        }
        // end Configure

        private void ConfigureForCommitTest()
        {
            IConfiguration config = Db4oFactory.Configure();
            config.LockDatabaseFile(false);
            config.WeakReferences(false);
            // FlushFileBuffers should be set to true to ensure that
            // the commit information is physically written 
            // and in the correct order
            config.FlushFileBuffers(false);
        }
        // end ConfigureForCommitTest

        private void ConfigureIndex()
        {
            IConfiguration config = Db4oFactory.Configure();
            config.LockDatabaseFile(false);
            config.WeakReferences(false);
            config.Io(new MemoryIoAdapter());
            config.FlushFileBuffers(false);
            config.ObjectClass(typeof(Item)).ObjectField("_name").Indexed(true);
        }
        // end ConfigureIndex

        private void ConfigureClientServer()
        {
            IConfiguration config = Db4oFactory.Configure();
            config.LockDatabaseFile(false);
            config.WeakReferences(false);
            config.FlushFileBuffers(false);
            config.ClientServer().SingleThreadedClient(true);
        }
        // end ConfigureClientServer

        private void ConfigureRamDrive()
        {
            IConfiguration config = Db4oFactory.Configure();
            config.LockDatabaseFile(false);
            config.WeakReferences(false);
            config.FlushFileBuffers(true);
        }
        // end ConfigureRamDrive

        private void Store()
        {
            StartTimer();
            for (int i = 0; i < _count; i++)
            {
                Item item = new Item("load", null);
                for (int j = 1; j < _depth; j++)
                {
                    item = new Item("load", item);
                }
                objectContainer.Set(item);
            }
            objectContainer.Commit();
            StopTimer("Store " + TotalObjects() + " objects");
        }
        // end Store

        private void StoreInherited()
        {
            StartTimer();
            for (int i = 0; i < _count; i++)
            {
                ItemDerived item = new ItemDerived("load", null);
                for (int j = 1; j < _depth; j++)
                {
                    item = new ItemDerived("load", item);
                }
                objectContainer.Set(item);
            }
            objectContainer.Commit();
            StopTimer("Store " + TotalObjects() + " objects");
        }
        // end StoreInherited


        private void StoreWithCommit()
        {
            StartTimer();
            int k = 0;
            while (k < _count)
            {
                for (int i = 0; i < _commitInterval; i++)
                {
                    Item item = new Item("load", null);
                    k++;
                    for (int j = 1; j < _depth; j++)
                    {
                        item = new Item("load", item);
                    }
                    objectContainer.Set(item);
                }
                objectContainer.Commit();
            }
            objectContainer.Commit();
            StopTimer("Store " + TotalObjects() + " objects");
        }
        // end StoreWithCommit

        private void StoreWithStringBuilder()
        {
            StartTimer();
            for (int i = 0; i < _count; i++)
            {
                ItemWithStringBuilder item = new ItemWithStringBuilder(new StringBuilder("load"), null);
                for (int j = 1; j < _depth; j++)
                {
                    item = new ItemWithStringBuilder(new StringBuilder("load"), item);
                }
                objectContainer.Set(item);
            }
            objectContainer.Commit();
            StopTimer("Store " + TotalObjects() + " objects");
        }
        // end StoreWithStringBuilder

        private void StoreSimplest()
        {
            StartTimer();
            for (int i = 0; i < _count; i++)
            {
                SimplestItem item = new SimplestItem(i, null);
                for (int j = 1; j < _depth; j++)
                {
                    item = new SimplestItem(i, item);
                }
                objectContainer.Set(item);
            }
            objectContainer.Commit();
            StopTimer("Store " + TotalObjects() + " objects");
        }
        // end StoreSimplest

        private void StoreWithArray()
        {
            StartTimer();
            int[] array = new int[] { 1, 2, 3, 4 };
            for (int i = 0; i < _count; i++)
            {
                int[] id = new int[] { 1, 2, 3, 4 };
                ItemWithArray item = new ItemWithArray(id, null);
                for (int j = 1; j < _depth; j++)
                {
                    int[] id1 = new int[] { 1, 2, 3, 4 };
                    item = new ItemWithArray(id1, item);
                }
                objectContainer.Set(item);
            }
            objectContainer.Commit();
            StopTimer("Store " + TotalObjects() + " objects");
        }
        // end StoreWithArray

        private void StoreWithArrayList()
        {
            StartTimer();
            ArrayList idList = new ArrayList();
            idList.Add(1);
            idList.Add(2);
            idList.Add(3);
            idList.Add(4);
            for (int i = 0; i < _count; i++)
            {
                ArrayList ids = new ArrayList();
                ids.AddRange(idList);
                ItemWithArrayList item = new ItemWithArrayList(ids, null);
                for (int j = 1; j < _depth; j++)
                {
                    ArrayList ids1 = new ArrayList();
                    ids1.AddRange(idList);
                    item = new ItemWithArrayList(ids1, item);
                }
                objectContainer.Set(item);
            }
            objectContainer.Commit();
            StopTimer("Store " + TotalObjects() + " objects");
        }
        // end StoreWithArrayList

        private int TotalObjects()
        {
            return _count * _depth;
        }
        // end TotalObjects

        private void Open()
        {
            if (_isClientServer)
            {
                int port = Tcp ? Port : 0;
                String user = "db4o";
                String password = user;
                objectServer = Db4oFactory.OpenServer(_filePath, port);
                objectServer.GrantAccess(user, password);
                objectContainer = Tcp ? Db4oFactory.OpenClient("localhost", port, user,
                        password) : objectServer.OpenClient();
            }
            else
            {
                objectContainer = Db4oFactory.OpenFile(_filePath);
            }
        }
        // end Open

        private void Close()
        {
            objectContainer.Close();
            if (_isClientServer)
            {
                objectServer.Close();
            }
        }
        // end Close

        private void StartTimer()
        {
            startTime = DateTime.UtcNow;
        }
        // end StartTimer

        private void StopTimer(String message)
        {
            DateTime stop = DateTime.UtcNow;
            TimeSpan duration = stop - startTime;
            System.Console.WriteLine(message + ": " + duration.TotalMilliseconds + "ms");
        }
        // end StopTimer

    }

    public class Item
    {

        public String _name;
        public Item _child;

        public Item()
        {

        }

        public Item(String name, Item child)
        {
            _name = name;
            _child = child;
        }
    }
    // end Item

    public class ItemDerived : Item
    {

        public ItemDerived(String name, ItemDerived child)
            : base(name, child)
        {

        }
    }
    // end ItemDerived

    public class ItemWithStringBuilder
    {
        public StringBuilder _name;
        public ItemWithStringBuilder _child;

        public ItemWithStringBuilder()
        {
        }

        public ItemWithStringBuilder(StringBuilder name, ItemWithStringBuilder child)
        {
            _name = name;
            _child = child;
        }
    }
    // end ItemWithStringBuilder

    public class SimplestItem
    {
        public int _id;
        public SimplestItem _child;

        public SimplestItem()
        {
        }

        public SimplestItem(int id, SimplestItem child)
        {
            _id = id;
            _child = child;
        }
    }
    // end SimplestItem

    public class ItemWithArray
    {
        public int[] _id;
        public ItemWithArray _child;

        public ItemWithArray()
        {
        }

        public ItemWithArray(int[] id, ItemWithArray child)
        {
            _id = id;
            _child = child;
        }
    }
    // end ItemWithArray

    public class ItemWithArrayList
    {
        public ArrayList _ids;
        public ItemWithArrayList _child;

        public ItemWithArrayList()
        {
        }

        public ItemWithArrayList(ArrayList ids, ItemWithArrayList child)
        {
            _ids = ids;
            _child = child;
        }
    }
    // end ItemWithArrayList

}

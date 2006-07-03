using System;
using System.Collections;
using System.IO;

using com.db4o.ext;
using com.db4o.query;

namespace com.db4o.test
{

    public class UpdatingDb4oVersions
	{

        static string PATH = "../../../test/db4oVersions/";

        static string[] VERSIONS = {
            "db4o_3.0.3", 
            "db4o_4.0.005",
            "db4o_4.0.009",
            "db4o_4.1.001",
            "db4o_4.1.002",
            "db4o_4.6.010",
            "db4o_5.2.002"
        };

        IList list;
        IDictionary map;
        string name;

        public void Configure(){
            Db4o.Configure().AllowVersionUpdates(true);
        }

        public void Store(){
            if(Tester.IsClientServer()){
                return;
            }
            string file = PATH + FileName();
            Directory.CreateDirectory(PATH);
            File.Delete(file);
            ExtObjectContainer objectContainer = Db4o.OpenFile(file).Ext();
            UpdatingDb4oVersions udv = new UpdatingDb4oVersions();
            udv.name = "check";
            udv.list = objectContainer.Collections().NewLinkedList();
            udv.map = objectContainer.Collections().NewHashMap(1);
            objectContainer.Set(udv);
            udv.list.Add("check");
            udv.map["check"] = "check";
            objectContainer.Close();
        }

        public void Test(){

            String shortName = GetType().FullName;

            String fullAssemblyName = GetType().Assembly.GetName().ToString();
            String shortAssemblyName = fullAssemblyName;
            int pos = fullAssemblyName.IndexOf(",");
            if(pos > 0) {
                shortAssemblyName = fullAssemblyName.Substring(0, pos);
            }
            String fullyQualifiedTypeName  = GetType().FullName + ", " + shortAssemblyName;

            if(Tester.IsClientServer()){
                return;
            }
            for(int i = 0; i < VERSIONS.Length; i ++){
                string oldFile = PATH + VERSIONS[i];
                if(File.Exists(oldFile)){
                    String testFile = PATH + VERSIONS[i] + ".yap";
                    File.Delete(testFile);
                    File.Copy(oldFile, testFile, true);
                    ExtObjectContainer objectContainer = Db4o.OpenFile(testFile).Ext();
                    StoredClass[] storedClasses = objectContainer.StoredClasses();
                    for(int j = 0; j < storedClasses.Length; j ++){
                        if(storedClasses[j].GetName().StartsWith(shortName)){
                            string oldName = storedClasses[j].GetName();
                            storedClasses[j].Rename(fullyQualifiedTypeName);
                            Console.WriteLine("Renamed " + oldName + " to " + fullyQualifiedTypeName);
                        }
                    }

                    objectContainer.Close();
                    objectContainer = Db4o.OpenFile(testFile).Ext();
                    Query q = objectContainer.Query();
                    q.Constrain(typeof(UpdatingDb4oVersions));
                    ObjectSet objectSet = q.Execute();
                    Tester.Ensure(objectSet.Size() == 1);
                    UpdatingDb4oVersions udv = (UpdatingDb4oVersions)objectSet.Next();
                    Tester.Ensure(udv.name.Equals("check"));
                    Tester.Ensure(udv.list.Count == 1);
                    Tester.Ensure(udv.list[0].Equals("check"));

                    // The following relies on a the hash method being constant for strings.
                    // It is broken if a database file was created on .NET 1.1 and opened
                    // on .NET 2.0.

                    // The test is commented out since it fails.

                    // The P2HashMap implementation is just about to be replaced by more 
                    // intelligent new fast collections, so this will be fixed anyway.

                    // Tester.Ensure(udv.map["check"].Equals("check"));


                    objectContainer.Close();
                }else{
                    Console.WriteLine("Version upgrade check failed. File not found:");
                    Console.WriteLine(oldFile);
                }
            }
        }

        private static string FileName(){
            return Db4o.Version().Replace(" ", "_") + ".yap";
        }
	}
}

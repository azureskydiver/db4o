using System;
using System.Collections;
using System.IO;

using com.db4o.ext;
using com.db4o.query;

namespace com.db4o.test
{

    public class UpdatingDb4oVersions
	{

        static string PATH = "./test/db4oVersions/";

        static string[] VERSIONS = {"db4o_3.0.3", "db4o_4.0.005"};

        IList list;
        IDictionary map;
        string name;

        public void store(){
            if(Test.isClientServer()){
                return;
            }
            string file = PATH + fileName();
            Directory.CreateDirectory(PATH);
            File.Delete(file);
            ExtObjectContainer objectContainer = Db4o.openFile(file).ext();
            UpdatingDb4oVersions udv = new UpdatingDb4oVersions();
            udv.name = "check";
            udv.list = objectContainer.collections().newLinkedList();
            udv.map = objectContainer.collections().newHashMap(1);
            objectContainer.set(udv);
            udv.list.Add("check");
            udv.map["check"] = "check";
            objectContainer.close();
        }

        public void test(){

            String shortName = GetType().FullName;

            String fullyQualifiedTypeName = GetType().AssemblyQualifiedName;
            int pos = fullyQualifiedTypeName.IndexOf(",");
            pos = fullyQualifiedTypeName.IndexOf(",",pos +1);
            fullyQualifiedTypeName = fullyQualifiedTypeName.Substring(0, pos);

            if(Test.isClientServer()){
                return;
            }
            for(int i = 0; i < VERSIONS.Length; i ++){
                string oldFile = PATH + VERSIONS[i];
                if(File.Exists(oldFile)){
                    String testFile = PATH + VERSIONS[i] + ".yap";
                    File.Delete(testFile);
                    File.Copy(oldFile, testFile, true);
                    ExtObjectContainer objectContainer = Db4o.openFile(testFile).ext();
                    StoredClass[] storedClasses = objectContainer.storedClasses();
                    for(int j = 0; j < storedClasses.Length; j ++){
                        if(storedClasses[j].getName().StartsWith(shortName)){
                            string oldName = storedClasses[j].getName();
                            storedClasses[j].rename(fullyQualifiedTypeName);
                            Console.WriteLine("Renamed " + oldName + " to " + fullyQualifiedTypeName);
                        }
                    }

                    objectContainer.close();
                    objectContainer = Db4o.openFile(testFile).ext();
                    Query q = objectContainer.query();
                    q.constrain(typeof(UpdatingDb4oVersions));
                    ObjectSet objectSet = q.execute();
                    Test.ensure(objectSet.size() == 1);
                    UpdatingDb4oVersions udv = (UpdatingDb4oVersions)objectSet.next();
                    Test.ensure(udv.name.Equals("check"));
                    Test.ensure(udv.list.Count == 1);
                    Test.ensure(udv.list[0].Equals("check"));
                    Test.ensure(udv.map["check"].Equals("check"));
                    objectContainer.close();
                }else{
                    Console.WriteLine("Version upgrade check failed. File not found:");
                    Console.WriteLine(oldFile);
                }
            }
        }

        private static string fileName(){
            return Db4o.version().Replace(" ", "_") + ".yap";
        }
	}
}

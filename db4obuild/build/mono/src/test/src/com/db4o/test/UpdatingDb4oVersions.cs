/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */

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

        static string[] VERSIONS = {
            "db4o_3.0.3", 
            "db4o_4.0.005",
            "db4o_4.0.009",
            "db4o_4.1.001",
            "db4o_4.1.002"
                                   };

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

            String fullAssemblyName = GetType().Assembly.GetName().ToString();
            String shortAssemblyName = fullAssemblyName;
            int pos = fullAssemblyName.IndexOf(",");
            if(pos > 0) {
                shortAssemblyName = fullAssemblyName.Substring(0, pos);
            }
            String fullyQualifiedTypeName  = GetType().FullName + ", " + shortAssemblyName;

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

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
using j4o.lang;
using com.db4o;
using com.db4o.ext;
namespace com.db4o.tools {

    /**
     * prints statistics about a database file to System.out.
     * <br>
     * <br>Pass the database file path as an argument.
     * <br>
     * <br>This class is not part of db4o.dll. It is delivered
     * as sourcecode in the path ../com/db4o/tools/<br><br>
     */
    public class Statistics {
      
        /**
         * the main method that runs the statistics.
         * @param String[] a String array of length 1, with the name
         * of the database file as element 0.
         */
        public static void Main(String[] args) {
            if (args == null || args.Length != 1) {
                Console.WriteLine("Usage: java com.db4o.tools.Statistics <database filename>");
            } else {
                new Statistics().run(args[0]);
            }
        }
      
        public void run(String filename) {
            if (new j4o.io.File(filename).exists()) {
                ObjectContainer con1 = null;
                try { 
                    con1 = Db4o.openFile(filename);
                    printHeader("STATISTICS");
                    Console.WriteLine("File: " + filename);
                    printStats(con1, filename);
                    con1.close();
                      
                }  catch (Exception e) { 
                    Console.WriteLine("Statistics failed for file: \'" + filename + "\'");
                    Console.WriteLine(e.Message);
                    j4o.lang.JavaSystem.printStackTrace(e);
                                         
                }
            } else {
                Console.WriteLine("File not found: \'" + filename + "\'");
            }
        }
      
        private void printStats(ObjectContainer con, String filename) {
            Tree unavailable = new TreeString(REMOVE);
            Tree noConstructor = new TreeString(REMOVE);
            StoredClass[] internalClasses = con.ext().storedClasses();
            for (int i1 = 0; i1 < internalClasses.Length; i1++) {
                try { 
                    Class clazz1 = Class.forName(internalClasses[i1].getName());
                    try { 
                        clazz1.newInstance();
                                
                    }  catch (Exception th) { 
                        noConstructor = noConstructor.add(new TreeString(internalClasses[i1].getName()));
                    }
                }  catch (Exception t) { 
                    unavailable = unavailable.add(new TreeString(internalClasses[i1].getName()));
                                         
                }
            }
            unavailable = unavailable.removeLike(new TreeString(REMOVE));
            noConstructor = noConstructor.removeLike(new TreeString(REMOVE));
            if (unavailable != null) {
                printHeader("UNAVAILABLE");
                unavailable.traverse(new StatisticsPrintKey());
            }
            if (noConstructor != null) {
                printHeader("NO PUBLIC CONSTRUCTOR");
                noConstructor.traverse(new StatisticsPrintKey());
            }
            printHeader("CLASSES");
            Console.WriteLine("Number of objects per class:");
            if (internalClasses.Length > 0) {
                Tree all1 = new TreeStringObject(internalClasses[0].getName(), internalClasses[0]);
                for (int i1 = 1; i1 < internalClasses.Length; i1++) {
                    all1 = all1.add(new TreeStringObject(internalClasses[i1].getName(), internalClasses[i1]));
                }
                all1.traverse(new StatisticsPrintNodes());
            }
            printHeader("SUMMARY");
            Console.WriteLine("File: " + filename);
            Console.WriteLine("Stored classes: " + internalClasses.Length);
            if (unavailable != null) {
                Console.WriteLine("Unavailable classes: " + unavailable.size());
            }
            if (noConstructor != null) {
                Console.WriteLine("Classes without public constructors: " + noConstructor.size());
            }
            Console.WriteLine("Total number of objects: " + (ids.size() - 1));
        }
      
        private void printHeader(String str) {
            int starcount = (39 - str.Length) / 2;
            string stars = "";
            for (int i1 = 0; i1 < starcount; i1++) {
                stars += "*";
            }
            Console.WriteLine("\n\n" + stars + " " + str + " " + stars);
        }

        internal static TreeInt ids = new TreeInt(0);
        private static String REMOVE = "XXxxREMOVExxXX";
    }

    internal class StatisticsPrintKey : Visitor4{
        public void visit(Object obj){
            Console.WriteLine(((TreeString)obj).i_key);           
        }
    }

    internal class StatisticsPrintNodes : Visitor4{
        public void visit(Object obj){
            TreeStringObject node = (TreeStringObject)obj;
            long[] newIDs = ((StoredClass)node.i_object).getIDs();
            for (int j = 0; j < newIDs.Length; j ++) {
                if (Statistics.ids.find(new TreeInt((int)newIDs[j])) == null) {
                    Statistics.ids = (TreeInt)Statistics.ids.add(new TreeInt((int)newIDs[j]));
                }
            }
            Console.WriteLine(node.i_key + ": " + newIDs.Length);
        }

    }
}
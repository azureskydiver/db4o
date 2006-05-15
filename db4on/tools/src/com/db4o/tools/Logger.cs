/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4o.foundation;
using j4o.lang;
using com.db4o;
using com.db4o.ext;
using j4o.io;
using j4o.lang.reflect;
namespace com.db4o.tools {

    /**
     * Logger class to log and analyse objects in RAM.
     * <br><br>This class is not part of db4o.jar. It is delivered as
     * sourcecode in the path ../com/db4o/tools/<br><br>
     */
    public class Logger {

        private static int MAXIMUM_OBJECTS = 20;
      
        /**
         * opens a database file and logs the content of a class to
         * standard out.
         * @param [database filename] [fully qualified classname]
         */
        public static void Main(String[] args) {
            if (args == null || args.Length == 0) {
                Console.WriteLine("Usage: java com.db4o.tools.Logger <database filename> <class>");
            } else {
                if (!new File(args[0]).exists()) {
                    Console.WriteLine("A database file with the name \'" + args[0] + "\' does not exist.");
                } else {
                    ExtObjectContainer con1 = null;
                    try { 
                        ObjectContainer c1 = Db4o.openFile(args[0]);
                        if (c1 == null) {
                            throw new RuntimeException();
                        }
                        con1 = c1.ext();
                  
                    }  catch (Exception e) { 
                        Console.WriteLine("The database file \'" + args[0] + "\' could not be opened.");
                        return;
                  
                    }
                    if (args.Length > 1) {
                        StoredClass sc1 = con1.storedClass(args[1]);
                        if (sc1 == null) {
                            Console.WriteLine("There is no stored class with the name \'" + args[1] + "\'.");
                        } else {
                            long[] ids1 = sc1.getIDs();
                            for (int i1 = 0; i1 < ids1.Length; i1++) {
                                if (i1 > MAXIMUM_OBJECTS) {
                                    break;
                                }
                                Object obj1 = con1.getByID(ids1[i1]);
                                con1.activate(obj1, Int32.MaxValue);
                                log(con1, obj1);
                            }
                            msgCount(ids1.Length);
                        }
                    } else {
                        ObjectSet set1 = con1.get(null);
                        int i1 = 0;
                        while (set1.hasNext()) {
                            Object obj1 = set1.next();
                            con1.activate(obj1, Int32.MaxValue);
                            log(con1, obj1);
                            if (++i1 > MAXIMUM_OBJECTS) {
                                break;
                            }
                        }
                        msgCount(set1.size());
                    }
                    con1.close();
                }
            }
        }
      
        /**
         * logs the structure of an object. @param container the {@link ObjectContainer} to be used, or null to log any object. @param <code>Object</code> the object to be analysed.
         */
        public static void log(ObjectContainer container, Object obj) {
            if (obj == null) {
                log("[NULL]");
            } else {
                log(j4o.lang.Class.getClassForObject(obj).getName());
                log(container, obj, 0, new Collection4());
            }
        }
      
        /**
         * logs the structure of an object. @param <code>Object</code> the object to be analysed.
         */
        public static void log(Object obj) {
            ObjectSet objectSet = obj as ObjectSet;
            if(objectSet != null){
                while(objectSet.hasNext()){
                    log(objectSet.next());
                }
            }else{
                log(null, obj);
            }
        }
      
        /**
         * logs all objects in the passed ObjectContainer. @param container the {@link ObjectContainer} to be used.
         */
        public static void logAll(ObjectContainer container) {
            ObjectSet set1 = container.get(null);
            while (set1.hasNext()) {
                log(container, set1.next());
            }
        }
      
        /**
         * limits logging to a maximum depth. @param int the maximum depth.
         */
        public static void setMaximumDepth(int depth) {
            maximumDepth = depth;
        }
      
        private static void msgCount(int count) {
            Console.WriteLine("\n\nLog complete.\nObjects: " + count);
            if (count > MAXIMUM_OBJECTS) {
                Console.WriteLine("Displayed due to setting of " + Class.getClassForType(typeof(Logger)).getName() + "#MAXIMUM_OBJECTS: " + MAXIMUM_OBJECTS);
            }
        }
      
        private static void log(ObjectContainer a_container, Object a_object, int a_depth, Collection4 a_list) {
            if (a_list.contains(a_object) || a_depth > maximumDepth) {
                return;
            }
            Class clazz1 = j4o.lang.Class.getClassForObject(a_object);
            for (int i1 = 0; i1 < IGNORE.Length; i1++) {
                if (clazz1.isAssignableFrom(IGNORE[i1])) {
                    return;
                }
            }
            if (Platform4.isSimple(clazz1)) {
                log(a_depth + 1, j4o.lang.Class.getClassForObject(a_object).getName(), a_object.ToString());
                return;
            }
            a_list.add(a_object);
            Class[] classes1 = getClassHierarchy(a_object);
            String spaces1 = "";
            for (int i1 = classes1.Length - 1; i1 >= 0; i1--) {
                spaces1 = spaces1 + sp;
                String className1 = spaces1;
                int pos1 = classes1[i1].getName().LastIndexOf(".");
                if (pos1 > 0) {
                    className1 += classes1[i1].getName().Substring(pos1);
                } else {
                    className1 += classes1[i1].getName();
                }
                if (classes1[i1] == Class.getClassForType(typeof(j4o.util.Date))) {
                    String fieldName1 = className1 + ".getTime";
                    Object obj1 = System.Convert.ToInt64(((j4o.util.Date)a_object).getTime());
                    log(a_container, obj1, Class.getClassForType(typeof(Int64)), fieldName1, a_depth + 1, -1, a_list);
                } else {
                    Field[] fields1 = classes1[i1].getDeclaredFields();
                    for (int j1 = 0; j1 < fields1.Length; j1++) {
                        Platform4.setAccessible(fields1[j1]);
                        String fieldName1 = className1 + "." + fields1[j1].getName();
                        try { 
                            Object obj1 = fields1[j1].get(a_object);
                            if (j4o.lang.Class.getClassForObject(obj1).isArray()) {
                                obj1 = normalizeNArray(obj1);
                                int len1 = j4o.lang.reflect.JavaArray.getLength(obj1);
                                for (int k1 = 0; k1 < len1; k1++) {
                                    Object element1 = j4o.lang.reflect.JavaArray.get(obj1, k1);
                                    Class arrClass1 = element1 == null ? null : j4o.lang.Class.getClassForObject(element1);
                                    log(a_container, element1, arrClass1, fieldName1, a_depth + 1, k1, a_list);
                                }
                            } else {
                                log(a_container, obj1, fields1[j1].getType(), fieldName1, a_depth + 1, -1, a_list);
                            }
                     
                        }  catch (Exception e) { 
                        }
                    }
                }
            }
        }
      
        private static void log(ObjectContainer a_container, Object a_object, Class a_Class, String a_fieldName, int a_depth, int a_arrayElement, Collection4 a_list) {
            if (a_depth > maximumDepth) {
                return;
            }
            String fieldName1 = a_arrayElement > -1 ? a_fieldName + sp + sp + a_arrayElement : a_fieldName;
            if (a_object != null) {
                if (a_container == null || a_container.ext().isStored(a_object)) {
                    if (a_container == null || a_container.ext().isActive(a_object)) {
                        log(a_depth, fieldName1, "");
                        Class clazz1 = j4o.lang.Class.getClassForObject(a_object);
                        bool found1 = false;
                        if (Platform4.isSimple(clazz1)) {
                            log(a_depth + 1, j4o.lang.Class.getClassForObject(a_object).getName(), a_object.ToString());
                            found1 = true;
                        }
                        if (!found1) {
                            log(a_container, a_object, a_depth, a_list);
                        }
                    } else {
                        log(a_depth, fieldName1, "DEACTIVATED " + j4o.lang.Class.getClassForObject(a_object).getName());
                    }
                    return;
                } else {
                    log(a_depth, fieldName1, a_object.ToString());
                }
            } else {
                log(a_depth, fieldName1, "[NULL]");
            }
        }
      
        private static void log(String a_msg) {
            if (!silent) {
                Console.WriteLine(a_msg);
            }
        }
      
        private static void log(int indent, String a_property, String a_value) {
            for (int i1 = 0; i1 < indent; i1++) {
                a_property = sp + sp + a_property;
            }
            log(a_property, a_value);
        }
      
        private static void log(String a_property, String a_value) {
            if (a_value == null) a_value = "[NULL]";
            log(a_property + ": " + a_value);
        }
      
        private static void log(bool a_true) {
            if (a_true) {
                log("true");
            } else {
                log("false");
            }
        }
      
        private static void log(Exception e, Object obj, String msg) {
            String l_msg1;
            if (e != null) {
                l_msg1 = "!!! " + j4o.lang.Class.getClassForObject(e).getName();
                String l_exMsg1 = e.Message;
                if (l_exMsg1 != null) {
                    l_msg1 += sp + l_exMsg1;
                }
            } else {
                l_msg1 = "!!!Exception log";
            }
            if (obj != null) {
                l_msg1 += " in " + j4o.lang.Class.getClassForObject(obj).getName();
            }
            if (msg != null) {
                l_msg1 += sp + msg;
            }
            log(l_msg1);
        }
      
        private static Class[] getClassHierarchy(Object a_object) {
            Class[] classes1 = new Class[]{
                                              j4o.lang.Class.getClassForObject(a_object)         };
            return getClassHierarchy(classes1);
        }
      
        private static Class[] getClassHierarchy(Class[] a_classes) {
            Class clazz1 = a_classes[a_classes.Length - 1].getSuperclass();
            if (clazz1.Equals(Class.getClassForType(typeof(Object)))) {
                return a_classes;
            }
            Class[] classes1 = new Class[a_classes.Length + 1];
			System.Array.Copy(a_classes, 0, classes1, 0, a_classes.Length);
            classes1[a_classes.Length] = clazz1;
            return getClassHierarchy(classes1);
        }
      
        private static Object normalizeNArray(Object a_object) {
            if (j4o.lang.reflect.JavaArray.getLength(a_object) > 0) {
                Object first1 = j4o.lang.reflect.JavaArray.get(a_object, 0);
                if (first1 != null && j4o.lang.Class.getClassForObject(first1).isArray()) {
                    int[] dim1 = arrayDimensions(a_object);
                    Object all1 = (Object)new Object[arrayElementCount(dim1)];
                    normalizeNArray1(a_object, all1, 0, dim1, 0);
                    return all1;
                }
            }
            return a_object;
        }
      
        private static int normalizeNArray1(Object a_object, Object a_all, int a_next, int[] a_dim, int a_index) {
            if (a_index == a_dim.Length - 1) {
                for (int i1 = 0; i1 < a_dim[a_index]; i1++) {
                    j4o.lang.reflect.JavaArray.set(a_all, a_next++, j4o.lang.reflect.JavaArray.get(a_object, i1));
                }
            } else {
                for (int i1 = 0; i1 < a_dim[a_index]; i1++) {
                    a_next = normalizeNArray1(j4o.lang.reflect.JavaArray.get(a_object, i1), a_all, a_next, a_dim, a_index + 1);
                }
            }
            return a_next;
        }
      
        private static int[] arrayDimensions(Object a_object) {
            int count1 = 0;
            for (Class clazz1 = j4o.lang.Class.getClassForObject(a_object); clazz1.isArray(); clazz1 = clazz1.getComponentType()) {
                count1++;
            }
            int[] dim1 = new int[count1];
            for (int i1 = 0; i1 < count1; i1++) {
                dim1[i1] = j4o.lang.reflect.JavaArray.getLength(a_object);
                a_object = j4o.lang.reflect.JavaArray.get(a_object, 0);
            }
            return dim1;
        }
      
        private static int arrayElementCount(int[] a_dim) {
            int elements1 = a_dim[0];
            for (int i1 = 1; i1 < a_dim.Length; i1++) {
                elements1 *= a_dim[i1];
            }
            return elements1;
        }
        private static Class[] IGNORE = {
                                            Class.getClassForType(typeof(Class))      };

        private static int maximumDepth = Int32.MaxValue;
        private static String sp = " ";
        private static bool silent;
    }
}
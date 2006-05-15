/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using j4o.lang;
using j4o.lang.reflect;
using j4o.util;
using j4o.io;
using com.db4o.test.soda;
namespace com.db4o.test.soda.utils {

   public class STLogger {
      
      public STLogger() : base() {
      }
      private static int maximumDepth = Int32.MaxValue;
      private static PrintStream _out = j4o.lang.JavaSystem._out;
      private static String cr = "";
      private static String sp = " ";
      private static bool silent = false;
      
      public static void log(Object a_object) {
         if (a_object == null) {
            log("[NULL]");
         } else {
            log(j4o.lang.Class.getClassForObject(a_object).getName());
            log(a_object, 0, new ArrayList());
         }
      }
      
      public static void setOut(PrintStream ps) {
         _out = ps;
      }
      
      public static void setMaximumDepth(int depth) {
         maximumDepth = depth;
      }
      
      public static void setSilent(bool flag) {
         silent = flag;
      }
      
      private static void log(Object a_object, int a_depth, ArrayList a_list) {
         if (a_object is SodaTest) {
            return;
         }
         if (a_list.Contains(a_object) || a_depth > maximumDepth) {
            return;
         }
         Class clazz1 = j4o.lang.Class.getClassForObject(a_object);
         for (int i1 = 0; i1 < ignore.Length; i1++) {
            if (clazz1.isAssignableFrom(ignore[i1])) {
               return;
            }
         }
         a_list.Add(a_object);
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
               log(obj1, Class.getClassForType(typeof(Int64)), fieldName1, a_depth + 1, -1, a_list);
            } else {
               Field[] fields1 = classes1[i1].getDeclaredFields();
               for (int j1 = 0; j1 < fields1.Length; j1++) {
                  String fieldName1 = className1 + "." + fields1[j1].getName();
                  try {
                     {
                        Object obj1 = fields1[j1].get(a_object);
                        if (j4o.lang.Class.getClassForObject(obj1).isArray()) {
                           obj1 = normalizeNArray(obj1);
                           int len1 = j4o.lang.reflect.JavaArray.getLength(obj1);
                           for (int k1 = 0; k1 < len1; k1++) {
                              Object element1 = j4o.lang.reflect.JavaArray.get(obj1, k1);
                              Class arrClass1 = element1 == null ? null : j4o.lang.Class.getClassForObject(element1);
                              log(element1, arrClass1, fieldName1, a_depth + 1, k1, a_list);
                           }
                        } else {
                           log(obj1, fields1[j1].getType(), fieldName1, a_depth + 1, -1, a_list);
                        }
                     }
                  }  catch (Exception e) {
                     {
                     }
                  }
               }
            }
         }
      }
      
      private static void log(Object a_object, Class a_Class, String a_fieldName, int a_depth, int a_arrayElement, ArrayList a_list) {
         if (a_depth > maximumDepth) {
            return;
         }
         String fieldName1 = a_arrayElement > -1 ? a_fieldName + sp + sp + a_arrayElement : a_fieldName;
         if (a_object != null) {
            log(a_depth, fieldName1, "");
            Class clazz1 = j4o.lang.Class.getClassForObject(a_object);
            if (Platform4.isSimple(clazz1)) {
               log(a_depth + 1, j4o.lang.Class.getClassForObject(a_object).getName(), a_object.ToString());
            } else {
               log(a_object, a_depth, a_list);
            }
         } else {
            log(a_depth, fieldName1, "[NULL]");
         }
      }
      
      private static void log(String a_msg) {
         if (!silent) {
            _out.println(a_msg + cr);
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
      
      static internal Object normalizeNArray(Object a_object) {
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
      
      static internal int normalizeNArray1(Object a_object, Object a_all, int a_next, int[] a_dim, int a_index) {
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
      
      static internal int[] arrayDimensions(Object a_object) {
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
      
      static internal int arrayElementCount(int[] a_dim) {
         int elements1 = a_dim[0];
         for (int i1 = 1; i1 < a_dim.Length; i1++) {
            elements1 *= a_dim[i1];
         }
         return elements1;
      }
      private static Class[] ignore = {
         Class.getClassForType(typeof(Class))      };
   }
}
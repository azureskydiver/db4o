/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4o.foundation;
using j4o.lang;
using j4o.lang.reflect;
namespace com.db4o.test {

   public class Compare {
      
      public Compare() : base() {
      }
      
      static internal bool hasPublicConstructor(Class a_class) {
          

         if (a_class == null) {
            return false;
         }

          for(int i = 0; i < SIMPLE_CLASSES.Length; i++){
              if(a_class == SIMPLE_CLASSES[i]){
                  return false;
              }
          }

         try {
            {
               Object o1 = a_class.newInstance();
               if (o1 != null) return true;
            }
         }  catch (Exception t) {
            {
            }
         }
         return false;
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
      
      public static void compare(com.db4o.ObjectContainer a_con, Object a_Compare, Object a_With, String a_path, Collection4 a_list) {
         
            a_con.activate(a_With, 1);
         
         if (a_list == null) {
            a_list = new Collection4();
         }
         if (a_list.containsByIdentity(a_Compare)) {
            return;
         }
         a_list.add(a_Compare);
         if (a_path == null || j4o.lang.JavaSystem.getLengthOf(a_path) < 1) if (a_Compare != null) {
            a_path = j4o.lang.Class.getClassForObject(a_Compare).getName() + ":";
         } else {
            if (a_With != null) a_path = j4o.lang.Class.getClassForObject(a_With).getName() + ":";
         }
         String path1 = a_path;
         if (a_Compare == null) if (a_With == null) {
            return;
         } else {
            Regression.addError("1==null:" + path1);
            return;
         }
         if (a_With == null) {
            Regression.addError("2==null:" + path1);
            return;
         }
         Class l_Class1 = j4o.lang.Class.getClassForObject(a_Compare);
         if (!l_Class1.isInstance(a_With)) {
            Regression.addError("class!=:" + path1 + l_Class1.getName() + ":" + j4o.lang.Class.getClassForObject(a_With).getName());
            return;
         }
         Field[] l_Fields1 = l_Class1.getDeclaredFields();
         for (int i1 = 0; i1 < l_Fields1.Length; i1++) {
            if (storeableField(l_Class1, l_Fields1[i1])) {
               Platform.setAccessible(l_Fields1[i1]);
               try {
                  {
                     path1 = a_path + l_Fields1[i1].getName() + ":";
                     Object l_Compare1 = l_Fields1[i1].get(a_Compare);
                     Object l_With1 = l_Fields1[i1].get(a_With);
                     if (l_Compare1 == null) {
                        if (l_With1 != null) {
                           Regression.addError("f1==null:" + path1);
                        }
                     } else if (l_With1 == null) Regression.addError("f2==null:" + path1); else if (j4o.lang.Class.getClassForObject(l_Compare1).isArray()) {
                        if (!j4o.lang.Class.getClassForObject(l_With1).isArray()) {
                           Regression.addError("f2!=array:" + path1);
                        } else {
                           l_Compare1 = normalizeNArray(l_Compare1);
                           l_With1 = normalizeNArray(l_With1);
                           int l_len1 = j4o.lang.reflect.JavaArray.getLength(l_Compare1);
                           if (l_len1 != j4o.lang.reflect.JavaArray.getLength(l_With1)) {
                              Regression.addError("arraylen!=:" + path1);
                           } else {
                              bool l_persistentArray1 = hasPublicConstructor(l_Fields1[i1].getType().getComponentType());
                              for (int j1 = 0; j1 < l_len1; j1++) {
                                 Object l_ElementCompare1 = j4o.lang.reflect.JavaArray.get(l_Compare1, j1);
                                 Object l_ElementWith1 = j4o.lang.reflect.JavaArray.get(l_With1, j1);
                                 if (l_persistentArray1) {
                                    compare(a_con, l_ElementCompare1, l_ElementWith1, path1, a_list);
                                 } else if (l_ElementCompare1 == null) {
                                    if (l_ElementWith1 != null) {
                                       Regression.addError("1e" + j1 + "==null:" + path1);
                                    }
                                 } else if (l_ElementWith1 == null) {
                                    Regression.addError("2e" + j1 + "==null:" + path1);
                                 } else {
                                    Class elementCompareClass1 = j4o.lang.Class.getClassForObject(l_ElementCompare1);
                                    if (elementCompareClass1 != j4o.lang.Class.getClassForObject(l_ElementWith1)) {
                                       Regression.addError("e" + j1 + "!=class:" + path1 + elementCompareClass1.ToString() + ":" + j4o.lang.Class.getClassForObject(l_ElementWith1).ToString());
                                    } else if (hasPublicConstructor(elementCompareClass1)) {
                                       compare(a_con, l_ElementCompare1, l_ElementWith1, path1, a_list);
                                    } else {
                                       if (!l_ElementCompare1.Equals(l_ElementWith1)) Regression.addError("e" + j1 + "!=:" + path1 + l_ElementCompare1.ToString() + ":" + l_ElementWith1.ToString());
                                    }
                                 }
                              }
                           }
                        }
                     } else if (hasPublicConstructor(l_Fields1[i1].getType())) compare(a_con, l_Compare1, l_With1, path1, a_list); else if (!l_Compare1.Equals(l_With1)) Regression.addError("!=:" + path1);
                  }
               }  catch (Exception e) {
                  {
                     Regression.addError("Exception:" + path1);
                  }
               }
            }
         }
      }
      
      static internal int[] arrayDimensions(Object a_object) {
         int count1 = 0;
         for (Class clazz1 = j4o.lang.Class.getClassForObject(a_object); clazz1.isArray(); clazz1 = clazz1.getComponentType()) count1++;
         int[] dim1 = new int[count1];
         for (int i1 = 0; i1 < count1; i1++) {
            dim1[i1] = j4o.lang.reflect.JavaArray.getLength(a_object);
            a_object = j4o.lang.reflect.JavaArray.get(a_object, 0);
         }
         return dim1;
      }
      
      static internal int normalizeNArray1(Object a_object, Object a_all, int a_next, int[] a_dim, int a_index) {
         if (a_index == a_dim.Length - 1) {
            for (int i1 = 0; i1 < a_dim[a_index]; i1++) j4o.lang.reflect.JavaArray.set(a_all, a_next++, j4o.lang.reflect.JavaArray.get(a_object, i1));
         } else {
            for (int i1 = 0; i1 < a_dim[a_index]; i1++) a_next = normalizeNArray1(j4o.lang.reflect.JavaArray.get(a_object, i1), a_all, a_next, a_dim, a_index + 1);
         }
         return a_next;
      }
      
      static internal int arrayElementCount(int[] a_dim) {
         int elements1 = a_dim[0];
         for (int i1 = 1; i1 < a_dim.Length; i1++) elements1 *= a_dim[i1];
         return elements1;
      }
      
      static internal String nl() {
         return j4o.lang.JavaSystem.getProperty("line.separator");
      }

       public static bool storeableField(Class a_class, Field a_field) {
           return (!Modifier.isStatic(a_field.getModifiers()))
               && (!Modifier.isTransient(a_field.getModifiers())
               & !(a_field.getName().IndexOf("$") > -1));
       }


       private static Class[] SIMPLE_CLASSES = {
                                                   Class.getClassForType(typeof(Boolean)),
                                                   Class.getClassForType(typeof(Byte)),
                                                   Class.getClassForType(typeof(Char)),
                                                   Class.getClassForType(typeof(Double)),
                                                   Class.getClassForType(typeof(Int16)),
                                                   Class.getClassForType(typeof(Int32)),
                                                   Class.getClassForType(typeof(Int64)),
                                                   Class.getClassForType(typeof(SByte)),
                                                   Class.getClassForType(typeof(Single)),
                                                   Class.getClassForType(typeof(String)),
                                                   Class.getClassForType(typeof(UInt32)),
                                                   Class.getClassForType(typeof(UInt16)),
                                                   Class.getClassForType(typeof(UInt64)),
                                                   Class.getClassForType(typeof(j4o.util.Date))      };


   }
}
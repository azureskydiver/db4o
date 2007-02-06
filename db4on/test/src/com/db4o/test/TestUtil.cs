/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4o.foundation;
using j4o.lang;
using j4o.lang.reflect;
using com.db4o.@internal;

namespace com.db4o.test {

   public class TestUtil {
      
      public TestUtil() : base() {
      }
      
      static internal bool HasPublicConstructor(Class a_class) {
          

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
               Object o1 = a_class.NewInstance();
               if (o1 != null) return true;
            }
         }  catch (Exception t) {
            {
            }
         }
         return false;
      }
      
      static internal Object NormalizeNArray(Object a_object) {
         if (j4o.lang.reflect.JavaArray.GetLength(a_object) > 0) {
            Object first1 = j4o.lang.reflect.JavaArray.Get(a_object, 0);
            if (first1 != null && j4o.lang.Class.GetClassForObject(first1).IsArray()) {
               int[] dim1 = ArrayDimensions(a_object);
               Object all1 = (Object)new Object[ArrayElementCount(dim1)];
               NormalizeNArray1(a_object, all1, 0, dim1, 0);
               return all1;
            }
         }
         return a_object;
      }
      
      public static void Compare(com.db4o.ObjectContainer a_con, Object a_Compare, Object a_With, String a_path, Collection4 a_list) {
         
            a_con.Activate(a_With, 1);
         
         if (a_list == null) {
            a_list = new Collection4();
         }
         if (a_list.ContainsByIdentity(a_Compare)) {
            return;
         }
         a_list.Add(a_Compare);
         if (a_path == null || a_path.Length < 1) if (a_Compare != null) {
            a_path = j4o.lang.Class.GetClassForObject(a_Compare).GetName() + ":";
         } else {
            if (a_With != null) a_path = j4o.lang.Class.GetClassForObject(a_With).GetName() + ":";
         }
         String path1 = a_path;
         if (a_Compare == null) if (a_With == null) {
            return;
         } else {
            Regression.AddError("1==null:" + path1);
            return;
         }
         if (a_With == null) {
            Regression.AddError("2==null:" + path1);
            return;
         }
         Class l_Class1 = j4o.lang.Class.GetClassForObject(a_Compare);
         if (!l_Class1.IsInstance(a_With)) {
            Regression.AddError("class!=:" + path1 + l_Class1.GetName() + ":" + j4o.lang.Class.GetClassForObject(a_With).GetName());
            return;
         }
         Field[] l_Fields1 = l_Class1.GetDeclaredFields();
         for (int i1 = 0; i1 < l_Fields1.Length; i1++) {
            if (StoreableField(l_Class1, l_Fields1[i1])) {
               Platform4.SetAccessible(l_Fields1[i1]);
               try {
                  {
                     path1 = a_path + l_Fields1[i1].GetName() + ":";
                     Object l_Compare1 = l_Fields1[i1].Get(a_Compare);
                     Object l_With1 = l_Fields1[i1].Get(a_With);
                     if (l_Compare1 == null) {
                        if (l_With1 != null) {
                           Regression.AddError("f1==null:" + path1);
                        }
                     } else if (l_With1 == null) Regression.AddError("f2==null:" + path1); else if (j4o.lang.Class.GetClassForObject(l_Compare1).IsArray()) {
                        if (!j4o.lang.Class.GetClassForObject(l_With1).IsArray()) {
                           Regression.AddError("f2!=array:" + path1);
                        } else {
                           l_Compare1 = NormalizeNArray(l_Compare1);
                           l_With1 = NormalizeNArray(l_With1);
                           int l_len1 = j4o.lang.reflect.JavaArray.GetLength(l_Compare1);
                           if (l_len1 != j4o.lang.reflect.JavaArray.GetLength(l_With1)) {
                              Regression.AddError("arraylen!=:" + path1);
                           } else {
                              bool l_persistentArray1 = HasPublicConstructor(l_Fields1[i1].GetFieldType().GetComponentType());
                              for (int j1 = 0; j1 < l_len1; j1++) {
                                 Object l_ElementCompare1 = j4o.lang.reflect.JavaArray.Get(l_Compare1, j1);
                                 Object l_ElementWith1 = j4o.lang.reflect.JavaArray.Get(l_With1, j1);
                                 if (l_persistentArray1) {
                                    Compare(a_con, l_ElementCompare1, l_ElementWith1, path1, a_list);
                                 } else if (l_ElementCompare1 == null) {
                                    if (l_ElementWith1 != null) {
                                       Regression.AddError("1e" + j1 + "==null:" + path1);
                                    }
                                 } else if (l_ElementWith1 == null) {
                                    Regression.AddError("2e" + j1 + "==null:" + path1);
                                 } else {
                                    Class elementCompareClass1 = j4o.lang.Class.GetClassForObject(l_ElementCompare1);
                                    if (elementCompareClass1 != j4o.lang.Class.GetClassForObject(l_ElementWith1)) {
                                       Regression.AddError("e" + j1 + "!=class:" + path1 + elementCompareClass1.ToString() + ":" + j4o.lang.Class.GetClassForObject(l_ElementWith1).ToString());
                                    } else if (HasPublicConstructor(elementCompareClass1)) {
                                       Compare(a_con, l_ElementCompare1, l_ElementWith1, path1, a_list);
                                    } else {
                                       if (!l_ElementCompare1.Equals(l_ElementWith1)) Regression.AddError("e" + j1 + "!=:" + path1 + l_ElementCompare1.ToString() + ":" + l_ElementWith1.ToString());
                                    }
                                 }
                              }
                           }
                        }
                     } else if (HasPublicConstructor(l_Fields1[i1].GetFieldType())) Compare(a_con, l_Compare1, l_With1, path1, a_list); else if (!l_Compare1.Equals(l_With1)) Regression.AddError("!=:" + path1);
                  }
               }  catch (Exception e) {
                  {
                     Regression.AddError("Exception:" + path1);
                  }
               }
            }
         }
      }
      
      static internal int[] ArrayDimensions(Object a_object) {
         int count1 = 0;
         for (Class clazz1 = j4o.lang.Class.GetClassForObject(a_object); clazz1.IsArray(); clazz1 = clazz1.GetComponentType()) count1++;
         int[] dim1 = new int[count1];
         for (int i1 = 0; i1 < count1; i1++) {
            dim1[i1] = j4o.lang.reflect.JavaArray.GetLength(a_object);
            a_object = j4o.lang.reflect.JavaArray.Get(a_object, 0);
         }
         return dim1;
      }
      
      static internal int NormalizeNArray1(Object a_object, Object a_all, int a_next, int[] a_dim, int a_index) {
         if (a_index == a_dim.Length - 1) {
			 for (int i1 = 0; i1 < a_dim[a_index]; i1++) ((Array)a_all).SetValue(j4o.lang.reflect.JavaArray.Get(a_object, i1), a_next++);
         } else {
            for (int i1 = 0; i1 < a_dim[a_index]; i1++) a_next = NormalizeNArray1(j4o.lang.reflect.JavaArray.Get(a_object, i1), a_all, a_next, a_dim, a_index + 1);
         }
         return a_next;
      }
      
      static internal int ArrayElementCount(int[] a_dim) {
         int elements1 = a_dim[0];
         for (int i1 = 1; i1 < a_dim.Length; i1++) elements1 *= a_dim[i1];
         return elements1;
      }
      
      static internal String Nl() {
         return j4o.lang.JavaSystem.GetProperty("line.separator");
      }

       public static bool StoreableField(Class a_class, Field a_field) {
           return (!Modifier.IsStatic(a_field.GetModifiers()))
               && (!Modifier.IsTransient(a_field.GetModifiers())
               & !(a_field.GetName().IndexOf("$") > -1));
       }


       private static Class[] SIMPLE_CLASSES = {
                                                   Class.GetClassForType(typeof(Boolean)),
                                                   Class.GetClassForType(typeof(Byte)),
                                                   Class.GetClassForType(typeof(Char)),
                                                   Class.GetClassForType(typeof(Double)),
                                                   Class.GetClassForType(typeof(Int16)),
                                                   Class.GetClassForType(typeof(Int32)),
                                                   Class.GetClassForType(typeof(Int64)),
                                                   Class.GetClassForType(typeof(SByte)),
                                                   Class.GetClassForType(typeof(Single)),
                                                   Class.GetClassForType(typeof(String)),
                                                   Class.GetClassForType(typeof(UInt32)),
                                                   Class.GetClassForType(typeof(UInt16)),
                                                   Class.GetClassForType(typeof(UInt64)),
                                                   Class.GetClassForType(typeof(j4o.util.Date))      };


   }
}
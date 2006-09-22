/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using j4o.lang;
using j4o.lang.reflect;
using j4o.util;
namespace com.db4o.test.soda.utils {

    public class STCompare {
      
        public STCompare() : base() {
        }
      
        public bool IsEqual(Object a_compare, Object a_with) {
            return IsEqual(a_compare, a_with, null, null);
        }
      
        public bool IsEqual(Object a_compare, Object a_with, String a_path, ArrayList a_list) {
            if (a_path == null || a_path.Length < 1) {
                if (a_compare != null) {
                    a_path = j4o.lang.Class.GetClassForObject(a_compare).GetName() + ":";
                } else {
                    if (a_with != null) {
                        a_path = j4o.lang.Class.GetClassForObject(a_with).GetName() + ":";
                    }
                }
            }
            String path1 = a_path;
            if (a_compare == null) {
                return a_with == null;
            }
            if (a_with == null) {
                return false;
            }
            Class clazz1 = j4o.lang.Class.GetClassForObject(a_compare);
            if (clazz1 != j4o.lang.Class.GetClassForObject(a_with)) {
                return false;
            }
            if (IsSimple(clazz1)) {
                return a_compare.Equals(a_with);
            }
            if (a_list == null) {
                a_list = new ArrayList();
            }
            if (a_list.Contains(a_compare)) {
                return true;
            }
            a_list.Add(a_compare);



            IEnumerable enumerableCompare = a_compare as IEnumerable;

            if(enumerableCompare != null){
                int xxx = 1;
            }

            IEnumerable enumerableWith = a_with as IEnumerable;
            if(enumerableCompare != null){
                ArrayList elementsWith = new ArrayList();
                IEnumerator enw = enumerableWith.GetEnumerator();
                while(enw.MoveNext()){
                    elementsWith.Add(enw.Current);
                }
                IEnumerator enc = enumerableCompare.GetEnumerator();
                while(enc.MoveNext()){
                    ArrayList temp = new ArrayList();
                    bool ok = false;
                    enw = elementsWith.GetEnumerator();
                    while(enw.MoveNext()){
                        if(!ok && IsEqual(enc.Current, enw.Current)){
                            ok = true;
                        } else{
                            temp.Add(enw.Current);
                        }
                    }
                    if(! ok){
                        return false;
                    }
                    elementsWith = temp;
                }
                return elementsWith.Count == 0;
            }

            Field[] fields1 = clazz1.GetDeclaredFields();
            for (int i1 = 0; i1 < fields1.Length; i1++) {
                if (StoreableField(clazz1, fields1[i1])) {
                    Platform4.SetAccessible(fields1[i1]);
                    try { {
                              path1 = a_path + fields1[i1].GetName() + ":";
                              Object compare1 = fields1[i1].Get(a_compare);
                              Object with1 = fields1[i1].Get(a_with);
                              if (compare1 == null) {
                                  if (with1 != null) {
                                      return false;
                                  }
                              } else if (with1 == null) {
                                  return false;
                              } else {
                                  if (j4o.lang.Class.GetClassForObject(compare1).IsArray()) {
                                      if (!j4o.lang.Class.GetClassForObject(with1).IsArray()) {
                                          return false;
                                      } else {
                                          compare1 = NormalizeNArray(compare1);
                                          with1 = NormalizeNArray(with1);
                                          int len1 = j4o.lang.reflect.JavaArray.GetLength(compare1);
                                          if (len1 != j4o.lang.reflect.JavaArray.GetLength(with1)) {
                                              return false;
                                          } else {
                                              for (int j1 = 0; j1 < len1; j1++) {
                                                  Object elementCompare1 = j4o.lang.reflect.JavaArray.Get(compare1, j1);
                                                  Object elementWith1 = j4o.lang.reflect.JavaArray.Get(with1, j1);
                                                  if (!IsEqual(elementCompare1, elementWith1, path1, a_list)) {
                                                      return false;
                                                  } else if (elementCompare1 == null) {
                                                      if (elementWith1 != null) {
                                                          return false;
                                                      }
                                                  } else if (elementWith1 == null) {
                                                      return false;
                                                  } else {
                                                      Class elementCompareClass1 = j4o.lang.Class.GetClassForObject(elementCompare1);
                                                      if (elementCompareClass1 != j4o.lang.Class.GetClassForObject(elementWith1)) {
                                                          return false;
                                                      }
                                                      if (HasPublicConstructor(elementCompareClass1)) {
                                                          if (!IsEqual(elementCompare1, elementWith1, path1, a_list)) {
                                                              return false;
                                                          }
                                                      } else if (!elementCompare1.Equals(elementWith1)) {
                                                          return false;
                                                      }
                                                  }
                                              }
                                          }
                                      }
                                  } else if (HasPublicConstructor(fields1[i1].GetFieldType())) {
                                      if (!IsEqual(compare1, with1, path1, a_list)) {
                                          return false;
                                      }
                                  } else {
                                      if (!compare1.Equals(with1)) {
                                          return false;
                                      }
                                  }
                              }
                          }
                    } catch (Exception e) { {
                                                System.Console.Error.WriteLine("STCompare failure executing path:" + path1);
                                                j4o.lang.JavaSystem.PrintStackTrace(e);
                                                return false;
                                            }
                    }
                }
            }
            return true;
        }
      
        internal bool HasPublicConstructor(Class a_class) {
            if (a_class != Class.GetClassForType(typeof(String))) {
                try { {
                          return a_class.NewInstance() != null;
                      }
                }  catch (Exception t) { {
                                         }
                }
            }
            return false;
        }

        internal Object NormalizeNArray(Object a_object) {
            Array arr = (Array)a_object;
            if(arr.Rank > 1){
                Object[] flat = new Object[arr.Length];
                int[] dim = ArrayDimensions(a_object);
                int[] currentDimensions = new int[dim.Length];
                NormalizeNArray1(arr, dim, 0, currentDimensions, flat, 0);
                return flat;
            }
            return arr;
        }

        protected static int NormalizeNArray1(
            Array shaped,
            int[] allDimensions,
            int currentDimension,
            int[] currentDimensions,
            Object[] flat,
            int flatElement) {
            if (currentDimension == (allDimensions.Length - 1)) {
                for (currentDimensions[currentDimension] = 0; currentDimensions[currentDimension] < allDimensions[currentDimension]; currentDimensions[currentDimension]++) {
                    flat[flatElement++] = shaped.GetValue(currentDimensions);
                }
            }else{
                for (currentDimensions[currentDimension] = 0; currentDimensions[currentDimension] < allDimensions[currentDimension]; currentDimensions[currentDimension]++) {
                    flatElement =
                        NormalizeNArray1(
                        shaped,
                        allDimensions,
                        currentDimension + 1,
                        currentDimensions,
                        flat,
                        flatElement);
                }
            }
            return flatElement;
        }

        public static int[] ArrayDimensions(Object obj) {
            Array array = (Array)obj;
            int[] dim = new int[array.Rank];
            for(int i = 0; i < dim.Length; i ++){
                dim[i] = array.GetLength(i);
            }
            return dim;
        }
      
        public bool StoreableField(Class a_class, Field a_field) {
            return !Modifier.IsStatic(a_field.GetModifiers()) && !Modifier.IsTransient(a_field.GetModifiers()) & !(a_field.GetName().IndexOf("__") > -1);
        }
      
        public static bool IsSimple(Class a_class) {
            for (int i1 = 0; i1 < SIMPLE_CLASSES.Length; i1++) {
                if (a_class == SIMPLE_CLASSES[i1]) {
                    return true;
                }
            }
            return false;
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
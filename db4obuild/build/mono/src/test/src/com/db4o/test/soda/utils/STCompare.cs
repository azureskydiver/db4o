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
using j4o.lang;
using j4o.lang.reflect;
using j4o.util;
namespace com.db4o.test.soda.utils {

    public class STCompare {
      
        public STCompare() : base() {
        }
      
        public bool isEqual(Object a_compare, Object a_with) {
            return isEqual(a_compare, a_with, null, null);
        }
      
        public bool isEqual(Object a_compare, Object a_with, String a_path, ArrayList a_list) {
            if (a_path == null || j4o.lang.JavaSystem.getLengthOf(a_path) < 1) {
                if (a_compare != null) {
                    a_path = j4o.lang.Class.getClassForObject(a_compare).getName() + ":";
                } else {
                    if (a_with != null) {
                        a_path = j4o.lang.Class.getClassForObject(a_with).getName() + ":";
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
            Class clazz1 = j4o.lang.Class.getClassForObject(a_compare);
            if (clazz1 != j4o.lang.Class.getClassForObject(a_with)) {
                return false;
            }
            if (isSimple(clazz1)) {
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
                        if(!ok && isEqual(enc.Current, enw.Current)){
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

            Field[] fields1 = clazz1.getDeclaredFields();
            for (int i1 = 0; i1 < fields1.Length; i1++) {
                if (storeableField(clazz1, fields1[i1])) {
                    Platform.setAccessible(fields1[i1]);
                    try { {
                              path1 = a_path + fields1[i1].getName() + ":";
                              Object compare1 = fields1[i1].get(a_compare);
                              Object with1 = fields1[i1].get(a_with);
                              if (compare1 == null) {
                                  if (with1 != null) {
                                      return false;
                                  }
                              } else if (with1 == null) {
                                  return false;
                              } else {
                                  if (j4o.lang.Class.getClassForObject(compare1).isArray()) {
                                      if (!j4o.lang.Class.getClassForObject(with1).isArray()) {
                                          return false;
                                      } else {
                                          compare1 = normalizeNArray(compare1);
                                          with1 = normalizeNArray(with1);
                                          int len1 = j4o.lang.reflect.JavaArray.getLength(compare1);
                                          if (len1 != j4o.lang.reflect.JavaArray.getLength(with1)) {
                                              return false;
                                          } else {
                                              for (int j1 = 0; j1 < len1; j1++) {
                                                  Object elementCompare1 = j4o.lang.reflect.JavaArray.get(compare1, j1);
                                                  Object elementWith1 = j4o.lang.reflect.JavaArray.get(with1, j1);
                                                  if (!isEqual(elementCompare1, elementWith1, path1, a_list)) {
                                                      return false;
                                                  } else if (elementCompare1 == null) {
                                                      if (elementWith1 != null) {
                                                          return false;
                                                      }
                                                  } else if (elementWith1 == null) {
                                                      return false;
                                                  } else {
                                                      Class elementCompareClass1 = j4o.lang.Class.getClassForObject(elementCompare1);
                                                      if (elementCompareClass1 != j4o.lang.Class.getClassForObject(elementWith1)) {
                                                          return false;
                                                      }
                                                      if (hasPublicConstructor(elementCompareClass1)) {
                                                          if (!isEqual(elementCompare1, elementWith1, path1, a_list)) {
                                                              return false;
                                                          }
                                                      } else if (!elementCompare1.Equals(elementWith1)) {
                                                          return false;
                                                      }
                                                  }
                                              }
                                          }
                                      }
                                  } else if (hasPublicConstructor(fields1[i1].getType())) {
                                      if (!isEqual(compare1, with1, path1, a_list)) {
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
                                                j4o.lang.JavaSystem.err.println("STCompare failure executing path:" + path1);
                                                j4o.lang.JavaSystem.printStackTrace(e);
                                                return false;
                                            }
                    }
                }
            }
            return true;
        }
      
        internal bool hasPublicConstructor(Class a_class) {
            if (a_class != Class.getClassForType(typeof(String))) {
                try { {
                          return a_class.newInstance() != null;
                      }
                }  catch (Exception t) { {
                                         }
                }
            }
            return false;
        }

        internal Object normalizeNArray(Object a_object) {
            Array arr = (Array)a_object;
            if(arr.Rank > 1){
                Object[] flat = new Object[arr.Length];
                int[] dim = arrayDimensions(a_object);
                int[] currentDimensions = new int[dim.Length];
                normalizeNArray1(arr, dim, 0, currentDimensions, flat, 0);
                return flat;
            }
            return arr;
        }

        protected static int normalizeNArray1(
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
                        normalizeNArray1(
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

        public static int[] arrayDimensions(Object obj) {
            Array array = (Array)obj;
            int[] dim = new int[array.Rank];
            for(int i = 0; i < dim.Length; i ++){
                dim[i] = array.GetLength(i);
            }
            return dim;
        }
      
        public bool storeableField(Class a_class, Field a_field) {
            return !Modifier.isStatic(a_field.getModifiers()) && !Modifier.isTransient(a_field.getModifiers()) & !(a_field.getName().IndexOf("__") > -1);
        }
      
        public static bool isSimple(Class a_class) {
            for (int i1 = 0; i1 < SIMPLE_CLASSES.Length; i1++) {
                if (a_class == SIMPLE_CLASSES[i1]) {
                    return true;
                }
            }
            return false;
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
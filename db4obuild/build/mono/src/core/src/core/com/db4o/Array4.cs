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
using com.db4o.reflect;
namespace com.db4o {

    internal class Array4 {
      
        static internal IArray i_reflector;
      
        public static int[] dimensions(Object obj) {
            Array array = (Array)obj;
            int[] dim = new int[array.Rank];
            for(int i = 0; i < dim.Length; i ++){
                dim[i] = array.GetLength(i);
            }
            return dim;
        }
      
        public static int flatten(
            Object shaped,
            int[] dimensions,
            int currentDimension,
            Object[] flat,
            int flatElement) {
            int[] currentDimensions = new int[dimensions.Length];
            flatten1((Array)shaped, dimensions, 0, currentDimensions, flat, 0);
            return 0;
        }

        protected static int flatten1(
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
                        flatten1(
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

      
        public static Class getComponentType(Class var_class) {
            return var_class.getComponentType();
        }
      
        public static bool isNDimensional(Class var_class) {
            return Compat.getArrayRank(var_class.getNetType()) > 1;
        }
      
        public static IArray reflector() {
            if (i_reflector == null) i_reflector = ((Config4Impl)Db4o.configure()).reflector().array();
            return i_reflector;
        }
      
        public static int shape(
            Object[] flat,
            int flatElement,
            Object shaped,
            int[] allDimensions,
            int currentDimension) {
            int[] currentDimensions = new int[allDimensions.Length];
            shape1(flat, 0, (Array)shaped, allDimensions, 0, currentDimensions);
            return 0;
        }

        public static int shape1(
            Object[] flat,
            int flatElement,
            Array shaped,
            int[] allDimensions,
            int currentDimension,
            int[] currentDimensions) {
            if (currentDimension == (allDimensions.Length - 1)) {
                for (currentDimensions[currentDimension] = 0; currentDimensions[currentDimension] < allDimensions[currentDimension]; currentDimensions[currentDimension]++) {
                    shaped.SetValue(flat[flatElement++], currentDimensions);
                }
            }else{
                for (currentDimensions[currentDimension] = 0; currentDimensions[currentDimension] < allDimensions[currentDimension]; currentDimensions[currentDimension]++) {
                    flatElement =
                        shape1(
                        flat,
                        flatElement,
                        shaped,
                        allDimensions,
                        currentDimension + 1,
                        currentDimensions
                        );
                }
            }
            return flatElement;
        }
    }
}
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
namespace com.db4o {

   internal class YapArrayN : YapArray {
      
      internal YapArrayN(YapDataType yapdatatype, bool xbool) : base(yapdatatype, xbool) {
      }
      
      internal override Object[] allElements(Object obj) {
         int[] xis1 = Array4.dimensions(obj);
         Object[] objs1 = new Object[elementCount(xis1)];
         Array4.flatten(obj, xis1, 0, objs1, 0);
         return objs1;
      }
      
      internal override int elementCount(Transaction transaction, YapReader yapreader) {
         return elementCount(readDimensions(transaction, yapreader, new Class[1]));
      }
      
      internal int elementCount(int[] xis) {
         int i1 = xis[0];
         for (int i_0_1 = 1; i_0_1 < xis.Length; i_0_1++) i1 *= xis[i_0_1];
         return i1;
      }
      
      internal override byte identifier() {
         return (byte)90;
      }
      
      internal override int objectLength(Object obj) {
         int[] xis1 = Array4.dimensions(obj);
         return 0 + 4 * (2 + xis1.Length) + elementCount(xis1) * i_handler.linkLength();
      }
      
      internal override Object read1(YapWriter yapwriter) {
         Object[] objs1 = new Object[1];
         int[] xis1 = read1Create(yapwriter.getTransaction(), yapwriter, objs1);
         if (objs1[0] != null) {
            Object[] objs_1_1 = new Object[elementCount(xis1)];
            for (int i1 = 0; i1 < objs_1_1.Length; i1++) objs_1_1[i1] = i_handler.read(yapwriter);
            Array4.shape(objs_1_1, 0, objs1[0], xis1, 0);
         }
         return objs1[0];
      }
      
      internal override Object read1Query(Transaction transaction, YapReader yapreader) {
         Object[] objs1 = new Object[1];
         int[] xis1 = read1Create(transaction, yapreader, objs1);
         if (objs1[0] != null) {
            Object[] objs_2_1 = new Object[elementCount(xis1)];
            for (int i1 = 0; i1 < objs_2_1.Length; i1++) objs_2_1[i1] = i_handler.readQuery(transaction, yapreader, true);
            Array4.shape(objs_2_1, 0, objs1[0], xis1, 0);
         }
         return objs1[0];
      }
      
      private int[] read1Create(Transaction transaction, YapReader yapreader, Object[] objs) {
         Class[] var_classes1 = new Class[1];
         int[] xis1 = readDimensions(transaction, yapreader, var_classes1);
         if (i_isPrimitive) objs[0] = Array4.reflector().newInstance(i_handler.getPrimitiveJavaClass(), xis1); else if (var_classes1[0] != null) objs[0] = Array4.reflector().newInstance(var_classes1[0], xis1);
         return xis1;
      }
      
      private int[] readDimensions(Transaction transaction, YapReader yapreader, Class[] var_classes) {
         int[] xis1 = new int[this.readElementsAndClass(transaction, yapreader, var_classes)];
         for (int i1 = 0; i1 < xis1.Length; i1++) xis1[i1] = yapreader.readInt();
         return xis1;
      }
      
      internal override void writeNew1(Object obj, YapWriter yapwriter) {
         int[] xis1 = Array4.dimensions(obj);
         this.writeClass(obj, yapwriter);
         yapwriter.writeInt(xis1.Length);
         for (int i1 = 0; i1 < xis1.Length; i1++) yapwriter.writeInt(xis1[i1]);
         Object[] objs1 = allElements(obj);
         for (int i1 = 0; i1 < objs1.Length; i1++) i_handler.writeNew(element(objs1, i1), yapwriter);
      }
      
      private Object element(Object obj, int i) {
         try {
            {
               return Array4.reflector().get(obj, i);
            }
         }  catch (Exception exception) {
            {
               return null;
            }
         }
      }
   }
}
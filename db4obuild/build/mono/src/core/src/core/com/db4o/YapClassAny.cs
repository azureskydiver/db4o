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

   internal class YapClassAny : YapClass {
      
      internal YapClassAny() : base() {
      }
      
      public override bool canHold(Class var_class) {
         return true;
      }
      
      public static void appendEmbedded(YapWriter yapwriter) {
         YapClass yapclass1 = readYapClass(yapwriter);
         if (yapclass1 != null) yapclass1.appendEmbedded1(yapwriter);
      }
      
      public override void cascadeActivation(Transaction transaction, Object obj, int i, bool xbool) {
         YapClass yapclass1 = transaction.i_stream.getYapClass(j4o.lang.Class.getClassForObject(obj), false);
         if (yapclass1 != null) yapclass1.cascadeActivation(transaction, obj, i, xbool);
      }
      
      public override void deleteEmbedded(YapWriter yapwriter) {
         int i1 = yapwriter.readInt();
         if (i1 > 0) {
            YapWriter yapwriter_0_1 = yapwriter.getStream().readWriterByID(yapwriter.getTransaction(), i1);
            if (yapwriter_0_1 != null) {
               yapwriter_0_1.setCascadeDeletes(yapwriter.cascadeDeletes());
               YapClass yapclass1 = readYapClass(yapwriter_0_1);
               if (yapclass1 != null) yapclass1.deleteEmbedded1(yapwriter_0_1, i1);
            }
         }
      }
      
      public override int getID() {
         return 11;
      }
      
      public override Class getJavaClass() {
         return YapConst.CLASS_OBJECT;
      }
      
      public override bool hasField(YapStream yapstream, String xstring) {
         return yapstream.i_classCollection.fieldExists(xstring);
      }
      
      internal override bool hasIndex() {
         return false;
      }
      
      public override bool holdsAnyClass() {
         return true;
      }
      
      internal override bool isStrongTyped() {
         return false;
      }
      
      public override YapDataType readArrayWrapper(Transaction transaction, YapReader[] yapreaders) {
         int i1 = 0;
         int i_1_1 = yapreaders[0]._offset;
         try {
            {
               i1 = yapreaders[0].readInt();
            }
         }  catch (Exception exception) {
            {
            }
         }
         yapreaders[0]._offset = i_1_1;
         if (i1 != 0) {
            YapWriter yapwriter1 = transaction.i_stream.readWriterByID(transaction, i1);
            if (yapwriter1 != null) {
               YapClass yapclass1 = readYapClass(yapwriter1);
               try {
                  {
                     if (yapclass1 != null) {
                        yapreaders[0] = yapwriter1;
                        return yapclass1.readArrayWrapper1(yapreaders);
                     }
                  }
               }  catch (Exception exception) {
                  {
                  }
               }
            }
         }
         return null;
      }
      
      static internal YapClass readYapClass(YapWriter yapwriter) {
         return yapwriter.getStream().getYapClass(yapwriter.readInt());
      }
      
      public override bool supportsIndex() {
         return false;
      }
   }
}
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

   public class MetaClass : Internal {
      public String name;
      public MetaField[] fields;
      
      public MetaClass() : base() {
      }
      
      public MetaClass(String xstring) : base() {
         name = xstring;
      }
      
      internal MetaField ensureField(Transaction transaction, String xstring) {
         if (fields != null) {
            for (int i1 = 0; i1 < fields.Length; i1++) {
               if (fields[i1].name.Equals(xstring)) return fields[i1];
            }
            MetaField[] metafields1 = new MetaField[fields.Length + 1];
            j4o.lang.JavaSystem.arraycopy(fields, 0, metafields1, 0, fields.Length);
            fields = metafields1;
         } else fields = new MetaField[1];
         MetaField metafield1 = new MetaField(xstring);
         fields[fields.Length - 1] = metafield1;
         transaction.i_stream.setInternal(transaction, metafield1, -2147483548, false);
         transaction.i_stream.setInternal(transaction, this, -2147483548, false);
         return metafield1;
      }
   }
}
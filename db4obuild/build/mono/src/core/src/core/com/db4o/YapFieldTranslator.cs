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
using com.db4o.config;
namespace com.db4o {

   internal class YapFieldTranslator : YapField {
      private ObjectTranslator i_translator;
      
      internal YapFieldTranslator(YapClass yapclass, ObjectTranslator objecttranslator) : base(yapclass, objecttranslator) {
         i_translator = objecttranslator;
         this.configure(objecttranslator.storedClass());
      }
      
      internal override void deactivate(Transaction transaction, Object obj, int i) {
         if (i > 0) this.cascadeActivation(transaction, obj, i, false);
         setOn(transaction.i_stream, obj, null);
      }
      
      internal override Object getOn(Transaction transaction, Object obj) {
         try {
            {
               return i_translator.onStore(transaction.i_stream, obj);
            }
         }  catch (Exception throwable) {
            {
               return null;
            }
         }
      }
      
      internal override Object getOrCreate(Transaction transaction, Object obj) {
         return getOn(transaction, obj);
      }
      
      internal override void instantiate(YapObject yapobject, Object obj, YapWriter yapwriter) {
         Object obj_0_1 = this.read(yapwriter);
         yapwriter.getStream().activate2(yapwriter.getTransaction(), obj_0_1, yapwriter.getInstantiationDepth());
         setOn(yapwriter.getStream(), obj, obj_0_1);
      }
      
      internal override void refresh() {
      }
      
      private void setOn(YapStream yapstream, Object obj, Object obj_1_) {
         try {
            {
               i_translator.onActivate(yapstream, obj, obj_1_);
            }
         }  catch (Exception throwable) {
            {
            }
         }
      }
   }
}
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

   internal class MCreateClass : MsgD {
      
      internal MCreateClass() : base() {
      }
      
      internal override bool processMessageAtServer(YapSocket yapsocket) {
         Class var_class1 = null;
         YapStream yapstream1 = this.getStream();
         Transaction transaction1 = yapstream1.getSystemTransaction();
         YapWriter yapwriter1 = new YapWriter(transaction1, 0);
         try {
            {
               var_class1 = Db4o.classForName(yapstream1, this.readString());
            }
         }  catch (Exception exception) {
            {
            }
         }
         if (var_class1 != null) {
            lock (yapstream1.i_lock) {
               try {
                  {
                     YapClass yapclass1 = yapstream1.getYapClass(var_class1, true);
                     if (yapclass1 != null) {
                        yapstream1.checkStillToSet();
                        yapclass1.setStateDirty();
                        yapclass1.write(yapstream1, transaction1);
                        transaction1.commit();
                        yapwriter1 = yapstream1.readWriterByID(transaction1, yapclass1.getID());
                     }
                  }
               }  catch (Exception throwable) {
                  {
                  }
               }
            }
         }
         Msg.OBJECT_TO_CLIENT.getWriter(yapwriter1).write(yapstream1, yapsocket);
         return true;
      }
   }
}
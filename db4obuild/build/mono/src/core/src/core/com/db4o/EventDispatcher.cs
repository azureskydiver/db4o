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

   internal class EventDispatcher {
      private static String[] events = {
         "objectCanDelete",
"objectOnDelete",
"objectOnActivate",
"objectOnDeactivate",
"objectOnNew",
"objectOnUpdate",
"objectCanActivate",
"objectCanDeactivate",
"objectCanNew",
"objectCanUpdate"      };
      static internal int CAN_DELETE = 0;
      static internal int DELETE = 1;
      static internal int SERVER_COUNT = 2;
      static internal int ACTIVATE = 2;
      static internal int DEACTIVATE = 3;
      static internal int NEW = 4;
      static internal int UPDATE = 5;
      static internal int CAN_ACTIVATE = 6;
      static internal int CAN_DEACTIVATE = 7;
      static internal int CAN_NEW = 8;
      static internal int CAN_UPDATE = 9;
      static internal int COUNT = 10;
      private IMethod[] methods;
      
      internal EventDispatcher(IMethod[] imethods) : base() {
         methods = imethods;
      }
      
      internal bool dispatch(YapStream yapstream, Object obj, int i) {
         if (methods[i] != null) {
            Object[] objs1 = {
               yapstream            };
            try {
               {
                  Object obj_0_1 = methods[i].invoke(obj, objs1);
                  if (obj_0_1 != null && obj_0_1 is Boolean) return System.Convert.ToBoolean((Boolean)obj_0_1);
               }
            }  catch (Exception throwable) {
               {
               }
            }
         }
         return true;
      }
      
      static internal EventDispatcher forClass(YapStream yapstream, IClass iclass) {
         EventDispatcher eventdispatcher1 = null;
         if (yapstream != null) {
            int i1 = 0;
            if (yapstream.i_config.i_callbacks) i1 = 10; else if (yapstream.i_config.i_isServer) i1 = 2;
            if (i1 > 0) {
               Class[] var_classes1 = {
                  YapConst.CLASS_OBJECTCONTAINER               };
               IMethod[] imethods1 = new IMethod[10];
               for (int i_1_1 = 9; i_1_1 >= 0; i_1_1--) {
                  try {
                     {
                        imethods1[i_1_1] = iclass.getMethod(events[i_1_1], var_classes1);
                        if (eventdispatcher1 == null) eventdispatcher1 = new EventDispatcher(imethods1);
                     }
                  }  catch (Exception throwable) {
                     {
                     }
                  }
               }
            }
         }
         return eventdispatcher1;
      }
   }
}
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
using System.Reflection;
using j4o.lang;

namespace com.db4o {

	/// <exclude />
    public class Dynamic {

        public static object GetProperty(object obj, string prop){
            if(obj != null){
                Type type = typeForObject(obj);
                try{
                    PropertyInfo pi = type.GetProperty(prop, BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Instance | BindingFlags.Static);
                    return pi.GetValue(obj,null);
                }catch(Exception e){
                }
            }
            return null;
        }

        public static void SetProperty(object obj, string prop, object val){
            if(obj != null){
                Type type = typeForObject(obj);
                try{
                    PropertyInfo pi = type.GetProperty(prop, BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Instance | BindingFlags.Static);
                    pi.SetValue(obj, val, null);
                }catch(Exception e){
                }
            }
        }

        private static Type typeForObject(object obj){
            Type type = obj as Type;
            if(type != null){
                return type;
            }
            Class clazz = obj as Class;
            if(clazz != null){
                return clazz.getNetType();
            }
            return obj.GetType();
        }
    }
}

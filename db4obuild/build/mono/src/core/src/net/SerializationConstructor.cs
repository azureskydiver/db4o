/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
using System;

namespace com.db4o.reflect.net
{
	/// <summary>Constructs objects by using System.Runtime.Serialization.FormatterServices.GetUninitializedObject
	/// and bypasses calls to user contructors this way. Not available on CompactFramework
	/// </summary>
	public class SerializationConstructor : com.db4o.reflect.ReflectConstructor
	{
        private Type _type;

		public SerializationConstructor(Type type){
            _type = type;
		}

        public virtual com.db4o.reflect.ReflectClass[] getParameterTypes() {
            return null;
        }

        public virtual void setAccessible() {
            // do nothing
        }

        public virtual object newInstance(object[] parameters) {
            return System.Runtime.Serialization.FormatterServices.GetUninitializedObject(_type);
        }
	}
}

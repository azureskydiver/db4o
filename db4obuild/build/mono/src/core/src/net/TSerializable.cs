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
using System.IO;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Formatters.Binary;

using j4o.lang;
using com.db4o.config;

namespace com.db4o
{

    /// <summary>
    /// translator for types that are marked with the Serializable attribute.
    /// The Serializable translator is provided to allow persisting objects that
    /// do not supply a convenient constructor. The use of this translator is
    /// recommended only if:<br />
    /// - the persistent type will never be refactored<br />
    /// - querying for type members is not necessary<br />
    /// </summary>
    /// <exclude />
    public class TSerializable : ObjectConstructor {

		static Class _byteArrayType = Class.getClassForType(typeof(byte[]));

        public Object onStore(ObjectContainer objectContainer, Object obj){
            MemoryStream memoryStream = new MemoryStream();
            new BinaryFormatter().Serialize(memoryStream, obj);
            return memoryStream.GetBuffer();
        }

        public void onActivate(ObjectContainer objectContainer, Object obj, Object members){
        }

        public Object onInstantiate(ObjectContainer objectContainer, Object obj){
            MemoryStream memoryStream = new MemoryStream((byte[])obj);
            return new BinaryFormatter().Deserialize(memoryStream);
        }

        public Class storedClass(){
            return _byteArrayType;
        }

	}
}


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

        public Object onStore(ObjectContainer objectContainer, Object obj){
            MemoryStream memoryStream = new MemoryStream();
            new BinaryFormatter().Serialize(memoryStream, obj);
            memoryStream.Close();
            return memoryStream.GetBuffer();
        }

        public void onActivate(ObjectContainer objectContainer, Object obj, Object members){
        }

        public Object onInstantiate(ObjectContainer objectContainer, Object obj){
            MemoryStream memoryStream = new MemoryStream((byte[])obj);
            Object ret = new BinaryFormatter().Deserialize(memoryStream);
            memoryStream.Close();
            return ret;
        }

        public Class storedClass(){
            return Class.getClassForType(typeof(byte[]));
        }

	}
}

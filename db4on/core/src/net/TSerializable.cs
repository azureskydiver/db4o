/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.IO;

using j4o.lang;
using com.db4o.config;

namespace com.db4o.config
{
#if !CF_1_0 && !CF_2_0
	using System.Runtime.Serialization;
	using System.Runtime.Serialization.Formatters.Binary;

	/// <summary>
	/// translator for types that are marked with the Serializable attribute.
	/// The Serializable translator is provided to allow persisting objects that
	/// do not supply a convenient constructor. The use of this translator is
	/// recommended only if:<br />
	/// - the persistent type will never be refactored<br />
	/// - querying for type members is not necessary<br />
	/// </summary>
	public class TSerializable : ObjectConstructor
	{
		static Class _byteArrayType = Class.GetClassForType(typeof(byte[]));

		public Object OnStore(ObjectContainer objectContainer, Object obj)
		{
			MemoryStream memoryStream = new MemoryStream();
			new BinaryFormatter().Serialize(memoryStream, obj);
			return memoryStream.GetBuffer();
		}

		public void OnActivate(ObjectContainer objectContainer, Object obj, Object members)
		{
		}

		public Object OnInstantiate(ObjectContainer objectContainer, Object obj)
		{
			MemoryStream memoryStream = new MemoryStream((byte[])obj);
			return new BinaryFormatter().Deserialize(memoryStream);
		}

		public Class StoredClass()
		{
			return _byteArrayType;
		}

	}
#endif
}


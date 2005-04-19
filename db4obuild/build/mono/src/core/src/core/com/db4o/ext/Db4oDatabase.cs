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
namespace com.db4o.ext
{
	/// <summary>Class to identify a database by it's signature.</summary>
	/// <remarks>
	/// Class to identify a database by it's signature.
	/// <br /><br />db4o UUID handling uses a reference to the Db4oDatabase object, that
	/// represents the database an object was created on.
	/// </remarks>
	public class Db4oDatabase : com.db4o.types.Db4oType, com.db4o.Internal
	{
		/// <summary>Field is public for implementation reasons, DO NOT TOUCH!</summary>
		public byte[] i_signature;

		/// <summary>Field is public for implementation reasons, DO NOT TOUCH!</summary>
		public long i_uuid;

		/// <summary>cached ObjectContainer for getting the own ID.</summary>
		/// <remarks>cached ObjectContainer for getting the own ID.</remarks>
		[com.db4o.Transient]
		private com.db4o.YapStream i_stream;

		/// <summary>cached ID, only valid in combination with i_objectContainer</summary>
		[com.db4o.Transient]
		private int i_id;

		/// <summary>generates a new Db4oDatabase object with a unique signature.</summary>
		/// <remarks>generates a new Db4oDatabase object with a unique signature.</remarks>
		public static com.db4o.ext.Db4oDatabase generate()
		{
			com.db4o.ext.Db4oDatabase db = new com.db4o.ext.Db4oDatabase();
			db.i_signature = com.db4o.Unobfuscated.generateSignature();
			db.i_uuid = j4o.lang.JavaSystem.currentTimeMillis();
			return db;
		}

		/// <summary>comparison by signature.</summary>
		/// <remarks>comparison by signature.</remarks>
		public override bool Equals(object obj)
		{
			if (obj == this)
			{
				return true;
			}
			if (obj == null || j4o.lang.Class.getClassForObject(this) != j4o.lang.Class.getClassForObject
				(obj))
			{
				return false;
			}
			com.db4o.ext.Db4oDatabase other = (com.db4o.ext.Db4oDatabase)obj;
			if (other.i_signature == null || this.i_signature == null)
			{
				return false;
			}
			if (other.i_signature.Length != i_signature.Length)
			{
				return false;
			}
			for (int i = 0; i < i_signature.Length; i++)
			{
				if (i_signature[i] != other.i_signature[i])
				{
					return false;
				}
			}
			return true;
		}

		/// <summary>gets the db4o ID, and may cache it for performance reasons.</summary>
		/// <remarks>gets the db4o ID, and may cache it for performance reasons.</remarks>
		/// <param name="a_oc">the ObjectContainer</param>
		/// <returns>the db4o ID for the ObjectContainer</returns>
		public virtual int getID(com.db4o.Transaction trans)
		{
			com.db4o.YapStream stream = trans.i_stream;
			if (stream != i_stream)
			{
				i_stream = stream;
				i_id = trans.ensureDb4oDatabase(this);
			}
			return i_id;
		}

		public override string ToString()
		{
			return "Db4oDatabase: " + i_signature;
		}

		public virtual bool isOlderThan(com.db4o.ext.Db4oDatabase peer)
		{
			if (i_uuid != peer.i_uuid)
			{
				return i_uuid < peer.i_uuid;
			}
			if (i_signature.Length != peer.i_signature.Length)
			{
				return i_signature.Length < peer.i_signature.Length;
			}
			for (int i = 0; i < i_signature.Length; i++)
			{
				if (i_signature[i] != peer.i_signature[i])
				{
					return i_signature[i] < peer.i_signature[i];
				}
			}
			throw new j4o.lang.RuntimeException();
		}
	}
}

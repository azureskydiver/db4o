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
	/// <summary>a unique universal identify for an object.</summary>
	/// <remarks>
	/// a unique universal identify for an object.
	/// <br /><br />The db4o UUID consists of two parts:<br />
	/// - an indexed long for fast access,<br />
	/// - the signature of the
	/// <see cref="com.db4o.ObjectContainer">ObjectContainer</see>
	/// the object
	/// was created with.
	/// <br /><br />Db4oUUIDs are valid representations of objects
	/// over multiple ObjectContainers
	/// </remarks>
	public class Db4oUUID
	{
		private readonly long longPart;

		private readonly byte[] signaturePart;

		public Db4oUUID(long longPart, byte[] signaturePart)
		{
			this.longPart = longPart;
			this.signaturePart = signaturePart;
		}

		/// <summary>returns the long part of this UUID.</summary>
		/// <remarks>
		/// returns the long part of this UUID.
		/// <br /><br />To uniquely identify an object universally, db4o
		/// uses an indexed long and a reference to the
		/// <see cref="com.db4o.ext.Db4oDatabase">com.db4o.ext.Db4oDatabase</see>
		/// object it was created on.
		/// </remarks>
		/// <returns>the long part of this UUID.</returns>
		public virtual long getLongPart()
		{
			return longPart;
		}

		/// <summary>returns the signature part of this UUID.</summary>
		/// <remarks>
		/// returns the signature part of this UUID.
		/// <br /><br />
		/// <br /><br />To uniquely identify an object universally, db4o
		/// uses an indexed long and a reference to the
		/// <see cref="com.db4o.ext.Db4oDatabase">com.db4o.ext.Db4oDatabase</see>
		/// singleton object of the
		/// <see cref="com.db4o.ObjectContainer">ObjectContainer</see>
		/// it was created on.
		/// This method returns the signature of the Db4oDatabase object of
		/// the ObjectContainer: the signature of the origin ObjectContainer.
		/// </remarks>
		/// <returns>the signature of the Db4oDatabase for this UUID.</returns>
		public virtual byte[] getSignaturePart()
		{
			return signaturePart;
		}
	}
}

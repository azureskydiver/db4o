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
	/// <summary>extended factory class with static methods to open special db4o sessions.
	/// 	</summary>
	/// <remarks>extended factory class with static methods to open special db4o sessions.
	/// 	</remarks>
	public sealed class ExtDb4o : com.db4o.Db4o
	{
		/// <summary>
		/// opens an
		/// <see cref="com.db4o.ObjectContainer">com.db4o.ObjectContainer</see>
		/// for in-memory use .
		/// <br /><br />In-memory ObjectContainers are useful for maximum performance
		/// on small databases, for swapping objects or for storing db4o format data
		/// to other media or other databases.<br /><br />Be aware of the danger of running
		/// into OutOfMemory problems or complete loss of all data, in case of hardware
		/// or JVM failures.<br /><br />
		/// </summary>
		/// <param name="memoryFile">
		/// a
		/// <see cref="com.db4o.ext.MemoryFile">MemoryFile</see>
		/// 
		/// to store the raw byte data.
		/// </param>
		/// <returns>
		/// an open
		/// <see cref="com.db4o.ObjectContainer">ObjectContainer</see>
		/// </returns>
		/// <seealso cref="com.db4o.ext.MemoryFile">com.db4o.ext.MemoryFile</seealso>
		public static com.db4o.ObjectContainer openMemoryFile(com.db4o.ext.MemoryFile memoryFile
			)
		{
			return openMemoryFile1(memoryFile);
		}
	}
}

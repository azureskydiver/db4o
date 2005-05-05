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
namespace com.db4o
{
	/// <exclude></exclude>
	public class MigrationConnection
	{
		private readonly com.db4o.Hashtable4 _referenceMap;

		internal MigrationConnection()
		{
			_referenceMap = new com.db4o.Hashtable4(1);
		}

		public virtual void mapReference(object obj, com.db4o.YapObject _ref)
		{
			_referenceMap.put(j4o.lang.JavaSystem.identityHashCode(obj), _ref);
		}

		public virtual com.db4o.YapObject referenceFor(object obj)
		{
			int hcode = j4o.lang.JavaSystem.identityHashCode(obj);
			com.db4o.YapObject _ref = (com.db4o.YapObject)_referenceMap.get(hcode);
			_referenceMap.remove(hcode);
			return _ref;
		}
	}
}

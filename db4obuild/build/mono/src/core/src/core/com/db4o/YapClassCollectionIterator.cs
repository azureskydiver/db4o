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
	internal class YapClassCollectionIterator : com.db4o.Iterator4
	{
		private readonly com.db4o.YapClassCollection i_collection;

		internal YapClassCollectionIterator(com.db4o.YapClassCollection a_collection, com.db4o.List4
			 a_first) : base(a_first)
		{
			i_collection = a_collection;
		}

		internal virtual com.db4o.YapClass nextClass()
		{
			com.db4o.YapClass yc = (com.db4o.YapClass)next();
			i_collection.readYapClass(yc, null);
			return yc;
		}
	}
}

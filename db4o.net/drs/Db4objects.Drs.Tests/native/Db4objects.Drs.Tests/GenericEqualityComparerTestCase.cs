/* Copyright (C) 2004 - 2008  Versant Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to Versant, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
using System;
using System.Collections.Generic;
using System.Text;
using Db4objects.Db4o;
using Db4oUnit;

namespace Db4objects.Drs.Tests
{
	class GenericEqualityComparerTestCase : DrsTestCase
	{
		public class Item
		{
			public IEqualityComparer<string> comparer;

			public Item(IEqualityComparer<string> comparer)
			{
				this.comparer = comparer;
			}
		}

		public void Test()
		{
			A().Provider().StoreNew(new Item(EqualityComparer<string>.Default));
			A().Provider().Commit();

			ReplicateAll(A().Provider(), B().Provider());

			IObjectSet found = B().Provider().GetStoredObjects(typeof(Item));
			Assert.AreEqual(1, found.Count);

			Item item = (Item) found[0];
			Assert.IsNotNull(item.comparer);
		}
	}
}

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
	internal class Order : com.db4o.Orderable
	{
		private int i_major;

		private int i_minor;

		public virtual int compareTo(object obj)
		{
			if (obj is com.db4o.Order)
			{
				com.db4o.Order other = (com.db4o.Order)obj;
				int res = i_major - other.i_major;
				if (res != 0)
				{
					return res;
				}
				return i_minor - other.i_minor;
			}
			return 1;
		}

		public virtual void hintOrder(int a_order, bool a_major)
		{
			if (a_major)
			{
				i_major = a_order;
			}
			else
			{
				i_minor = a_order;
			}
		}

		public virtual bool hasDuplicates()
		{
			return true;
		}
	}
}

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
	public class P1HashElement : com.db4o.P1ListElement
	{
		public object i_key;

		public int i_hashCode;

		public int i_position;

		public P1HashElement()
		{
		}

		public P1HashElement(com.db4o.Transaction a_trans, com.db4o.P1ListElement a_next, 
			object a_key, int a_hashCode, object a_object) : base(a_trans, a_next, a_object)
		{
			i_hashCode = a_hashCode;
			i_key = a_key;
		}

		public override int adjustReadDepth(int a_depth)
		{
			return 1;
		}

		internal virtual object activatedKey(int a_depth)
		{
			checkActive();
			activate(i_key, a_depth);
			return i_key;
		}

		internal override void delete(bool a_deleteRemoved)
		{
			if (a_deleteRemoved)
			{
				delete(i_key);
			}
			base.delete(a_deleteRemoved);
		}
	}
}

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
	/// <summary>element of linked lists</summary>
	/// <exclude></exclude>
	public class P1ListElement : com.db4o.P1Object
	{
		public com.db4o.P1ListElement i_next;

		public object i_object;

		public P1ListElement()
		{
		}

		public P1ListElement(com.db4o.Transaction a_trans, com.db4o.P1ListElement a_next, 
			object a_object) : base(a_trans)
		{
			i_next = a_next;
			i_object = a_object;
		}

		public override int adjustReadDepth(int a_depth)
		{
			if (a_depth >= 1)
			{
				return 1;
			}
			return 0;
		}

		internal virtual object activatedObject(int a_depth)
		{
			checkActive();
			activate(i_object, a_depth);
			return i_object;
		}

		public override object createDefault(com.db4o.Transaction a_trans)
		{
			com.db4o.P1ListElement elem4 = new com.db4o.P1ListElement();
			elem4.setTrans(a_trans);
			return elem4;
		}

		internal virtual void delete(bool a_deleteRemoved)
		{
			if (a_deleteRemoved)
			{
				delete(i_object);
			}
			delete();
		}
	}
}

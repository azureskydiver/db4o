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
	internal abstract class Config4Abstract
	{
		internal int i_cascadeOnActivate = 0;

		internal int i_cascadeOnDelete = 0;

		internal int i_cascadeOnUpdate = 0;

		internal string i_name;

		public virtual void cascadeOnActivate(bool flag)
		{
			i_cascadeOnActivate = flag ? 1 : -1;
		}

		public virtual void cascadeOnDelete(bool flag)
		{
			i_cascadeOnDelete = flag ? 1 : -1;
		}

		public virtual void cascadeOnUpdate(bool flag)
		{
			i_cascadeOnUpdate = flag ? 1 : -1;
		}

		internal abstract string className();

		public override bool Equals(object obj)
		{
			return i_name.Equals(((com.db4o.Config4Abstract)obj).i_name);
		}

		public virtual string getName()
		{
			return i_name;
		}
	}
}

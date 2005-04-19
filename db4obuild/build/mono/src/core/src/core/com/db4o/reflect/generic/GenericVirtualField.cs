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
namespace com.db4o.reflect.generic
{
	/// <exclude></exclude>
	public class GenericVirtualField : com.db4o.reflect.generic.GenericField
	{
		public GenericVirtualField(string name) : base(name, null, false, false, false)
		{
		}

		public override object deepClone(object obj)
		{
			com.db4o.reflect.Reflector reflector = (com.db4o.reflect.Reflector)obj;
			return new com.db4o.reflect.generic.GenericVirtualField(getName());
		}

		public override object get(object onObject)
		{
			return null;
		}

		public override com.db4o.reflect.ReflectClass getType()
		{
			return null;
		}

		public override bool isPublic()
		{
			return false;
		}

		public override bool isStatic()
		{
			return true;
		}

		public override bool isTransient()
		{
			return true;
		}

		public override void set(object onObject, object value)
		{
		}
	}
}

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
namespace com.db4o.reflect.net
{

	public class NetField : com.db4o.reflect.ReflectField
	{
		private readonly com.db4o.reflect.Reflector reflector;

		private readonly j4o.lang.reflect.Field field;

		public NetField(com.db4o.reflect.Reflector reflector, j4o.lang.reflect.Field field
			)
		{
			this.reflector = reflector;
			this.field = field;
		}

		public virtual string getName()
		{
			return field.getName();
		}

		public virtual com.db4o.reflect.ReflectClass getType()
		{
			return reflector.forClass(field.getType());
		}

		public virtual bool isPublic()
		{
			return j4o.lang.reflect.Modifier.isPublic(field.getModifiers());
		}

		public virtual bool isStatic()
		{
			return j4o.lang.reflect.Modifier.isStatic(field.getModifiers());
		}

		public virtual bool isTransient()
		{
			return j4o.lang.reflect.Modifier.isTransient(field.getModifiers());
		}

		public virtual void setAccessible()
		{
			com.db4o.Platform.setAccessible(field);
		}

		public virtual object get(object onObject)
		{
			try
			{
				return field.get(onObject);
			}
			catch (System.Exception e)
			{
				return null;
			}
		}

		public virtual void set(object onObject, object attribute)
		{
			try
			{
				field.set(onObject, attribute);
			}
			catch (System.Exception e)
			{
			}
		}
	}
}

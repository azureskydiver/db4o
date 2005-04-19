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
	public class QField : com.db4o.Visitor4
	{
		[com.db4o.Transient]
		internal com.db4o.Transaction i_trans;

		internal string i_name;

		[com.db4o.Transient]
		internal com.db4o.YapField i_yapField;

		internal int i_yapClassID;

		internal int i_index;

		public QField()
		{
		}

		internal QField(com.db4o.Transaction a_trans, string name, com.db4o.YapField a_yapField
			, int a_yapClassID, int a_index)
		{
			i_trans = a_trans;
			i_name = name;
			i_yapField = a_yapField;
			i_yapClassID = a_yapClassID;
			i_index = a_index;
			if (i_yapField != null)
			{
				if (!i_yapField.alive())
				{
					i_yapField = null;
				}
			}
		}

		internal virtual bool canHold(object a_object)
		{
			com.db4o.reflect.ReflectClass claxx = null;
			com.db4o.reflect.Reflector reflector = i_trans.reflector();
			if (a_object != null)
			{
				if (a_object is com.db4o.reflect.ReflectClass)
				{
					claxx = (com.db4o.reflect.ReflectClass)a_object;
				}
				else
				{
					claxx = reflector.forObject(a_object);
				}
			}
			return i_yapField == null || i_yapField.canHold(claxx);
		}

		internal virtual com.db4o.YapClass getYapClass()
		{
			if (i_yapField != null)
			{
				return i_yapField.getFieldYapClass(i_trans.i_stream);
			}
			return null;
		}

		internal virtual com.db4o.YapField getYapField(com.db4o.YapClass yc)
		{
			if (i_yapField != null)
			{
				return i_yapField;
			}
			com.db4o.YapField yf = yc.getYapField(i_name);
			if (yf != null)
			{
				yf.alive();
			}
			return yf;
		}

		internal virtual bool isArray()
		{
			return i_yapField != null && i_yapField.getHandler() is com.db4o.YapArray;
		}

		internal virtual bool isClass()
		{
			return i_yapField == null || i_yapField.getHandler().getType() == com.db4o.YapConst
				.TYPE_CLASS;
		}

		internal virtual bool isSimple()
		{
			return i_yapField != null && i_yapField.getHandler().getType() == com.db4o.YapConst
				.TYPE_SIMPLE;
		}

		internal virtual com.db4o.YapComparable prepareComparison(object obj)
		{
			if (i_yapField != null)
			{
				return i_yapField.prepareComparison(obj);
			}
			if (obj == null)
			{
				return com.db4o.Null.INSTANCE;
			}
			com.db4o.YapClass yc = i_trans.i_stream.getYapClass(i_trans.reflector().forObject
				(obj), true);
			com.db4o.YapField yf = yc.getYapField(i_name);
			if (yf != null)
			{
				return yf.prepareComparison(obj);
			}
			return null;
		}

		internal virtual void unmarshall(com.db4o.Transaction a_trans)
		{
			if (i_yapClassID != 0)
			{
				com.db4o.YapClass yc = a_trans.i_stream.getYapClass(i_yapClassID);
				i_yapField = yc.i_fields[i_index];
			}
		}

		public virtual void visit(object obj)
		{
			((com.db4o.QCandidate)obj).useField(this);
		}
	}
}

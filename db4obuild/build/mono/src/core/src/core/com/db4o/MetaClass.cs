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
	/// <summary>
	/// Class metadata to be stored to the database file
	/// Don't obfuscate.
	/// </summary>
	/// <remarks>
	/// Class metadata to be stored to the database file
	/// Don't obfuscate.
	/// </remarks>
	/// <exclude></exclude>
	public class MetaClass : com.db4o.Internal
	{
		public string name;

		public com.db4o.MetaField[] fields;

		public MetaClass()
		{
		}

		public MetaClass(string name)
		{
			this.name = name;
		}

		internal virtual com.db4o.MetaField ensureField(com.db4o.Transaction trans, string
			 a_name)
		{
			if (fields != null)
			{
				for (int i = 0; i < fields.Length; i++)
				{
					if (fields[i].name.Equals(a_name))
					{
						return fields[i];
					}
				}
				com.db4o.MetaField[] temp = new com.db4o.MetaField[fields.Length + 1];
				j4o.lang.JavaSystem.arraycopy(fields, 0, temp, 0, fields.Length);
				fields = temp;
			}
			else
			{
				fields = new com.db4o.MetaField[1];
			}
			com.db4o.MetaField newMetaField = new com.db4o.MetaField(a_name);
			fields[fields.Length - 1] = newMetaField;
			trans.i_stream.setInternal(trans, newMetaField, com.db4o.YapConst.UNSPECIFIED, false
				);
			trans.i_stream.setInternal(trans, this, com.db4o.YapConst.UNSPECIFIED, false);
			return newMetaField;
		}
	}
}

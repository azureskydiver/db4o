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
	public class Hashtable4 : j4o.lang.Cloneable, com.db4o.DeepClone
	{
		private const float FILL = 0.5F;

		private int i_tableSize;

		private int i_mask;

		private int i_maximumSize;

		private int i_size;

		private com.db4o.HashtableIntEntry[] i_table;

		public Hashtable4(int a_size)
		{
			a_size = newSize(a_size);
			i_tableSize = 1;
			while (i_tableSize < a_size)
			{
				i_tableSize = i_tableSize << 1;
			}
			i_mask = i_tableSize - 1;
			i_maximumSize = (int)(i_tableSize * FILL);
			i_table = new com.db4o.HashtableIntEntry[i_tableSize];
		}

		private int newSize(int a_size)
		{
			return (int)(a_size / FILL);
		}

		public virtual object deepClone(object obj)
		{
			com.db4o.Hashtable4 ret = (com.db4o.Hashtable4)j4o.lang.JavaSystem.clone(this);
			ret.i_table = new com.db4o.HashtableIntEntry[i_tableSize];
			for (int i = 0; i < i_tableSize; i++)
			{
				if (i_table[i] != null)
				{
					ret.i_table[i] = (com.db4o.HashtableIntEntry)i_table[i].deepClone(obj);
				}
			}
			return ret;
		}

		public virtual void forEachKey(com.db4o.Visitor4 visitor)
		{
			for (int i = 0; i < i_table.Length; i++)
			{
				com.db4o.HashtableIntEntry hie = i_table[i];
				while (hie != null)
				{
					if (hie is com.db4o.HashtableObjectEntry)
					{
						visitor.visit(((com.db4o.HashtableObjectEntry)hie).i_objectKey);
					}
					else
					{
						visitor.visit(System.Convert.ToInt32(hie.i_key));
					}
					hie = hie.i_next;
				}
			}
		}

		public virtual void forEachValue(com.db4o.Visitor4 visitor)
		{
			for (int i = 0; i < i_table.Length; i++)
			{
				com.db4o.HashtableIntEntry hie = i_table[i];
				while (hie != null)
				{
					visitor.visit(hie.i_object);
					hie = hie.i_next;
				}
			}
		}

		public virtual object get(int a_key)
		{
			com.db4o.HashtableIntEntry ihe = i_table[a_key & i_mask];
			while (ihe != null)
			{
				if (ihe.i_key == a_key)
				{
					return ihe.i_object;
				}
				ihe = ihe.i_next;
			}
			return null;
		}

		public virtual object get(object a_objectKey)
		{
			if (a_objectKey == null)
			{
				return null;
			}
			int a_key = a_objectKey.GetHashCode();
			com.db4o.HashtableObjectEntry ihe = (com.db4o.HashtableObjectEntry)i_table[a_key 
				& i_mask];
			while (ihe != null)
			{
				if (ihe.i_key == a_key && ihe.i_objectKey.Equals(a_objectKey))
				{
					return ihe.i_object;
				}
				ihe = (com.db4o.HashtableObjectEntry)ihe.i_next;
			}
			return null;
		}

		public virtual object get(byte[] a_bytes)
		{
			int a_key = hash(a_bytes);
			com.db4o.HashtableObjectEntry ihe = (com.db4o.HashtableObjectEntry)i_table[a_key 
				& i_mask];
			while (ihe != null)
			{
				if (ihe.i_key == a_key)
				{
					byte[] bytes = (byte[])ihe.i_objectKey;
					if (bytes.Length == a_bytes.Length)
					{
						bool isEqual = true;
						for (int i = 0; i < bytes.Length; i++)
						{
							if (bytes[i] != a_bytes[i])
							{
								isEqual = false;
							}
						}
						if (isEqual)
						{
							return ihe.i_object;
						}
					}
				}
				ihe = (com.db4o.HashtableObjectEntry)ihe.i_next;
			}
			return null;
		}

		private int hash(byte[] bytes)
		{
			int ret = 0;
			for (int i = 0; i < bytes.Length; i++)
			{
				ret = ret * 31 + bytes[i];
			}
			return ret;
		}

		private void increaseSize()
		{
			i_tableSize = i_tableSize << 1;
			i_maximumSize = i_maximumSize << 1;
			i_mask = i_tableSize - 1;
			com.db4o.HashtableIntEntry[] temp = i_table;
			i_table = new com.db4o.HashtableIntEntry[i_tableSize];
			for (int i = 0; i < temp.Length; i++)
			{
				reposition(temp[i]);
			}
		}

		public virtual void put(int a_key, object a_object)
		{
			put1(new com.db4o.HashtableIntEntry(a_key, a_object));
		}

		public virtual void put(object a_key, object a_object)
		{
			put1(new com.db4o.HashtableObjectEntry(a_key, a_object));
		}

		public virtual void put(byte[] a_bytes, object a_object)
		{
			int a_key = hash(a_bytes);
			put1(new com.db4o.HashtableObjectEntry(a_key, a_bytes, a_object));
		}

		private void put1(com.db4o.HashtableIntEntry a_entry)
		{
			i_size++;
			if (i_size > i_maximumSize)
			{
				increaseSize();
			}
			int index = a_entry.i_key & i_mask;
			a_entry.i_next = i_table[index];
			i_table[index] = a_entry;
		}

		public virtual void remove(int a_key)
		{
			com.db4o.HashtableIntEntry ihe = i_table[a_key & i_mask];
			com.db4o.HashtableIntEntry last = null;
			while (ihe != null)
			{
				if (ihe.i_key == a_key)
				{
					if (last != null)
					{
						last.i_next = ihe.i_next;
					}
					else
					{
						i_table[a_key & i_mask] = ihe.i_next;
					}
					i_size--;
					return;
				}
				last = ihe;
				ihe = ihe.i_next;
			}
		}

		public virtual void remove(object a_objectKey)
		{
			int a_key = a_objectKey.GetHashCode();
			com.db4o.HashtableObjectEntry ihe = (com.db4o.HashtableObjectEntry)i_table[a_key 
				& i_mask];
			com.db4o.HashtableIntEntry last = null;
			while (ihe != null)
			{
				if (ihe.i_key == a_key && ihe.i_objectKey.Equals(a_objectKey))
				{
					if (last != null)
					{
						last.i_next = ihe.i_next;
					}
					else
					{
						i_table[a_key & i_mask] = ihe.i_next;
					}
					i_size--;
					return;
				}
				last = ihe;
				ihe = (com.db4o.HashtableObjectEntry)ihe.i_next;
			}
		}

		public virtual object remove(byte[] a_bytes)
		{
			int a_key = hash(a_bytes);
			com.db4o.HashtableObjectEntry ihe = (com.db4o.HashtableObjectEntry)i_table[a_key 
				& i_mask];
			com.db4o.HashtableObjectEntry last = null;
			while (ihe != null)
			{
				if (ihe.i_key == a_key)
				{
					byte[] bytes = (byte[])ihe.i_objectKey;
					if (bytes.Length == a_bytes.Length)
					{
						bool isEqual = true;
						for (int i = 0; i < bytes.Length; i++)
						{
							if (bytes[i] != a_bytes[i])
							{
								isEqual = false;
							}
						}
						if (isEqual)
						{
							if (last != null)
							{
								last.i_next = ihe.i_next;
							}
							else
							{
								i_table[a_key & i_mask] = ihe.i_next;
							}
							i_size--;
							return ihe.i_object;
						}
					}
				}
				last = ihe;
				ihe = (com.db4o.HashtableObjectEntry)ihe.i_next;
			}
			return null;
		}

		private void reposition(com.db4o.HashtableIntEntry a_entry)
		{
			if (a_entry != null)
			{
				reposition(a_entry.i_next);
				a_entry.i_next = i_table[a_entry.i_key & i_mask];
				i_table[a_entry.i_key & i_mask] = a_entry;
			}
		}
	}
}

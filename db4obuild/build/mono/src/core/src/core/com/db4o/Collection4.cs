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
	/// <summary>Fast linked list for all usecases.</summary>
	/// <remarks>Fast linked list for all usecases.</remarks>
	/// <exclude></exclude>
	public class Collection4 : com.db4o.DeepClone
	{
		/// <summary>first element of the linked list</summary>
		internal com.db4o.List4 i_first;

		/// <summary>number of elements collected</summary>
		private int i_size;

		/// <summary>performance trick only: no object creation</summary>
		private static readonly com.db4o.EmptyIterator emptyIterator = new com.db4o.EmptyIterator
			();

		public void add(object a_object)
		{
			i_first = new com.db4o.List4(i_first, a_object);
			i_size++;
		}

		internal void addAll(object[] a_objects)
		{
			if (a_objects != null)
			{
				for (int i = 0; i < a_objects.Length; i++)
				{
					if (a_objects[i] != null)
					{
						add(a_objects[i]);
					}
				}
			}
		}

		public void addAll(com.db4o.Collection4 other)
		{
			if (other != null)
			{
				com.db4o.Iterator4 i = other.iterator();
				while (i.hasNext())
				{
					add(i.next());
				}
			}
		}

		internal void clear()
		{
			i_first = null;
			i_size = 0;
		}

		public bool contains(object a_obj)
		{
			return get(a_obj) != null;
		}

		/// <summary>tests if the object is in the Collection.</summary>
		/// <remarks>
		/// tests if the object is in the Collection.
		/// == comparison.
		/// </remarks>
		public bool containsByIdentity(object a_obj)
		{
			com.db4o.List4 current = i_first;
			while (current != null)
			{
				if (current.i_object != null && current.i_object == a_obj)
				{
					return true;
				}
				current = current.i_next;
			}
			return false;
		}

		/// <summary>
		/// returns the first object found in the Collections
		/// that equals() the passed object
		/// </summary>
		internal object get(object a_obj)
		{
			object current;
			com.db4o.Iterator4 i = iterator();
			while (i.hasNext())
			{
				current = i.next();
				if (current.Equals(a_obj))
				{
					return current;
				}
			}
			return null;
		}

		public virtual object deepClone(object param)
		{
			com.db4o.Collection4 col = new com.db4o.Collection4();
			object element = null;
			com.db4o.Iterator4 i = this.iterator();
			while (i.hasNext())
			{
				element = i.next();
				if (element is com.db4o.DeepClone)
				{
					col.add(((com.db4o.DeepClone)element).deepClone(param));
				}
				else
				{
					col.add(element);
				}
			}
			return col;
		}

		/// <summary>makes sure the passed object is in the Collection.</summary>
		/// <remarks>
		/// makes sure the passed object is in the Collection.
		/// equals() comparison.
		/// </remarks>
		internal object ensure(object a_obj)
		{
			object obj = get(a_obj);
			if (obj != null)
			{
				return obj;
			}
			add(a_obj);
			return a_obj;
		}

		public com.db4o.Iterator4 iterator()
		{
			if (i_first == null)
			{
				return emptyIterator;
			}
			return new com.db4o.Iterator4(i_first);
		}

		/// <summary>
		/// removes an object from the Collection
		/// equals() comparison
		/// returns the removed object or null, if none found
		/// </summary>
		public virtual object remove(object a_object)
		{
			com.db4o.List4 previous = null;
			com.db4o.List4 current = i_first;
			while (current != null)
			{
				if (current.i_object.Equals(a_object))
				{
					i_size--;
					if (previous == null)
					{
						i_first = current.i_next;
					}
					else
					{
						previous.i_next = current.i_next;
					}
					return current.i_object;
				}
				previous = current;
				current = current.i_next;
			}
			return null;
		}

		public int size()
		{
			return i_size;
		}

		/// <summary>This is a non reflection implementation for more speed.</summary>
		/// <remarks>
		/// This is a non reflection implementation for more speed.
		/// In contrast to the JDK behaviour, the passed array has
		/// to be initialized to the right length.
		/// </remarks>
		internal void toArray(object[] a_array)
		{
			int j = i_size;
			com.db4o.Iterator4 i = iterator();
			while (i.hasNext())
			{
				a_array[--j] = i.next();
			}
		}
	}
}

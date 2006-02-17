namespace com.db4o.foundation
{
	/// <summary>Fast linked list for all usecases.</summary>
	/// <remarks>Fast linked list for all usecases.</remarks>
	/// <exclude></exclude>
	public class Collection4 : com.db4o.foundation.DeepClone, com.db4o.types.Unversioned
	{
		/// <summary>first element of the linked list</summary>
		public com.db4o.foundation.List4 _first;

		/// <summary>number of elements collected</summary>
		public int _size;

		/// <summary>Adds an element to the beginning of this collection.</summary>
		/// <remarks>Adds an element to the beginning of this collection.</remarks>
		/// <param name="element"></param>
		public void add(object element)
		{
			_first = new com.db4o.foundation.List4(_first, element);
			_size++;
		}

		public void addAll(object[] elements)
		{
			if (elements != null)
			{
				for (int i = 0; i < elements.Length; i++)
				{
					if (elements[i] != null)
					{
						add(elements[i]);
					}
				}
			}
		}

		public void addAll(com.db4o.foundation.Collection4 other)
		{
			if (other != null)
			{
				addAll(other.iterator());
			}
		}

		public void addAll(com.db4o.foundation.Iterator4 iterator)
		{
			while (iterator.hasNext())
			{
				add(iterator.next());
			}
		}

		public void clear()
		{
			_first = null;
			_size = 0;
		}

		public bool contains(object element)
		{
			return get(element) != null;
		}

		/// <summary>tests if the object is in the Collection.</summary>
		/// <remarks>
		/// tests if the object is in the Collection.
		/// == comparison.
		/// </remarks>
		public bool containsByIdentity(object element)
		{
			com.db4o.foundation.List4 current = _first;
			while (current != null)
			{
				if (current._element != null && current._element == element)
				{
					return true;
				}
				current = current._next;
			}
			return false;
		}

		/// <summary>
		/// returns the first object found in the Collections
		/// that equals() the passed object
		/// </summary>
		public object get(object element)
		{
			com.db4o.foundation.Iterator4 i = iterator();
			while (i.hasNext())
			{
				object current = i.next();
				if (current.Equals(element))
				{
					return current;
				}
			}
			return null;
		}

		public virtual object deepClone(object newParent)
		{
			com.db4o.foundation.Collection4 col = new com.db4o.foundation.Collection4();
			object element = null;
			com.db4o.foundation.Iterator4 i = this.iterator();
			while (i.hasNext())
			{
				element = i.next();
				if (element is com.db4o.foundation.DeepClone)
				{
					col.add(((com.db4o.foundation.DeepClone)element).deepClone(newParent));
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
		public object ensure(object a_obj)
		{
			object obj = get(a_obj);
			if (obj != null)
			{
				return obj;
			}
			add(a_obj);
			return a_obj;
		}

		/// <summary>
		/// Iterates through the collection in
		/// reversed insertion order which happens
		/// to be the fastest.
		/// </summary>
		/// <remarks>
		/// Iterates through the collection in
		/// reversed insertion order which happens
		/// to be the fastest.
		/// </remarks>
		/// <returns></returns>
		public com.db4o.foundation.Iterator4 iterator()
		{
			return _first == null ? com.db4o.foundation.Iterator4Impl.EMPTY : new com.db4o.foundation.Iterator4Impl
				(_first);
		}

		/// <summary>
		/// Iterates through the collection in the correct
		/// order (the insertion order).
		/// </summary>
		/// <remarks>
		/// Iterates through the collection in the correct
		/// order (the insertion order).
		/// </remarks>
		/// <returns></returns>
		public virtual com.db4o.foundation.Iterator4 strictIterator()
		{
			return new com.db4o.foundation.ArrayIterator4(toArray());
		}

		/// <summary>
		/// removes an object from the Collection
		/// equals() comparison
		/// returns the removed object or null, if none found
		/// </summary>
		public virtual object remove(object a_object)
		{
			com.db4o.foundation.List4 previous = null;
			com.db4o.foundation.List4 current = _first;
			while (current != null)
			{
				if (current._element.Equals(a_object))
				{
					_size--;
					if (previous == null)
					{
						_first = current._next;
					}
					else
					{
						previous._next = current._next;
					}
					return current._element;
				}
				previous = current;
				current = current._next;
			}
			return null;
		}

		public int size()
		{
			return _size;
		}

		/// <summary>This is a non reflection implementation for more speed.</summary>
		/// <remarks>
		/// This is a non reflection implementation for more speed.
		/// In contrast to the JDK behaviour, the passed array has
		/// to be initialized to the right length.
		/// </remarks>
		public void toArray(object[] a_array)
		{
			int j = _size;
			com.db4o.foundation.Iterator4 i = iterator();
			while (i.hasNext())
			{
				a_array[--j] = i.next();
			}
		}

		public object[] toArray()
		{
			object[] array = new object[_size];
			toArray(array);
			return array;
		}

		public override string ToString()
		{
			return base.ToString();
			if (_size == 0)
			{
				return "[]";
			}
			j4o.lang.StringBuffer sb = new j4o.lang.StringBuffer();
			sb.append("[");
			com.db4o.foundation.Iterator4 i = iterator();
			sb.append(i.next());
			while (i.hasNext())
			{
				sb.append(", ");
				sb.append(i.next());
			}
			sb.append("]");
			return sb.ToString();
		}
	}
}

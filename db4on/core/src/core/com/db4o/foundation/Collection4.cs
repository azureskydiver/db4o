namespace com.db4o.foundation
{
	/// <summary>Fast linked list for all usecases.</summary>
	/// <remarks>Fast linked list for all usecases.</remarks>
	/// <exclude></exclude>
	public class Collection4 : com.db4o.foundation.Iterable4, com.db4o.foundation.DeepClone
		, com.db4o.types.Unversioned
	{
		/// <summary>first element of the linked list</summary>
		public com.db4o.foundation.List4 _first;

		/// <summary>number of elements collected</summary>
		public int _size;

		public Collection4()
		{
		}

		public Collection4(com.db4o.foundation.Collection4 other)
		{
			AddAll(other);
		}

		public virtual object SingleElement()
		{
			if (Size() != 1)
			{
				throw new System.InvalidOperationException();
			}
			return _first._element;
		}

		/// <summary>Adds an element to the beginning of this collection.</summary>
		/// <remarks>Adds an element to the beginning of this collection.</remarks>
		/// <param name="element"></param>
		public void Add(object element)
		{
			_first = new com.db4o.foundation.List4(_first, element);
			_size++;
		}

		public void AddAll(object[] elements)
		{
			if (elements != null)
			{
				for (int i = 0; i < elements.Length; i++)
				{
					if (elements[i] != null)
					{
						Add(elements[i]);
					}
				}
			}
		}

		public void AddAll(com.db4o.foundation.Collection4 other)
		{
			if (other != null)
			{
				AddAll(other.Iterator());
			}
		}

		public void AddAll(com.db4o.foundation.Iterator4 iterator)
		{
			while (iterator.MoveNext())
			{
				Add(iterator.Current());
			}
		}

		public void Clear()
		{
			_first = null;
			_size = 0;
		}

		public bool Contains(object element)
		{
			return Get(element) != null;
		}

		public virtual bool ContainsAll(com.db4o.foundation.Iterator4 iter)
		{
			while (iter.MoveNext())
			{
				if (!Contains(iter.Current()))
				{
					return false;
				}
			}
			return true;
		}

		/// <summary>tests if the object is in the Collection.</summary>
		/// <remarks>
		/// tests if the object is in the Collection.
		/// == comparison.
		/// </remarks>
		public bool ContainsByIdentity(object element)
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
		public object Get(object element)
		{
			com.db4o.foundation.Iterator4 i = Iterator();
			while (i.MoveNext())
			{
				object current = i.Current();
				if (current.Equals(element))
				{
					return current;
				}
			}
			return null;
		}

		public virtual object DeepClone(object newParent)
		{
			com.db4o.foundation.Collection4 col = new com.db4o.foundation.Collection4();
			object element = null;
			com.db4o.foundation.Iterator4 i = this.Iterator();
			while (i.MoveNext())
			{
				element = i.Current();
				if (element is com.db4o.foundation.DeepClone)
				{
					col.Add(((com.db4o.foundation.DeepClone)element).DeepClone(newParent));
				}
				else
				{
					col.Add(element);
				}
			}
			return col;
		}

		/// <summary>makes sure the passed object is in the Collection.</summary>
		/// <remarks>
		/// makes sure the passed object is in the Collection.
		/// equals() comparison.
		/// </remarks>
		public object Ensure(object a_obj)
		{
			object obj = Get(a_obj);
			if (obj != null)
			{
				return obj;
			}
			Add(a_obj);
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
		public com.db4o.foundation.Iterator4 Iterator()
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
		public virtual com.db4o.foundation.Iterator4 StrictIterator()
		{
			return new com.db4o.foundation.ArrayIterator4(ToArray());
		}

		/// <summary>
		/// removes an object from the Collection
		/// equals() comparison
		/// returns the removed object or null, if none found
		/// </summary>
		public virtual object Remove(object a_object)
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

		public int Size()
		{
			return _size;
		}

		/// <summary>This is a non reflection implementation for more speed.</summary>
		/// <remarks>
		/// This is a non reflection implementation for more speed.
		/// In contrast to the JDK behaviour, the passed array has
		/// to be initialized to the right length.
		/// </remarks>
		public object[] ToArray(object[] a_array)
		{
			int j = _size;
			com.db4o.foundation.Iterator4 i = Iterator();
			while (i.MoveNext())
			{
				a_array[--j] = i.Current();
			}
			return a_array;
		}

		public object[] ToArray()
		{
			object[] array = new object[_size];
			ToArray(array);
			return array;
		}

		public override string ToString()
		{
			if (_size == 0)
			{
				return "[]";
			}
			j4o.lang.StringBuffer sb = new j4o.lang.StringBuffer();
			sb.Append("[");
			com.db4o.foundation.Iterator4 i = StrictIterator();
			i.MoveNext();
			sb.Append(i.Current());
			while (i.MoveNext())
			{
				sb.Append(", ");
				sb.Append(i.Current());
			}
			sb.Append("]");
			return sb.ToString();
		}
	}
}

using System;
using com.db4o.ext;

namespace com.db4o.inside.query
{
	/// <summary>
	/// List based objectSet implementation
	/// </summary>
	/// <exclude />
	public class ObjectSetFacade : ExtObjectSet, System.Collections.IList
	{
		public readonly QueryResult _delegate;

        public ObjectSetFacade(QueryResult qr)
		{
            _delegate = qr;
		}

		#region ObjectSet Members
		public Object get(int index) {
            return _delegate.get(index);
        }
		
		public long[] getIDs() 
		{
			return _delegate.getIDs();
		}

		public ExtObjectSet ext() 
		{
			return this;
		}

		public bool hasNext() 
		{
			return _delegate.hasNext();
		}

		public Object next() 
		{
			return _delegate.next();
		}

		public void reset() 
		{
			_delegate.reset();
		}

		public int size() 
		{
			return _delegate.size();
		}
    
		private Object streamLock()
		{
			return _delegate.streamLock();
		}
    
		private ObjectContainer objectContainer()
		{
			return _delegate.objectContainer();
		}
		#endregion

		#region IList Members

		public bool IsReadOnly
		{
			get
			{
				return true;
			}
		}

		public object this[int index]
		{
			get
			{
				return _delegate.get(index);
			}
			set
			{
				throw new NotSupportedException();
			}
		}

		public void RemoveAt(int index)
		{
			throw new NotSupportedException();
		}

		public void Insert(int index, object value)
		{
			throw new NotSupportedException();
		}

		public void Remove(object value)
		{
			throw new NotSupportedException();
		}

		public bool Contains(object value)
		{
			return IndexOf(value) >= 0;
		}

		public void Clear()
		{
			throw new NotSupportedException();
		}

		public int IndexOf(object value)
		{
			lock (streamLock())
			{
                int id = (int)objectContainer().ext().getID(value);
				if(id <= 0)
				{
					return -1;
				}
				return _delegate.indexOf(id);
			}
		}

		public int Add(object value)
		{
			throw new NotSupportedException();
		}

		public bool IsFixedSize
		{
			get
			{
				return true;
			}
		}

		#endregion

		#region ICollection Members
		public bool IsSynchronized
		{
			get
			{
				return true;
			}
		}

		public int Count
		{
			get
			{
				return size();
			}
		}

        public void CopyTo(Array array, int index)
        {
            lock (streamLock())
            {
                int i = 0;
                int s = _delegate.size();
                while (i < s)
                {
                    array.SetValue(_delegate.get(i), index + i);
                    i++;
                }
            }
        }

        public object SyncRoot
		{
			get
			{
				return streamLock();
			}
		}

		#endregion

		#region IEnumerable Members

		class ObjectSetImplEnumerator : System.Collections.IEnumerator
		{
			QueryResult _result;
			int _next = 0;
			
			public ObjectSetImplEnumerator(QueryResult result)
			{
				_result = result;
			}

			public void Reset()
			{
				_next = 0;
			}

			public object Current
			{
				get
				{
					return _result.get(_next-1);
				}
			}

			public bool MoveNext()
			{
				if (_next < _result.size())
				{
					++_next;
					return true;
				}
				return false;
			}
		}

		public System.Collections.IEnumerator GetEnumerator()
		{
			return new ObjectSetImplEnumerator(_delegate);
		}
		#endregion
	}
}


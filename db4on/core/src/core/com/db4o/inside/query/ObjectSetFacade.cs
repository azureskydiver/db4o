﻿/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
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
		
		public Object Get(int index) {
            return _delegate.Get(index);
        }

		public void Sort(com.db4o.query.QueryComparator cmp)
		{
			throw new NotImplementedException();
		}

		public long[] GetIDs() 
		{
			return _delegate.GetIDs();
		}

		public ExtObjectSet Ext() 
		{
			return this;
		}

		public bool HasNext() 
		{
			return _delegate.HasNext();
		}

		public Object Next() 
		{
			return _delegate.Next();
		}

		public void Reset() 
		{
			_delegate.Reset();
		}

		public int Size() 
		{
			return _delegate.Size();
		}
    
		private Object StreamLock()
		{
			return _delegate.StreamLock();
		}
    
		private ObjectContainer ObjectContainer()
		{
			return _delegate.ObjectContainer();
		}

		public QueryResult Delegate_()
		{
			return _delegate;
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
				return _delegate.Get(ReverseIndex(index));
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
			lock (StreamLock())
			{
                int id = (int)ObjectContainer().Ext().GetID(value);
				if(id <= 0)
				{
					return -1;
				}
				return ReverseIndex(_delegate.IndexOf(id));
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
				return Size();
			}
		}

        public void CopyTo(Array array, int index)
        {
            lock (StreamLock())
            {
                int i = 0;
                int s = _delegate.Size();
                while (i < s)
                {
                    array.SetValue(_delegate.Get(ReverseIndex(i)), index + i);
                    i++;
                }
            }
        }

        public object SyncRoot
		{
			get
			{
				return StreamLock();
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
					return _result.Get(_next-1);
				}
			}

			public bool MoveNext()
			{
				if (_next < _result.Size())
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
		
		private int ReverseIndex(int idx) 
		{
        	return Size()-idx-1;
    	}
	}
}



using System;
using com.db4o.ext;

namespace com.db4o.inside.query
{
#if NET_2_0
    /// <summary>
    /// List based objectSet implementation
    /// </summary>
    /// <exclude />
    public class GenericObjectSetFacade<T> : System.Collections.Generic.IList<T>
    {
        public readonly QueryResult _delegate;

        public GenericObjectSetFacade(QueryResult qr)
        {
            _delegate = qr;
        }

        #region IList<T> Members
        public bool IsReadOnly
        {
            get
            {
                return true;
            }
        }

        public T this[int index]
        {
            get
            {
                return (T)_delegate.get(index);
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

        public void Insert(int index, T value)
        {
            throw new NotSupportedException();
        }

        public bool Remove(T value)
        {
            throw new NotSupportedException();
        }

        public bool Contains(T value)
        {
            return IndexOf(value) >= 0;
        }

        public void Clear()
        {
            throw new NotSupportedException();
        }

        public int IndexOf(T value)
        {
            lock (this.SyncRoot)
            {
                int id = (int)_delegate.objectContainer().ext().getID(value);
                if (id <= 0)
                {
                    return -1;
                }
                return _delegate.indexOf(id);
            }
        }

        public void Add(T value)
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

        #region ICollection<T> Members
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
                return _delegate.size();
            }
        }

        public void CopyTo(T[] array, int index)
        {
            lock (this.SyncRoot)
            {
                int i = 0;
                int s = _delegate.size();
                while (i < s)
                {
                    array[index + i] = (T)_delegate.get(i);
                    i++;
                }
            }
        }

        public object SyncRoot
        {
            get
            {
                return _delegate.streamLock();
            }
        }

        #endregion

        #region IEnumerable Members

        class ObjectSetImplEnumerator<T> : System.Collections.IEnumerator, System.Collections.Generic.IEnumerator<T>
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
                    return InnerCurrent;
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

            private object InnerCurrent
            {
                get
                {
                    return _result.get(_next - 1);
                }
            }

            #region IEnumerator<T> Members
            T System.Collections.Generic.IEnumerator<T>.Current
            {
                get
                {
                    return (T)InnerCurrent;
                }
            }
            #endregion

            #region IDisposable Members
            public void Dispose()
            {
            }
            #endregion
        }

        public System.Collections.IEnumerator GetEnumerator()
        {
            return new ObjectSetImplEnumerator<T>(_delegate);
        }
        #endregion

        #region IEnumerable<T> implementation
        System.Collections.Generic.IEnumerator<T> System.Collections.Generic.IEnumerable<T>.GetEnumerator()
        {
            return new ObjectSetImplEnumerator<T>(_delegate);
        }
        #endregion
    }
#endif
}


namespace Db4objects.Drs.Foundation
{
	public class ObjectSetCollection4Facade : ObjectSetAbstractFacade
	{
        internal Db4objects.Db4o.Foundation.Collection4 _delegate;

        private System.Collections.IEnumerator _currentIterator;

        enum Enumerator_Status
        {
            RESET,
            MOVING,
            EOF,
        };

        Enumerator_Status _status;

        public ObjectSetCollection4Facade(Db4objects.Db4o.Foundation.Collection4 delegate_
            )
        {
            this._delegate = delegate_;
        }


        public override object Next()
        {
            object obj;
            if (HasNext())
            {
                obj = CurrentIterator().Current;
                MoveNext();
                return obj;
            }
            else
            {
                return null;
            }
        }

        public override bool HasNext()
        {
            if (_status == Enumerator_Status.RESET)
            {
                MoveNext();
            }

            return _status != Enumerator_Status.EOF;
        }

        public override void Reset()
        {
            CurrentIterator().Reset();
            _status = Enumerator_Status.RESET;
        }

        public override int Size()
        {
            return this._delegate.Size();
        }

        public override bool Contains(object value)
        {
            return this._delegate.Contains(value);
        }

        private System.Collections.IEnumerator CurrentIterator()
        {
            if (_currentIterator == null)
            {
                _currentIterator = _delegate.GetEnumerator();
                _status = Enumerator_Status.RESET;
            }
            return _currentIterator;
        }

        private void MoveNext()
        {
            if (CurrentIterator().MoveNext())
            {
                _status = Enumerator_Status.MOVING;
            }
            else
            {
                _status = Enumerator_Status.EOF;
            }

        }
	}
}
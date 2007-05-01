using System;
using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Internal.IX;
using Db4objects.Db4o.Internal.Slots;

namespace Db4objects.Db4o.Internal.Freespace
{
	/// <exclude></exclude>
	public abstract class SlotHandler : IIndexable4
	{
		protected Slot _current;

		public virtual object ComparableObject(Transaction trans, object indexEntry)
		{
			throw new NotImplementedException();
		}

		public virtual void DefragIndexEntry(ReaderPair readers)
		{
			throw new NotImplementedException();
		}

		public virtual int LinkLength()
		{
			return Slot.MARSHALLED_LENGTH;
		}

		public virtual object ReadIndexEntry(Db4objects.Db4o.Internal.Buffer reader)
		{
			return new Slot(reader.ReadInt(), reader.ReadInt());
		}

		public virtual void WriteIndexEntry(Db4objects.Db4o.Internal.Buffer writer, object
			 obj)
		{
			Slot slot = (Slot)obj;
			writer.WriteInt(slot._address);
			writer.WriteInt(slot._length);
		}

		public virtual object Current()
		{
			return _current;
		}

		public virtual bool IsEqual(object obj)
		{
			throw new NotImplementedException();
		}

		public virtual bool IsGreater(object obj)
		{
			throw new NotImplementedException();
		}

		public virtual bool IsSmaller(object obj)
		{
			throw new NotImplementedException();
		}

		public virtual IComparable4 PrepareComparison(object obj)
		{
			_current = (Slot)obj;
			return this;
		}

		public abstract int CompareTo(object arg1);
	}
}

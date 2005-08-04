namespace com.db4o
{
	internal class PendingClassInits
	{
		private readonly com.db4o.YapClassCollection _classColl;

		private com.db4o.foundation.Collection4 _pending = new com.db4o.foundation.Collection4
			();

		private com.db4o.foundation.List4 _members;

		private com.db4o.foundation.List4 _statics;

		private com.db4o.foundation.List4 _writes;

		private bool _running = false;

		internal PendingClassInits(com.db4o.YapClassCollection classColl)
		{
			_classColl = classColl;
		}

		internal virtual void process(com.db4o.YapClass newYapClass)
		{
			if (_pending.contains(newYapClass))
			{
				return;
			}
			com.db4o.YapClass ancestor = newYapClass.getAncestor();
			if (ancestor != null)
			{
				process(ancestor);
			}
			_pending.add(newYapClass);
			_members = new com.db4o.foundation.List4(_members, newYapClass);
			if (_running)
			{
				return;
			}
			_running = true;
			checkWrites();
			_pending = new com.db4o.foundation.Collection4();
			_running = false;
		}

		private void checkMembers()
		{
			while (_members != null)
			{
				com.db4o.foundation.Iterator4 members = new com.db4o.foundation.Iterator4(_members
					);
				_members = null;
				while (members.hasNext())
				{
					com.db4o.YapClass yc = (com.db4o.YapClass)members.next();
					yc.addMembers(_classColl.i_stream);
					_statics = new com.db4o.foundation.List4(_statics, yc);
				}
			}
		}

		private void checkStatics()
		{
			checkMembers();
			while (_statics != null)
			{
				com.db4o.foundation.Iterator4 statics = new com.db4o.foundation.Iterator4(_statics
					);
				_statics = null;
				while (statics.hasNext())
				{
					com.db4o.YapClass yc = (com.db4o.YapClass)statics.next();
					yc.storeStaticFieldValues(_classColl.i_systemTrans, true);
					_writes = new com.db4o.foundation.List4(_writes, yc);
					checkMembers();
				}
			}
		}

		private void checkWrites()
		{
			checkStatics();
			while (_writes != null)
			{
				com.db4o.foundation.Iterator4 writes = new com.db4o.foundation.Iterator4(_writes);
				_writes = null;
				while (writes.hasNext())
				{
					com.db4o.YapClass yc = (com.db4o.YapClass)writes.next();
					yc.setStateDirty();
					yc.write(_classColl.i_stream, _classColl.i_systemTrans);
					checkStatics();
				}
			}
		}
	}
}

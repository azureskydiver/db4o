namespace com.db4o
{
	internal class PendingClassInits
	{
		private readonly com.db4o.YapClassCollection _classColl;

		private com.db4o.foundation.Collection4 _pending = new com.db4o.foundation.Collection4
			();

		private com.db4o.foundation.Queue4 _members = new com.db4o.foundation.Queue4();

		private com.db4o.foundation.Queue4 _statics = new com.db4o.foundation.Queue4();

		private com.db4o.foundation.Queue4 _writes = new com.db4o.foundation.Queue4();

		private com.db4o.foundation.Queue4 _inits = new com.db4o.foundation.Queue4();

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
			_members.add(newYapClass);
			if (_running)
			{
				return;
			}
			_running = true;
			checkInits();
			_pending = new com.db4o.foundation.Collection4();
			_running = false;
		}

		private void checkMembers()
		{
			while (_members.hasNext())
			{
				com.db4o.YapClass yc = (com.db4o.YapClass)_members.next();
				yc.addMembers(_classColl.i_stream);
				_statics.add(yc);
			}
		}

		private void checkStatics()
		{
			checkMembers();
			while (_statics.hasNext())
			{
				com.db4o.YapClass yc = (com.db4o.YapClass)_statics.next();
				yc.storeStaticFieldValues(_classColl.i_systemTrans, true);
				_writes.add(yc);
				checkMembers();
			}
		}

		private void checkWrites()
		{
			checkStatics();
			while (_writes.hasNext())
			{
				com.db4o.YapClass yc = (com.db4o.YapClass)_writes.next();
				yc.setStateDirty();
				yc.write(_classColl.i_systemTrans);
				_inits.add(yc);
				checkStatics();
			}
		}

		private void checkInits()
		{
			checkWrites();
			while (_inits.hasNext())
			{
				com.db4o.YapClass yc = (com.db4o.YapClass)_inits.next();
				yc.initConfigOnUp(_classColl.i_systemTrans);
				checkWrites();
			}
		}
	}
}

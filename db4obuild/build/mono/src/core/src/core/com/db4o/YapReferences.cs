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
	internal class YapReferences : j4o.lang.Runnable
	{
		internal readonly object _queue;

		private readonly com.db4o.YapStream _stream;

		private com.db4o.foundation.SimpleTimer _timer;

		public readonly bool _weak;

		internal YapReferences(com.db4o.YapStream a_stream)
		{
			_stream = a_stream;
			_weak = (!(a_stream is com.db4o.YapObjectCarrier) && com.db4o.Platform.hasWeakReferences
				() && a_stream.i_config.i_weakReferences);
			_queue = _weak ? com.db4o.Platform.createReferenceQueue() : null;
		}

		internal virtual object createYapRef(com.db4o.YapObject a_yo, object obj)
		{
			if (!_weak)
			{
				return obj;
			}
			return com.db4o.Platform.createYapRef(_queue, a_yo, obj);
		}

		internal virtual void pollReferenceQueue()
		{
			if (_weak)
			{
				com.db4o.Platform.pollReferenceQueue(_stream, _queue);
			}
		}

		public virtual void run()
		{
			pollReferenceQueue();
		}

		internal virtual void startTimer()
		{
			if (!_weak)
			{
				return;
			}
			if (_stream.i_config.i_weakReferenceCollectionInterval <= 0)
			{
				return;
			}
			if (_timer != null)
			{
				return;
			}
			_timer = new com.db4o.foundation.SimpleTimer(this, _stream.i_config.i_weakReferenceCollectionInterval
				, "db4o WeakReference collector");
		}

		internal virtual void stopTimer()
		{
			if (_timer == null)
			{
				return;
			}
			_timer.stop();
			_timer = null;
		}
	}
}

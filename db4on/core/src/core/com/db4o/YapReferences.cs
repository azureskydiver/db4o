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
			_weak = (!(a_stream is com.db4o.YapObjectCarrier) && com.db4o.Platform4.hasWeakReferences
				() && a_stream.i_config.i_weakReferences);
			_queue = _weak ? com.db4o.Platform4.createReferenceQueue() : null;
		}

		internal virtual object createYapRef(com.db4o.YapObject a_yo, object obj)
		{
			if (!_weak)
			{
				return obj;
			}
			return com.db4o.Platform4.createYapRef(_queue, a_yo, obj);
		}

		internal virtual void pollReferenceQueue()
		{
			if (_weak)
			{
				com.db4o.Platform4.pollReferenceQueue(_stream, _queue);
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

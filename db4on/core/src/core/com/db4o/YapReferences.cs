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
			_weak = (!(a_stream is com.db4o.YapObjectCarrier) && com.db4o.Platform4.HasWeakReferences
				() && a_stream.ConfigImpl().WeakReferences());
			_queue = _weak ? com.db4o.Platform4.CreateReferenceQueue() : null;
		}

		internal virtual object CreateYapRef(com.db4o.YapObject a_yo, object obj)
		{
			if (!_weak)
			{
				return obj;
			}
			return com.db4o.Platform4.CreateYapRef(_queue, a_yo, obj);
		}

		internal virtual void PollReferenceQueue()
		{
			if (_weak)
			{
				com.db4o.Platform4.PollReferenceQueue(_stream, _queue);
			}
		}

		public virtual void Run()
		{
			PollReferenceQueue();
		}

		internal virtual void StartTimer()
		{
			if (!_weak)
			{
				return;
			}
			if (!_stream.ConfigImpl().WeakReferences())
			{
				return;
			}
			if (_stream.ConfigImpl().WeakReferenceCollectionInterval() <= 0)
			{
				return;
			}
			if (_timer != null)
			{
				return;
			}
			_timer = new com.db4o.foundation.SimpleTimer(this, _stream.ConfigImpl().WeakReferenceCollectionInterval
				(), "db4o WeakReference collector");
		}

		internal virtual void StopTimer()
		{
			if (_timer == null)
			{
				return;
			}
			_timer.Stop();
			_timer = null;
		}
	}
}

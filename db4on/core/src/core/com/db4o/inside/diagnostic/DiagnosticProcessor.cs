namespace com.db4o.inside.diagnostic
{
	/// <exclude></exclude>
	public class DiagnosticProcessor : com.db4o.diagnostic.DiagnosticConfiguration, com.db4o.diagnostic.DiagnosticListener
		, com.db4o.foundation.DeepClone
	{
		private com.db4o.foundation.Collection4 _listeners;

		public virtual void AddListener(com.db4o.diagnostic.DiagnosticListener listener)
		{
			if (_listeners == null)
			{
				_listeners = new com.db4o.foundation.Collection4();
			}
			_listeners.Add(listener);
		}

		public virtual object DeepClone(object context)
		{
			com.db4o.inside.diagnostic.DiagnosticProcessor ret = new com.db4o.inside.diagnostic.DiagnosticProcessor
				();
			if (_listeners != null)
			{
				com.db4o.foundation.Iterator4 i = _listeners.Iterator();
				while (i.HasNext())
				{
					ret.AddListener((com.db4o.diagnostic.DiagnosticListener)i.Next());
				}
			}
			return ret;
		}

		public virtual bool Enabled()
		{
			return _listeners != null;
		}

		public virtual void OnDiagnostic(com.db4o.diagnostic.Diagnostic d)
		{
			if (_listeners == null)
			{
				return;
			}
			com.db4o.foundation.Iterator4 i = _listeners.Iterator();
			while (i.HasNext())
			{
				((com.db4o.diagnostic.DiagnosticListener)i.Next()).OnDiagnostic(d);
			}
		}

		public virtual void RemoveAllListeners()
		{
			_listeners = null;
		}
	}
}

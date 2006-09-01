namespace com.db4o.inside.diagnostic
{
	/// <exclude>FIXME: remove me from the core and make me a facade over Events</exclude>
	public class DiagnosticProcessor : com.db4o.diagnostic.DiagnosticConfiguration, com.db4o.foundation.DeepClone
	{
		private com.db4o.foundation.Collection4 _listeners;

		public DiagnosticProcessor()
		{
		}

		private DiagnosticProcessor(com.db4o.foundation.Collection4 listeners)
		{
			_listeners = listeners;
		}

		public virtual void AddListener(com.db4o.diagnostic.DiagnosticListener listener)
		{
			if (_listeners == null)
			{
				_listeners = new com.db4o.foundation.Collection4();
			}
			_listeners.Add(listener);
		}

		public virtual void CheckClassHasFields(com.db4o.YapClass yc)
		{
			com.db4o.YapField[] fields = yc.i_fields;
			if (fields != null && fields.Length == 0)
			{
				string name = yc.GetName();
				string[] ignoredPackages = new string[] { "java.util." };
				for (int i = 0; i < ignoredPackages.Length; i++)
				{
					if (name.IndexOf(ignoredPackages[i]) == 0)
					{
						return;
					}
				}
				if (IsDb4oClass(yc))
				{
					return;
				}
				OnDiagnostic(new com.db4o.diagnostic.ClassHasNoFields(name));
			}
		}

		public virtual void CheckUpdateDepth(int depth)
		{
			if (depth > 1)
			{
				OnDiagnostic(new com.db4o.diagnostic.UpdateDepthGreaterOne(depth));
			}
		}

		public virtual object DeepClone(object context)
		{
			return new com.db4o.inside.diagnostic.DiagnosticProcessor(CloneListeners());
		}

		private com.db4o.foundation.Collection4 CloneListeners()
		{
			return _listeners != null ? new com.db4o.foundation.Collection4(_listeners) : null;
		}

		public virtual bool Enabled()
		{
			return _listeners != null;
		}

		private bool IsDb4oClass(com.db4o.YapClass yc)
		{
			string name = yc.GetName();
			if (name.IndexOf("com.db4o.test") == 0)
			{
				return false;
			}
			return name.IndexOf("com.db4o.") == 0;
		}

		public virtual void LoadedFromClassIndex(com.db4o.YapClass yc)
		{
			if (IsDb4oClass(yc))
			{
				return;
			}
			OnDiagnostic(new com.db4o.diagnostic.LoadedFromClassIndex(yc.GetName()));
		}

		public virtual void DescendIntoTranslator(com.db4o.YapClass parent, string fieldName
			)
		{
			OnDiagnostic(new com.db4o.diagnostic.DescendIntoTranslator(parent.GetName(), fieldName
				));
		}

		public virtual void NativeQueryUnoptimized(com.db4o.query.Predicate predicate)
		{
			OnDiagnostic(new com.db4o.diagnostic.NativeQueryNotOptimized(predicate));
		}

		private void OnDiagnostic(com.db4o.diagnostic.Diagnostic d)
		{
			if (_listeners == null)
			{
				return;
			}
			com.db4o.foundation.Iterator4 i = _listeners.Iterator();
			while (i.MoveNext())
			{
				((com.db4o.diagnostic.DiagnosticListener)i.Current()).OnDiagnostic(d);
			}
		}

		public virtual void RemoveAllListeners()
		{
			_listeners = null;
		}
	}
}

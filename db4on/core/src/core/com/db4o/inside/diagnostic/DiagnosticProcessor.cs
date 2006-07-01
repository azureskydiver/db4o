namespace com.db4o.inside.diagnostic
{
	/// <exclude></exclude>
	public class DiagnosticProcessor : com.db4o.diagnostic.DiagnosticConfiguration, com.db4o.diagnostic.DiagnosticListener
		, com.db4o.foundation.DeepClone
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

		public virtual object DeepClone(object context)
		{
			return _listeners != null ? new com.db4o.inside.diagnostic.DiagnosticProcessor(new 
				com.db4o.foundation.Collection4(_listeners)) : new com.db4o.inside.diagnostic.DiagnosticProcessor
				();
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
				OnDiagnostic(new com.db4o.inside.diagnostic.DiagnosticMessage(name + " : This class does not contain any persistent fields.\n"
					 + "  Every class in the class hierarchy requires some overhead for the maintenance of a class index."
					 + " Consider removing this class from the hierarchy, if it is not needed."));
			}
		}

		public virtual void NativeQueryUnoptimized(com.db4o.query.Predicate predicate)
		{
			string msg = " The following native query predicate could not be run optimized:\n"
				 + predicate;
			OnDiagnostic(new com.db4o.inside.diagnostic.DiagnosticMessage(msg));
		}

		public virtual void CheckUpdateDepth(int depth)
		{
			if (depth > 1)
			{
				string msg = "Db4o.configure().updateDepth(" + depth + ")\n" + "  Increasing the global updateDepth to a value greater than 1 is only recommended for"
					 + " testing, not for production use. If individual deep updates are needed, consider using"
					 + " ExtObjectContainer#set(object, depth) and make sure to profile the performance of each call.";
				OnDiagnostic(new com.db4o.inside.diagnostic.DiagnosticMessage(msg));
			}
		}

		public virtual void LoadedFromClassIndex(com.db4o.YapClass yc)
		{
			if (IsDb4oClass(yc))
			{
				return;
			}
			OnDiagnostic(new com.db4o.inside.diagnostic.DiagnosticMessage(yc.GetName() + " : Query candidate set could not be loaded from a field index.\n"
				 + "  Consider indexing the fields that you want to query for using: \n" + "  Db4o.configure().objectClass([class]).objectField([fieldName]).indexed(true);"
				));
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
	}
}

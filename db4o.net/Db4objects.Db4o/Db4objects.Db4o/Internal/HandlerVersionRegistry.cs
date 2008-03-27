/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com */

using Db4objects.Db4o.Foundation;
using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Internal.Fieldhandlers;

namespace Db4objects.Db4o.Internal
{
	/// <exclude></exclude>
	public class HandlerVersionRegistry
	{
		private readonly HandlerRegistry _registry;

		private readonly Hashtable4 _versions = new Hashtable4();

		public HandlerVersionRegistry(HandlerRegistry registry)
		{
			_registry = registry;
		}

		public virtual void Put(IFieldHandler handler, int version, ITypeHandler4 replacement
			)
		{
			_versions.Put(new HandlerVersionRegistry.HandlerVersionKey(this, handler, version
				), replacement);
		}

		public virtual ITypeHandler4 CorrectHandlerVersion(ITypeHandler4 originalHandler, 
			int version)
		{
			if (version >= HandlerRegistry.HandlerVersion)
			{
				return originalHandler;
			}
			ITypeHandler4 replacement = (ITypeHandler4)_versions.Get(new HandlerVersionRegistry.HandlerVersionKey
				(this, GenericTemplate(originalHandler), version));
			if (replacement == null)
			{
				return CorrectHandlerVersion(originalHandler, version + 1);
			}
			if (replacement is ICompositeTypeHandler)
			{
				return (ITypeHandler4)((ICompositeTypeHandler)replacement).DeepClone(new TypeHandlerCloneContext
					(_registry, originalHandler, version));
			}
			return replacement;
		}

		private ITypeHandler4 GenericTemplate(ITypeHandler4 handler)
		{
			if (handler is ICompositeTypeHandler)
			{
				return ((ICompositeTypeHandler)handler).GenericTemplate();
			}
			return handler;
		}

		private class HandlerVersionKey
		{
			private readonly IFieldHandler _handler;

			private readonly int _version;

			public HandlerVersionKey(HandlerVersionRegistry _enclosing, IFieldHandler handler
				, int version)
			{
				this._enclosing = _enclosing;
				this._handler = handler;
				this._version = version;
			}

			public override int GetHashCode()
			{
				return this._handler.GetHashCode() + this._version * 4271;
			}

			public override bool Equals(object obj)
			{
				HandlerVersionRegistry.HandlerVersionKey other = (HandlerVersionRegistry.HandlerVersionKey
					)obj;
				return this._handler.Equals(other._handler) && this._version == other._version;
			}

			private readonly HandlerVersionRegistry _enclosing;
		}
	}
}

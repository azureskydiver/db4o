/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

using System;
using Db4objects.Db4o.Activation;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Events;
using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Internal.Diagnostic;
using Db4objects.Db4o.Reflect;
using Db4objects.Db4o.TA;

namespace Db4objects.Db4o.TA
{
	public class TransparentActivationSupport : IConfigurationItem
	{
		public virtual void Prepare(IConfiguration configuration)
		{
		}

		public virtual void Apply(ObjectContainerBase container)
		{
			container.Configure().ActivationDepth(0);
			IEventRegistry factory = EventRegistryFactory.ForObjectContainer(container);
			factory.Instantiated += new Db4objects.Db4o.Events.ObjectEventHandler(new _AnonymousInnerClass23
				(this, container).OnEvent);
			TransparentActivationSupport.TADiagnosticProcessor processor = new TransparentActivationSupport.TADiagnosticProcessor
				(this, container);
			factory.ClassRegistered += new Db4objects.Db4o.Events.ClassEventHandler(new _AnonymousInnerClass39
				(this, processor).OnEvent);
		}

		private sealed class _AnonymousInnerClass23
		{
			public _AnonymousInnerClass23(TransparentActivationSupport _enclosing, ObjectContainerBase
				 container)
			{
				this._enclosing = _enclosing;
				this.container = container;
			}

			public void OnEvent(object sender, Db4objects.Db4o.Events.ObjectEventArgs args)
			{
				ObjectEventArgs oea = (ObjectEventArgs)args;
				object obj = oea.Object;
				if (obj is IActivatable)
				{
					((IActivatable)obj).Bind(this.ActivatorForObject(container, obj));
				}
			}

			private IActivator ActivatorForObject(ObjectContainerBase container, object obj)
			{
				return (IActivator)container.ReferenceForObject(obj);
			}

			private readonly TransparentActivationSupport _enclosing;

			private readonly ObjectContainerBase container;
		}

		private sealed class _AnonymousInnerClass39
		{
			public _AnonymousInnerClass39(TransparentActivationSupport _enclosing, TransparentActivationSupport.TADiagnosticProcessor
				 processor)
			{
				this._enclosing = _enclosing;
				this.processor = processor;
			}

			public void OnEvent(object sender, Db4objects.Db4o.Events.ClassEventArgs args)
			{
				ClassEventArgs cea = (ClassEventArgs)args;
				processor.OnClassRegistered(cea.ClassMetadata());
			}

			private readonly TransparentActivationSupport _enclosing;

			private readonly TransparentActivationSupport.TADiagnosticProcessor processor;
		}

		private sealed class TADiagnosticProcessor
		{
			private readonly ObjectContainerBase _container;

			public TADiagnosticProcessor(TransparentActivationSupport _enclosing, ObjectContainerBase
				 container)
			{
				this._enclosing = _enclosing;
				this._container = container;
			}

			public void OnClassRegistered(ClassMetadata clazz)
			{
				IReflectClass reflectClass = clazz.ClassReflector();
				if (this.ActivatableClass().IsAssignableFrom(reflectClass))
				{
					return;
				}
				if (this.HasOnlyPrimitiveFields(reflectClass))
				{
					return;
				}
				NotTransparentActivationEnabled diagnostic = new NotTransparentActivationEnabled(
					clazz);
				DiagnosticProcessor processor = this._container.Handlers()._diagnosticProcessor;
				processor.OnDiagnostic(diagnostic);
			}

			private IReflectClass ActivatableClass()
			{
				return this._container.Reflector().ForClass(typeof(IActivatable));
			}

			private bool HasOnlyPrimitiveFields(IReflectClass clazz)
			{
				IReflectClass curClass = clazz;
				while (curClass != null)
				{
					IReflectField[] fields = curClass.GetDeclaredFields();
					for (int fieldIdx = 0; fieldIdx < fields.Length; fieldIdx++)
					{
						if (!fields[fieldIdx].GetFieldType().IsPrimitive())
						{
							return false;
						}
					}
					curClass = curClass.GetSuperclass();
				}
				return true;
			}

			private readonly TransparentActivationSupport _enclosing;
		}
	}
}

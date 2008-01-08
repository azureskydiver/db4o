/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.ta;

import com.db4o.*;
import com.db4o.activation.*;
import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.internal.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.diagnostic.*;
import com.db4o.reflect.*;

// TODO: unbindOnClose should be configurable
public class TransparentActivationSupport implements ConfigurationItem {

	public void prepare(Configuration configuration) {
		// Nothing to do...
	}

	public void apply(final InternalObjectContainer container) {
		container.configImpl().activationDepthProvider(new TransparentActivationDepthProvider());

		EventRegistry registry = eventRegistryFor(container);
		registry.instantiated().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				bindActivatableToActivator((ObjectEventArgs) args);
			}
		});
		registry.created().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				bindActivatableToActivator((ObjectEventArgs) args);
			}
		});

		final TADiagnosticProcessor processor = new TADiagnosticProcessor(container);
		registry.classRegistered().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				ClassEventArgs cea = (ClassEventArgs) args;
				processor.onClassRegistered(cea.classMetadata());
			}
		});
	}

	private EventRegistry eventRegistryFor(final ObjectContainer container) {
		return EventRegistryFactory.forObjectContainer(container);
	}
	
	private void bindActivatableToActivator(ObjectEventArgs oea) {
		Object obj = oea.object();
		if (obj instanceof Activatable) {
			final Transaction transaction = (Transaction) oea.transaction();
			final ObjectReference objectReference = transaction.referenceForObject(obj);
			bind(obj, activatorForObject(transaction, objectReference));
			unbindActivatableUponClosing(transaction, objectReference);
		}
	}

	private void bind(Object activatable, final Activator activator) {
		((Activatable) activatable).bind(activator);
	}
	
	private void unbindActivatableUponClosing(Transaction transaction, final ObjectReference objectReference) {
		final EventRegistry eventRegistry = eventRegistryFor(transaction.objectContainer());
		eventRegistry.closing().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				final Object obj = objectReference.getObject();
				if (obj == null) {
					return;
				}
				bind(obj, null);
			}
		});
	}

	private Activator activatorForObject(final Transaction transaction, ObjectReference objectReference) {
		
		if (isEmbeddedClient(transaction)) {
			return new TransactionalActivator(transaction, objectReference);
		}
		return objectReference;
	}

	private boolean isEmbeddedClient(Transaction transaction) {
		return transaction.objectContainer() instanceof EmbeddedClientObjectContainer;
	}

	private final class TADiagnosticProcessor {
	    
		private final InternalObjectContainer _container;

		public TADiagnosticProcessor(InternalObjectContainer container) {
			_container = container;
		}

		public void onClassRegistered(ClassMetadata clazz) {
			// if(Platform4.isDb4oClass(clazz.getName())) {
			// return;
			// }
			ReflectClass reflectClass = clazz.classReflector();
			if (activatableClass().isAssignableFrom(reflectClass)) {
				return;
			}
			if (hasOnlyPrimitiveFields(reflectClass)) {
				return;
			}
			NotTransparentActivationEnabled diagnostic = new NotTransparentActivationEnabled(
					clazz);
			DiagnosticProcessor processor = _container.handlers()._diagnosticProcessor;
			processor.onDiagnostic(diagnostic);
		}

		private ReflectClass activatableClass() {
			return _container.reflector().forClass(Activatable.class);
		}

		private boolean hasOnlyPrimitiveFields(ReflectClass clazz) {
			ReflectClass curClass = clazz;
			while (curClass != null) {
				final ReflectField[] fields = curClass.getDeclaredFields();
				if (!hasOnlyPrimitiveFields(fields)) {
					return false;
				}
				curClass = curClass.getSuperclass();
			}
			return true;
		}

		private boolean hasOnlyPrimitiveFields(ReflectField[] fields) {
			for (int i = 0; i < fields.length; i++) {
				if (!isPrimitive(fields[i])) {
					return false;
				}
			}
			return true;
		}

		private boolean isPrimitive(final ReflectField field) {
			return field.getFieldType().isPrimitive();
		}
	}
}
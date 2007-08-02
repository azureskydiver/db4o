/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.ta;

import com.db4o.activation.Activator;
import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.internal.*;
import com.db4o.internal.diagnostic.*;
import com.db4o.reflect.*;

public class TransparentActivationSupport implements ConfigurationItem {

	public void prepare(Configuration configuration) {
		// Nothing to do...
	}

	public void apply(final ObjectContainerBase container) {
		container.configure().activationDepth(0);

		EventRegistry factory = EventRegistryFactory
				.forObjectContainer(container);

		factory.instantiated().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				ObjectEventArgs oea = (ObjectEventArgs) args;
				Object obj = oea.object();
				if (obj instanceof Activatable) {
					((Activatable) obj).bind(activatorForObject(container, obj));
				}
			}

			private Activator activatorForObject(
					final ObjectContainerBase container_, Object obj) {
			    // FIXME: Using ObjectContainerBase here won't work for MTOC.
				return container_.transaction().referenceForObject(obj);
			}
		});

		final TADiagnosticProcessor processor = new TADiagnosticProcessor(container);
		factory.classRegistered().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				ClassEventArgs cea = (ClassEventArgs) args;
				processor.onClassRegistered(cea.classMetadata());
			}
		});
	}

	private final class TADiagnosticProcessor {
		private final ObjectContainerBase _container;

		public TADiagnosticProcessor(ObjectContainerBase container) {
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
				ReflectField[] fields = curClass.getDeclaredFields();
				for (int fieldIdx = 0; fieldIdx < fields.length; fieldIdx++) {
					if (!fields[fieldIdx].getFieldType().isPrimitive()) {
						return false;
					}
				}
				curClass = curClass.getSuperclass();
			}
			return true;
		}
	};
}
package com.db4o.ta;

import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.internal.*;
import com.db4o.internal.diagnostic.*;
import com.db4o.reflect.*;

public class TransparentActivationSupport implements ConfigurationItem {

	public void apply(final ObjectContainerBase container) {
		container.configure().activationDepth(0);
		
		EventRegistry factory = EventRegistryFactory.forObjectContainer(container);
		factory.instantiated().addListener(
			new EventListener4() {
				public void onEvent(Event4 e, EventArgs args) {
					ObjectEventArgs oea = (ObjectEventArgs)args;
					if (oea.object() instanceof Activatable) {
						((Activatable)oea.object()).bind(container);
					}
				}
			});
		factory.classRegistered().addListener(new TAClassRegistrationListener(container));
	}
	
	public void prepare(Configuration configuration) {
		// Nothing to do...
	}

	private static final class TAClassRegistrationListener implements EventListener4 {
		private final ObjectContainerBase _container;
		private final ReflectClass _activatableClass;

		private TAClassRegistrationListener(ObjectContainerBase container) {
			_container = container;
			_activatableClass = container.reflector().forClass(Activatable.class);
		}

		public void onEvent(Event4 e, EventArgs args) {
			ClassEventArgs cea = (ClassEventArgs) args;
			ClassMetadata clazz = cea.classMetadata();
//			if(Platform4.isDb4oClass(clazz.getName())) {
//				return;
//			}
			ReflectClass reflectClass = clazz.classReflector();
			if(_activatableClass.isAssignableFrom(reflectClass)) {
				return;
			}
			if(hasOnlyPrimitiveFields(reflectClass)) {
				return;
			}
			NotTransparentActivationEnabled diagnostic = new NotTransparentActivationEnabled(clazz);
			DiagnosticProcessor processor = _container.handlers()._diagnosticProcessor;
			processor.onDiagnostic(diagnostic);
		}

		private boolean hasOnlyPrimitiveFields(ReflectClass clazz) {
			ReflectClass curClass = clazz;
			while(curClass != null) {
				ReflectField[] fields = curClass.getDeclaredFields();
				for (int fieldIdx = 0; fieldIdx < fields.length; fieldIdx++) {
					if(!fields[fieldIdx].getFieldType().isPrimitive()) {
						return false;
					}
				}
				curClass = curClass.getSuperclass();
			}
			return true;
		}
	}
	
}

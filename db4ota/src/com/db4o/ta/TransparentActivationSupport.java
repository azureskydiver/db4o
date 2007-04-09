package com.db4o.ta;

import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.internal.*;

public class TransparentActivationSupport implements ConfigurationItem {

	public void apply(final ObjectContainerBase container) {
		container.configure().activationDepth(0);
		
		EventRegistryFactory.forObjectContainer(container).instantiated().addListener(
			new EventListener4() {
				public void onEvent(Event4 e, EventArgs args) {
					ObjectEventArgs oea = (ObjectEventArgs)args;
					if (oea.object() instanceof Activatable) {
						((Activatable)oea.object()).bind(container);
					}
				}
			});
	}

}

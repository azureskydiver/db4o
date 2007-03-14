/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.constraints;

import com.db4o.events.*;
import com.db4o.internal.*;

/**
 * @sharpen.ignore
 */
class ConstraintPlatform {

	public static void addCommittingHandler(final ObjectContainerBase container, final CommitHandler handler) {
		EventRegistryFactory.forObjectContainer(container).committing().addListener(new EventListener4() {

			public void onEvent(Event4 e, EventArgs args) {
				handler.handle(container, (CommitEventArgs)args);
			}
		});
	}
}

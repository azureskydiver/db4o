/* Copyright (C) 2009  Versant Corp.  http://www.db4o.com */

package com.db4o.monitoring;

import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.slots.*;

/**
 * Publishes statistics about freespace to JMX.
 */
@decaf.Ignore
public class FreespaceMonitoringSupport implements ConfigurationItem {

	public void apply(InternalObjectContainer container) {
		if(! (container instanceof LocalObjectContainer) || container.configImpl().isReadOnly()){
			return;
		}
		LocalObjectContainer localObjectContainer = (LocalObjectContainer) container;
		FreespaceManager freespaceManager = localObjectContainer.freespaceManager();
		final Freespace freespace = Db4oMBeans.newFreespaceMBean(container);
		freespaceManager.listener(freespace);
		addClosingListener(container, freespace);
		freespaceManager.traverse(new Visitor4<Slot>() {
			public void visit(Slot slot) {
				freespace.slotAdded(slot.length());
			}
		});
	}
	
	private void addClosingListener(final InternalObjectContainer container,
			final Freespace freespace) {
		final EventRegistry events = EventRegistryFactory.forObjectContainer(container);
		events.closing().addListener(new EventListener4<ObjectContainerEventArgs>() {
			public void onEvent(Event4<ObjectContainerEventArgs> e, ObjectContainerEventArgs args) {
				freespace.unregister();
			}
		});
	}


	public void prepare(Configuration configuration) {
		// do nothing
	}



}

package com.db4o.ibs;

import com.db4o.*;
import com.db4o.events.*;
import com.db4o.internal.*;

/**
 * Listen to {@link ObjectContainer} events and publishes {@link ChangeSet} instances
 * provided by a {@link ChangeSetBuilder} to an {@link ChangeSetListener}.
 */
public class ChangeSetPublisher {

	private final ChangeSetBuilder _builder;
	private final ChangeSetListener _listener;

	public ChangeSetPublisher(ObjectContainer container,
			ChangeSetBuilder builder,
			ChangeSetListener listener) {
		
		_builder = builder;
		_listener = listener;
		
		final EventRegistry registry = EventRegistryFactory.forObjectContainer(container);
		registry.creating().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				onCreating((ObjectEventArgs)args);
			}
		});
		
		registry.committing().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				onCommitting((CommitEventArgs)args);
			}
		});
	}

	protected void onCommitting(CommitEventArgs args) {
		final ChangeSet cs = _builder.build(transaction(args));
		_listener.onChange(cs);
	}

	protected void onCreating(ObjectEventArgs args) {
		_builder.create(transaction(args), args.object());
	}

	private Transaction transaction(TransactionalEventArgs args) {
		return (Transaction) args.transaction();
	}
}

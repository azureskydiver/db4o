package com.db4o.ibs;

import com.db4o.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;

/**
 * Listen to {@link ObjectContainer} events and publishes {@link ChangeSet} instances
 * provided by a {@link ChangeSetEngine} to an {@link ChangeSetListener}.
 */
public class ChangeSetPublisher {

	private final ChangeSetEngine _engine;
	private final ChangeSetListener _listener;

	public ChangeSetPublisher(ChangeSetEngine engine, ChangeSetListener listener) {		
		_engine = engine;
		_listener = listener;
	}

	public void monitor(ObjectContainer container) {
		final EventRegistry registry = EventRegistryFactory.forObjectContainer(container);
		registry.committing().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				onCommitting((CommitEventArgs)args);
			}
		});
	}

	protected void onCommitting(CommitEventArgs args) {
		_listener.onChange(changeSetFor(args));
	}

	private ChangeSet changeSetFor(CommitEventArgs args) {
		final ChangeSetBuilder builder = _engine.newBuilderFor(transaction(args));
		final Iterator4 added = args.added().iterator();
		while (added.moveNext()) {
			builder.added((ObjectInfo)added.current());
		}
		final Iterator4 deleted = args.deleted().iterator();
		while (deleted.moveNext()) {
			builder.deleted((ObjectInfo)deleted.current());
		}
		final Iterator4 updated = args.updated().iterator();
		while (updated.moveNext()) {
			builder.updated((ObjectInfo)updated.current());
		}
		return builder.build();
	}

	private Transaction transaction(TransactionalEventArgs args) {
		return (Transaction) args.transaction();
	}
}

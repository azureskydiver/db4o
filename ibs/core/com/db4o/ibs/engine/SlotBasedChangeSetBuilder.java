package com.db4o.ibs.engine;

import java.util.*;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.ibs.*;
import com.db4o.internal.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.slots.*;

import db4ounit.*;

public class SlotBasedChangeSetBuilder implements ChangeSetBuilder {

	private final LocalTransaction _transaction;
	
	private final List<SlotBasedChange> _changes = new ArrayList<SlotBasedChange>();

	SlotBasedChangeSetBuilder(LocalTransaction transaction) {
		_transaction = transaction;
	}

	public void added(ObjectInfo object) {
		// TODO Auto-generated method stub

	}

	public ChangeSet build() {
		if (_changes.isEmpty()) {
			return null;
		}
		return new SlotBasedChangeSet(_changes);
	}

	public void deleted(ObjectInfo object) {
		// TODO Auto-generated method stub

	}

	public void updated(ObjectInfo object) {
		
		final ArrayList<FieldChange> fieldChanges = collectFieldChanges(object);
		if (!fieldChanges.isEmpty()) {
			_changes.add(new UpdateChange(object, fieldChanges));
		}
	}

	private ArrayList<FieldChange> collectFieldChanges(ObjectInfo object) {
		final ArrayList<FieldChange> fieldChanges = new ArrayList<FieldChange>();
		
		final ObjectHeaderContext oldSlotContext = readContextForOldSlot(object);
		final ClassMetadata classMetadata = classMetadataFor(object);
		final Iterator4 fields = classMetadata.fields();
		
		final int initialOffset = oldSlotContext.offset();
		while (fields.moveNext()) {
			oldSlotContext.seek(initialOffset);
			
			final FieldMetadata field = (FieldMetadata) fields.current();
			final Object currentFieldValue = field.getOn(_transaction, object.getObject());
			final Object oldFieldValue = oldSlotContext.readFieldValue(classMetadata, field);
			if (!Check.objectsAreEqual(currentFieldValue, oldFieldValue)) {
				fieldChanges.add(new FieldChange(field, currentFieldValue));
			}
		}
		return fieldChanges;
	}

	private ObjectHeaderContext readContextForOldSlot(ObjectInfo object) {
		final SlotChange change = slotChangeFor(object);
		final Slot oldSlot = change.oldSlot();
		final ByteArrayBuffer oldSlotBuffer = arrayBufferFor(oldSlot);
		return new ObjectHeaderContext(_transaction, oldSlotBuffer, new ObjectHeader(_transaction.container(), oldSlotBuffer)) {
		};
	}

	private SlotChange slotChangeFor(ObjectInfo object) {
		return _transaction.findSlotChange((int) object.getInternalID());
	}
	
	private ByteArrayBuffer arrayBufferFor(final Slot slot) {
		final ByteArrayBuffer oldSlotBuffer = new ByteArrayBuffer(slot.length());
		oldSlotBuffer.readEncrypt(_transaction.container(), slot.address());
		return oldSlotBuffer;
	}

	private ClassMetadata classMetadataFor(ObjectInfo object) {
		return ((LazyObjectReference)object).reference().classMetadata();
	}
}

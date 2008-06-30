package com.db4o.ibs.engine;

import java.util.*;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.ibs.*;
import com.db4o.internal.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.slots.*;

public class SlotBasedChangeSetBuilder implements ChangeSetBuilder {

	private final LocalTransaction _transaction;
	
	private final List<SlotBasedChange> _changes = new ArrayList<SlotBasedChange>();

	SlotBasedChangeSetBuilder(LocalTransaction transaction) {
		_transaction = transaction;
	}

	public void added(ObjectInfo objectInfo) {
		_changes.add(new NewObjectChange(objectInfo));
	}

	public ChangeSet build() {
		if (_changes.isEmpty()) {
			return null;
		}
		return new SlotBasedChangeSet(_changes);
	}

	public void deleted(ObjectInfo object) {
		_changes.add(new DeleteChange(object));
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
			if(field instanceof VirtualFieldMetadata){
			    continue;
			}
			final Object currentFieldValue = field.getOn(_transaction, object.getObject());
			
			if (fieldValueHasChanged(oldSlotContext, classMetadata, field, currentFieldValue)) {
				fieldChanges.add(new FieldChange(field, currentFieldValue));
			}
		}
		return fieldChanges;
	}

	private boolean fieldValueHasChanged(
			final ObjectHeaderContext oldSlotContext,
			final ClassMetadata classMetadata,
			final FieldMetadata field,
			final Object currentFieldValue) {
		
		if (oldSlotContext.useDedicatedSlot(oldSlotContext.correctHandlerVersion(field.getHandler()))) {
			final int oldId = readIdAtField(oldSlotContext, classMetadata, field);
			if (currentFieldValue == null) {
				return oldId != 0;
			}
			return oldId != idFor(currentFieldValue);
		}
		
		final Object oldFieldValue = oldSlotContext.readFieldValue(classMetadata, field);
		return !objectsAreEqual(currentFieldValue, oldFieldValue);
	}

	private long idFor(final Object currentFieldValue) {
		return _transaction.container().getID(currentFieldValue);
	}

	private int readIdAtField(final ObjectHeaderContext oldSlotContext,
			final ClassMetadata classMetadata,
			final FieldMetadata field) {
		if (!oldSlotContext.seekToField(classMetadata, field)) {
			return 0;
		}
		return oldSlotContext.readInt();
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

	private boolean objectsAreEqual(Object expected, Object actual) {
		return expected == actual
			|| (expected != null
				&& actual != null
				&& expected.equals(actual));
	}
}

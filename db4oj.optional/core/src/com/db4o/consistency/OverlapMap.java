package com.db4o.consistency;

import java.util.*;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;

class OverlapMap {

	private Set<Pair<SlotWithSource, SlotWithSource>> _dupes = new HashSet<Pair<SlotWithSource,SlotWithSource>>();
	private TreeIntObject _slots = null;
	private final BlockConverter _blockConverter;
	
	public OverlapMap(BlockConverter blockConverter) {
		_blockConverter = blockConverter;
	}

	public void add(Slot slot, SlotSource source) {
		add(new SlotWithSource(slot, source));
	}

	public void add(SlotWithSource slot) {
		if(TreeIntObject.find(_slots, new TreeIntObject(slot._slot.address())) != null) {
			_dupes.add(new Pair<SlotWithSource, SlotWithSource>(byAddress(slot._slot.address()), slot));
		}
		_slots = (TreeIntObject) TreeIntObject.add(_slots, new TreeIntObject(slot._slot.address(), slot));
	}
	
	public Set<Pair<SlotWithSource, SlotWithSource>> overlaps() {
		final Set<Pair<SlotWithSource, SlotWithSource>> overlaps = new HashSet<Pair<SlotWithSource, SlotWithSource>>();
		final ByRef<SlotWithSource> prevSlot = ByRef.newInstance();
		TreeIntObject.traverse(_slots, new Visitor4<TreeIntObject>() {
			public void visit(TreeIntObject tree) {
				SlotWithSource curSlot = (SlotWithSource) tree._object;
				if(prevSlot.value != null && prevSlot.value._slot.address() + _blockConverter.bytesToBlocks(prevSlot.value._slot.length()) > curSlot._slot.address()) {
					overlaps.add(new Pair<SlotWithSource, SlotWithSource>(prevSlot.value, curSlot));
				}
				prevSlot.value = curSlot;
			}
		});
		return overlaps;
	}

	public Set<Pair<SlotWithSource, SlotWithSource>> dupes() {
		return _dupes;
	}
	
	private SlotWithSource byAddress(int address) {
		TreeIntObject tree = (TreeIntObject) TreeIntObject.find(_slots, new TreeIntObject(address, null));
		return tree == null ? null : (SlotWithSource)tree._object;
	}
}

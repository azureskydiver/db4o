/* Copyright (C) 2010  Versant Inc.   http://www.db4o.com */
package com.db4o.consistency;

import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.classindex.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.slots.*;

@decaf.Ignore(decaf.Platform.JDK11)
public class ConsistencyChecker {

	private final LocalObjectContainer _db;
	private final List<SlotWithSource> bogusSlots = new LinkedList<SlotWithSource>();
	private TreeIntObject mappings;

	public static class SlotSource {
		public final static SlotSource ID_SYSTEM = new SlotSource("IdSystem");
		public final static SlotSource FREESPACE = new SlotSource("Freespace");

		private final String _name;
		
		private SlotSource(String name) {
			_name = name;
		}
		
		@Override
		public String toString() {
			return _name;
		}
	}

	public static class SlotWithSource {
		public final Slot slot;
		public final SlotSource source;

		public SlotWithSource(Slot slot, SlotSource source) {
			this.slot = slot;
			this.source = source;
		}
		
		@Override
		public String toString() {
			return slot + "(" + source + ")";
		}
	}
	
	public static class ConsistencyReport {
		
		private static final int MAX_REPORTED_ITEMS = 50;
		final List<SlotWithSource> bogusSlots;
		final List<Pair<SlotWithSource,SlotWithSource>> overlaps;
		final List<Pair<String,Integer>> invalidObjectIds;
		
		public ConsistencyReport(List<SlotWithSource> bogusSlots, List<Pair<SlotWithSource,SlotWithSource>> overlaps, List<Pair<String,Integer>> invalidClassIds) {
			this.bogusSlots = bogusSlots;
			this.overlaps = overlaps;
			this.invalidObjectIds = invalidClassIds;
		}
		
		public boolean consistent() {
			return bogusSlots.size() == 0 && overlaps.size() == 0 && invalidObjectIds.size() == 0;
		}
		
		@Override
		public String toString() {
			if(consistent()) {
				return "no inconsistencies detected";
			}
			StringBuffer message = new StringBuffer("INCONSISTENCIES DETECTED\n")
				.append(overlaps.size() + " overlaps\n")
				.append(bogusSlots.size() + " bogus slots\n")
				.append(invalidObjectIds.size() + " invalid class ids\n");
			message.append("(slot lengths are non-blocked)\n");
			appendInconsistencyReport(message, "OVERLAPS", overlaps);
			appendInconsistencyReport(message, "BOGUS SLOTS", bogusSlots);
			appendInconsistencyReport(message, "INVALID OBJECT IDS", invalidObjectIds);
			return message.toString();
		}
		
		private <T> void appendInconsistencyReport(StringBuffer str, String title, Collection<T> entries) {
			if(entries.size() != 0) {
				str.append(title + "\n");
				int count = 0;
				for (T entry : entries) {
					str.append(entry).append("\n");
					count++;
					if(count > MAX_REPORTED_ITEMS) {
						str.append("and more...\n");
						break;
					}
				}
			}
		}
	}
	
	public ConsistencyChecker(ObjectContainer db) {
		_db = (LocalObjectContainer) db;
		
	}
	
	public ConsistencyReport checkSlotConsistency() {
		mapIdSystem();
		mapFreespace();
		return new ConsistencyReport(bogusSlots, collectOverlaps(), checkClassIndices());
	}

	private List<Pair<String,Integer>> checkClassIndices() {
		final List<Pair<String,Integer>> invalidIds = new LinkedList<Pair<String,Integer>>();
		final IdSystem idSystem= _db.idSystem();
		if(!(idSystem instanceof BTreeIdSystem)) {
			return invalidIds;
		}
		ClassMetadataIterator clazzIter = _db.classCollection().iterator();
		while(clazzIter.moveNext()) {
			final ClassMetadata clazz = clazzIter.currentClass();
			if(!clazz.hasClassIndex()) {
				continue;
			}
			BTreeClassIndexStrategy index = (BTreeClassIndexStrategy) clazz.index();
			index.traverseAll(_db.systemTransaction(), new Visitor4<Integer>() {
				public void visit(Integer id) {
					try {
						Slot slot = idSystem.committedSlot(id);
						if(Slot.isNull(slot)) {
							invalidIds.add(new Pair(clazz.getName(), id));
						}
					}
					catch(InvalidIDException exc) {
						invalidIds.add(new Pair(clazz.getName(), id));
					}
				}
			});
		}
		return invalidIds;
	}

	private List<Pair<SlotWithSource, SlotWithSource>> collectOverlaps() {
		final BlockConverter blockConverter = _db.blockConverter();
		final List<Pair<SlotWithSource,SlotWithSource>> overlaps = new LinkedList<Pair<SlotWithSource,SlotWithSource>>();
		final ByRef<SlotWithSource> prevSlot = ByRef.newInstance();
		mappings.traverse(new Visitor4<TreeIntObject>() {
			public void visit(TreeIntObject obj) {
				SlotWithSource curSlot = (SlotWithSource) obj._object;
				if(prevSlot.value != null) {
					if(prevSlot.value.slot.address() + blockConverter.toBlockedLength(prevSlot.value.slot).length() > curSlot.slot.address()) {
						overlaps.add(new Pair<SlotWithSource, SlotWithSource>(prevSlot.value, curSlot));
					}
				}
				prevSlot.value = curSlot;
			}
		});
		return overlaps;
	}

	private void mapFreespace() {
		_db.freespaceManager().traverse(new Visitor4<Slot>() {
			public void visit(Slot slot) {
				if(slot.address() < 0) {
					bogusSlots.add(new SlotWithSource(slot, SlotSource.FREESPACE));
				}
				addMapping(slot, SlotSource.FREESPACE);
			}
		});
	}

	private void mapIdSystem() {
		IdSystem idSystem= _db.idSystem();
		if(idSystem instanceof BTreeIdSystem) {
			((BTreeIdSystem)idSystem).traverseIds(new Visitor4<IdSlotMapping>() {
				public void visit(IdSlotMapping mapping) {
					if(mapping._address < 0) {
						bogusSlots.add(new SlotWithSource(mapping.slot(), SlotSource.ID_SYSTEM));
					}
					if(mapping._address > 0) {
						addMapping(mapping.slot(), SlotSource.ID_SYSTEM);
					}
				}
			});
		}
	}

	private void addMapping(Slot slot, SlotSource source) {
		mappings = TreeIntObject.add(mappings, slot.address(), new SlotWithSource(slot, source));
	}
}

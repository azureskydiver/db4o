/* Copyright (C) 2010  Versant Inc.   http://www.db4o.com */
package com.db4o.consistency;

import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.classindex.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.slots.*;


public class ConsistencyChecker {

	private final List<SlotWithSource> _bogusSlots = new ArrayList<SlotWithSource>();
	private final LocalObjectContainer _db;
	private final OverlapMap _overlaps;

	public static void main(String[] args) {
		EmbeddedObjectContainer db = Db4oEmbedded.openFile(args[0]);
		try {
			System.out.println(new ConsistencyChecker(db).checkSlotConsistency());
		}
		finally {
			db.close();
		}
	}
	
	public ConsistencyChecker(ObjectContainer db) {
		_db = (LocalObjectContainer) db;
		_overlaps = new OverlapMap(_db.blockConverter());
	}
	
	public ConsistencyReport checkSlotConsistency() {
		return _db.syncExec(new Closure4<ConsistencyReport>() {
			@Override
			public ConsistencyReport run() {
				mapIdSystem();
				mapFreespace();
				return new ConsistencyReport(
						_bogusSlots, 
						_overlaps, 
						checkClassIndices(), 
						checkFieldIndices());
			}
		});
	}

	private List<Pair<String,Integer>> checkClassIndices() {
		final List<Pair<String,Integer>> invalidIds = new ArrayList<Pair<String,Integer>>();
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
			index.traverseIds(_db.systemTransaction(), new Visitor4<Integer>() {
				public void visit(Integer id) {
					if(!idIsValid(id)) {
						invalidIds.add(new Pair(clazz.getName(), id));
					}
				}
			});
		}
		return invalidIds;
	}
	
	private List<Pair<String, Integer>> checkFieldIndices() {
		final List<Pair<String,Integer>> invalidIds = new ArrayList<Pair<String,Integer>>();
		ClassMetadataIterator clazzIter = _db.classCollection().iterator();
		while(clazzIter.moveNext()) {
			final ClassMetadata clazz = clazzIter.currentClass();
			clazz.traverseDeclaredFields(new Procedure4<FieldMetadata>() {
				public void apply(final FieldMetadata field) {
					if(!field.hasIndex()) {
						return;
					}
					BTree fieldIndex = field.getIndex(_db.systemTransaction());
					fieldIndex.traverseKeys(_db.systemTransaction(), new Visitor4<FieldIndexKey>() {
						public void visit(FieldIndexKey fieldIndexKey) {
							int parentID = fieldIndexKey.parentID();
							if(!idIsValid(parentID)) {
								invalidIds.add(new Pair<String, Integer>(clazz.getName() + "#" + field.getName(), parentID));
							}
						}
					});
				}
			});
		}
		return invalidIds;
	}

	private boolean idIsValid(int id) {
		try {
			return !Slot.isNull(_db.idSystem().committedSlot(id));
		}
		catch(InvalidIDException exc) {
			return false;
		}
	}

	private void mapFreespace() {
		_db.freespaceManager().traverse(new Visitor4<Slot>() {
			public void visit(Slot slot) {
				if(isBogusSlot(slot.address(), slot.length())) {
					_bogusSlots.add(new SlotWithSource(slot, SlotSource.FREESPACE));
				}
				_overlaps.add(slot, SlotSource.FREESPACE);
			}
		});
	}

	private void mapIdSystem() {
		IdSystem idSystem= _db.idSystem();
		if(!(idSystem instanceof BTreeIdSystem)) {
			System.err.println("No btree id system found - not mapping ids.");
			return;
		}
		((BTreeIdSystem)idSystem).traverseIds(new Visitor4<IdSlotMapping>() {
			public void visit(IdSlotMapping mapping) {
				if(isBogusSlot(mapping._address, mapping._length)) {
					_bogusSlots.add(new SlotWithSource(mapping.slot(), SlotSource.ID_SYSTEM));
				}
				if(mapping._address > 0) {
					_overlaps.add(mapping.slot(), SlotSource.ID_SYSTEM);
				}
			}
		});
		idSystem.traverseOwnSlots(new Procedure4<Slot>() {
			@Override
			public void apply(Slot slot) {
				if(isBogusSlot(slot.address(), slot.length())) {
					_bogusSlots.add(new SlotWithSource(slot, SlotSource.ID_SYSTEM));
				}
				_overlaps.add(slot, SlotSource.ID_SYSTEM);
			}
		});
	}

	private boolean isBogusSlot(int address, int length) {
		return address < 0 || (long)address + length > _db.fileLength();
	}
	
}

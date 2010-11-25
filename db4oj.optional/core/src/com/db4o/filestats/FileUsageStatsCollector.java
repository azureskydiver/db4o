/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.filestats;

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.classindex.*;
import com.db4o.internal.collections.*;
import com.db4o.internal.fileheader.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.slots.*;
import com.db4o.io.*;

@decaf.Ignore
public class FileUsageStatsCollector {
	
	private static interface MiscCollector {
		long collectFor(int id);
	}
	
	private Map<String, MiscCollector> MISC_COLLECTORS;
	
	{
		MISC_COLLECTORS = new HashMap<String, MiscCollector>();
		MISC_COLLECTORS.put(BigSet.class.getName(), new BigSetMiscCollector());
	}
	
	public static void main(String[] args) {
		String dbPath = args[0];
		System.out.println(dbPath + ": " + new File(dbPath).length());
		FileUsageStats stats = runStats(dbPath);
		System.out.println(stats);
	}

	static FileUsageStats runStats(String dbPath) {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.file().storage(new FileStorage());
		EmbeddedObjectContainer db = Db4oEmbedded.openFile(config, dbPath);
		try {
			return new FileUsageStatsCollector(db).collectStats();
		}
		finally {
			db.close();
		}
	}
	
	private final LocalObjectContainer _db;
	private MergingSlotMap _slots;
	private BlockConverter _blockConverter;
	
	public FileUsageStatsCollector(ObjectContainer db) {
		_db = (LocalObjectContainer) db;
		_slots = new MergingSlotMap();
		byte blockSize = _db.blockSize();
		_blockConverter = blockSize > 1 ? new BlockSizeBlockConverter(blockSize) : new DisabledBlockConverter();
	}

	public FileUsageStats collectStats() {
		final FileUsageStats stats = new FileUsageStats(_db.fileLength(), fileHeaderUsage(), idSystemUsage(), freespace(), classMetadataUsage(), freespaceUsage());
		Set<ClassNode> classRoots = ClassNode.buildHierarchy(_db.classCollection());
		for (ClassNode classRoot : classRoots) {
			collectClassSlots(classRoot.classMetadata());
			collectClassStats(stats, classRoot);
		}
		stats._slots = _slots;
		System.out.println("SLOTS:");
		logSlots(_slots.merged());
		System.out.println("GAPS:");
		logSlots(_slots.gaps(_db.fileLength()));
		return stats;
	}

	private void logSlots(Iterable<Slot> slots) {
		int totalLength = 0;
		for (Slot gap : slots) {
			totalLength += gap.length();
			System.out.println(gap);
		}
		System.out.println("TOTAL: " + totalLength);
		System.out.println();
	}
	
	private long collectClassStats(FileUsageStats stats, ClassNode classNode) {
		Iterator<ClassNode> subClassIter = classNode.subClasses();
		long subClassSlotUsage = 0;
		while(subClassIter.hasNext()) {
			subClassSlotUsage += collectClassStats(stats, subClassIter.next());
		}
		ClassMetadata clazz = classNode.classMetadata();
		long classIndexUsage = 0;
		if(clazz.hasClassIndex()) {
			classIndexUsage = bTreeUsage(((BTreeClassIndexStrategy)clazz.index()).btree());
		}
		long fieldIndexUsage = fieldIndexUsage(clazz);
		InstanceUsage instanceUsage = classSlotUsage(clazz);
		long totalSlotUsage = instanceUsage.slotUsage;
		long ownSlotUsage = totalSlotUsage - subClassSlotUsage;
		ClassUsageStats classStats = new ClassUsageStats(clazz.getName(), ownSlotUsage, classIndexUsage, fieldIndexUsage, instanceUsage.miscUsage);
		stats.addClassStats(classStats);
		return totalSlotUsage;
	}

	private long fieldIndexUsage(ClassMetadata classMetadata) {
		final LongByRef usage = new LongByRef(); 
		classMetadata.traverseDeclaredFields(new Procedure4<FieldMetadata>() {
			public void apply(FieldMetadata field) {
				if(!field.hasIndex()) {
					return;
				}
				usage.value += bTreeUsage(field.getIndex(_db.systemTransaction()));
			}
		});
		return usage.value;
	}
	
	private long bTreeUsage(BTree btree) {
		return bTreeUsage(_db.idSystem(), btree);
	}

	private long bTreeUsage(IdSystem idSystem, BTree btree) {
		Iterator4<Integer> nodeIter = btree.allNodeIds(_db.systemTransaction());
		long usage = idSystem.committedSlot(btree.getID()).length();
		_slots.add(idSystem.committedSlot(btree.getID()));
		while(nodeIter.moveNext()) {
			Integer curNodeId = nodeIter.current();
			_slots.add(idSystem.committedSlot(curNodeId));
			usage += idSystem.committedSlot(curNodeId).length();
		}
		return usage;
	}

	private InstanceUsage classSlotUsage(ClassMetadata clazz) {
		if(!clazz.hasClassIndex()) {
			return new InstanceUsage(0, 0);
		}
		final MiscCollector miscCollector = MISC_COLLECTORS.get(clazz.getName());
		final LongByRef slotUsage = new LongByRef();
		final LongByRef miscUsage = new LongByRef();
		BTreeClassIndexStrategy index = (BTreeClassIndexStrategy) clazz.index();
		index.traverseAll(_db.systemTransaction(), new Visitor4<Integer>() {
			public void visit(Integer id) {
				slotUsage.value += slotSizeForId(id);
				if(miscCollector != null) {
					miscUsage.value += miscCollector.collectFor(id);
				}
			}
		});
		return new InstanceUsage(slotUsage.value, miscUsage.value);
	}

	private void collectClassSlots(ClassMetadata clazz) {
		if(!clazz.hasClassIndex()) {
			return;
		}
		BTreeClassIndexStrategy index = (BTreeClassIndexStrategy) clazz.index();
		index.traverseAll(_db.systemTransaction(), new Visitor4<Integer>() {
			public void visit(Integer id) {
				_slots.add(slot(id));
			}
		});
	}

	private long freespace() {
		_db.freespaceManager().traverse(new Visitor4<Slot>() {
			public void visit(Slot slot) {
				_slots.add(slot);
			}
		});
		return _db.freespaceManager().totalFreespace();
	}

	private long freespaceUsage() {
		return freespaceUsage(_db.freespaceManager());
	}

	private long freespaceUsage(FreespaceManager fsm) {
		if(fsm instanceof InMemoryFreespaceManager) {
			return 0;
		}
		if(fsm instanceof BTreeFreespaceManager) {
			return bTreeUsage((BTree)fieldValue(fsm, "_slotsByAddress")) + bTreeUsage((BTree)fieldValue(fsm, "_slotsByLength")); 
		}
		if(fsm instanceof BlockAwareFreespaceManager) {
			return freespaceUsage((FreespaceManager) fieldValue(fsm, "_delegate"));
		}
		throw new IllegalStateException("Unknown freespace manager: " + fsm);
	}
	
	private long idSystemUsage() {
		IdSystem idSystem = _db.idSystem();
		long usage = 0;
		while(idSystem instanceof BTreeIdSystem) {
			IdSystem parentIdSystem = fieldValue(idSystem, "_parentIdSystem");
			usage += bTreeUsage(parentIdSystem, (BTree)fieldValue(idSystem, "_bTree"));
			PersistentIntegerArray persistentState = (PersistentIntegerArray)fieldValue(idSystem, "_persistentState");
			int persistentStateId = persistentState.getID();
			Slot persistentStateSlot = parentIdSystem.committedSlot(persistentStateId);
			_slots.add(persistentStateSlot);
			usage += persistentStateSlot.length();
			idSystem = parentIdSystem;
		}
		if(idSystem instanceof InMemoryIdSystem) {
			Slot idSystemSlot = fieldValue(idSystem, "_slot");
			usage += idSystemSlot.length();
			_slots.add(idSystemSlot);
		}
		return usage;
	}
	
	private long classMetadataUsage() {
		long usage = slotSizeForId(_db.classCollection().getID());
		_slots.add(slot(_db.classCollection().getID()));
		Iterator4<Integer> classIdIter = _db.classCollection().ids();
		while(classIdIter.moveNext()) {
			int curClassId = classIdIter.current();
			usage += slotSizeForId(curClassId);
			_slots.add(slot(curClassId));
		}
		return usage;
	}
	
	private long fileHeaderUsage() {
		int headerLength = _db.getFileHeader().length();
		int usage = _blockConverter.blockAlignedBytes(headerLength);
		FileHeaderVariablePart2 variablePart = (FileHeaderVariablePart2)fieldValue(_db.getFileHeader(), "_variablePart");
		usage += _blockConverter.blockAlignedBytes(variablePart.marshalledLength());
		_slots.add(new Slot(0, headerLength));
		_slots.add(new Slot(variablePart.address(), variablePart.marshalledLength()));
		return usage;
	}
	
	private int slotSizeForId(int id) {
		return slot(id).length();
	}

	private static <T> T fieldValue(Object parent, String fieldName) {
		return (T) Reflection4.getFieldValue(parent, fieldName);
	}
	
	private static class InstanceUsage {
		public final long slotUsage;
		public final long miscUsage;
		
		public InstanceUsage(long slotUsage, long miscUsage) {
			this.slotUsage = slotUsage;
			this.miscUsage = miscUsage;
		}
	}
	
	private class BigSetMiscCollector implements MiscCollector {
		public long collectFor(int id) {
			BigSet bigSet = (BigSet) _db.getByID(id);
			_db.activate(bigSet, 1);
			BTree btree = fieldValue(bigSet, "_bTree");
			return bTreeUsage(btree);
		}
	}

	private Slot slot(int id) {
		return _db.idSystem().committedSlot(id);
	}
}

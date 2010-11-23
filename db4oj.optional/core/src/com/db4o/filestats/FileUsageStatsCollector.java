/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.filestats;

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.classindex.*;
import com.db4o.internal.collections.*;
import com.db4o.internal.fileheader.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.slots.*;

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
		EmbeddedObjectContainer db = Db4oEmbedded.openFile(dbPath);
		try {
			FileUsageStats stats = new FileUsageStatsCollector(db).collectStats();
			System.out.println(stats);
		}
		finally {
			db.close();
		}
	}
	
	private final LocalObjectContainer _db;
	
	public FileUsageStatsCollector(ObjectContainer db) {
		_db = (LocalObjectContainer) db;
	}

	public FileUsageStats collectStats() {
		final FileUsageStats stats = new FileUsageStats(_db.fileLength(), fileHeaderUsage(), idSystemUsage(), freespace(), classMetadataUsage(), freespaceUsage());
		Set<ClassNode> classRoots = ClassNode.buildHierarchy(_db.classCollection());
		for (ClassNode classRoot : classRoots) {
			collectClassStats(stats, classRoot);
		}
		return stats;
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
		Iterator4<Integer> nodeIter = btree.allNodeIds(_db.systemTransaction());
		long usage = slotSizeForId(btree.getID());
		while(nodeIter.moveNext()) {
			usage += slotSizeForId(nodeIter.current());
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

	private long freespace() {
		return _db.freespaceManager().totalFreespace();
	}

	private long freespaceUsage() {
		return freespaceUsage(_db.freespaceManager());
	}

	private long freespaceUsage(FreespaceManager fsm) {
		if(fsm instanceof InMemoryFreespaceManager) {
			return (Integer)Reflection4.invoke(fsm, "marshalledLength");
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
		if(!(idSystem instanceof BTreeIdSystem)) {
			return 0;
		}
		return bTreeUsage((BTree)fieldValue(idSystem, "_bTree"));
	}
	
	private long classMetadataUsage() {
		long usage = slotSizeForId(_db.classCollection().getID());
		Iterator4<Integer> classIdIter = _db.classCollection().ids();
		while(classIdIter.moveNext()) {
			usage += slotSizeForId(classIdIter.current());
		}
		return usage;
	}
	
	private long fileHeaderUsage() {
		int usage = _db.getFileHeader().length();
		usage += ((FileHeaderVariablePart)fieldValue(_db.getFileHeader(), "_variablePart")).marshalledLength();
		return usage;
	}
	
	private long slotSizeForId(int id) {
		return _db.idSystem().committedSlot(id).length();
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

}

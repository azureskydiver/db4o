/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.tools.defragment;

import java.io.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.foundation.io.*;
import com.db4o.inside.btree.*;
import com.db4o.inside.classindex.*;

/**
 * Slotwise defragment for yap files.
 */
public class SlotDefragment {

	/**
	 * Renames the file at the configured original path to the configured backup
	 * path and then builds a defragmented version of the file in the original place.
	 * 
	 * @param config The configuration for this defragmentation run.
	 */
	public static void defrag(DefragmentConfig config) throws IOException {
		defrag(config,new NullListener());
	}

	/**
	 * Renames the file at the configured original path to the configured backup
	 * path and then builds a defragmented version of the file in the original place.
	 * 
	 * @param config The configuration for this defragmentation run.
	 * @param listener A listener for status notifications during the defragmentation
	 *         process.
	 */
	public static void defrag(DefragmentConfig config, DefragmentListener listener) throws IOException {
		File4.rename(config.origPath(), config.backupPath());
		DefragContextImpl context=new DefragContextImpl(config,listener);
		int newClassCollectionID=0;
		int targetIdentityID=0;
		int targetUuidIndexID=0;
		try {
			firstPass(context,config);
			secondPass(context,config);
			defragUnindexed(context);
			newClassCollectionID=context.mappedID(context.sourceClassCollectionID());
			int sourceIdentityID=context.databaseIdentityID(DefragContextImpl.SOURCEDB);
			targetIdentityID=context.mappedID(sourceIdentityID);
			context.targetClassCollectionID(newClassCollectionID);
			targetUuidIndexID = context.mappedID(context.sourceUuidIndexID(),0);
		} 
		catch (CorruptionException exc) {
			exc.printStackTrace();
		} 
		finally {
			context.close();
		}
		setIdentity(config.origPath(), targetIdentityID,targetUuidIndexID);
	}

	private static void defragUnindexed(DefragContextImpl context) throws CorruptionException {
		Iterator4 unindexedIDs=context.unindexedIDs();
		while(unindexedIDs.moveNext()) {
			final int origID=((Integer)unindexedIDs.current()).intValue();
			if(context.hasSeen(origID)) {
				continue;
			}
			ReaderPair.processCopy(context, origID, new SlotCopyHandler() {
				public void processCopy(ReaderPair readers) throws CorruptionException {
					YapClass.defragObject(readers);
				}
				
			}, true, true);
		}
	}

	private static void setIdentity(String targetFile, int targetIdentityID, int targetUuidIndexID) {
		YapFile targetDB=(YapFile)Db4o.openFile(Db4o.newConfiguration(),targetFile);
		try {
			Db4oDatabase identity=(Db4oDatabase)targetDB.getByID(targetIdentityID);
			targetDB.setIdentity(identity);
			targetDB.systemData().uuidIndexId(targetUuidIndexID);
		}
		finally {
			targetDB.close();
		}
	}

	private static void firstPass(DefragContextImpl context,DefragmentConfig config) throws CorruptionException {
		//System.out.println("FIRST");
		pass(context,config,new FirstPassCommand());
	}

	private static void secondPass(final DefragContextImpl context,DefragmentConfig config) throws CorruptionException {
		//System.out.println("SECOND");
		pass(context,config,new SecondPassCommand());
	}		

	private static void pass(DefragContextImpl context,DefragmentConfig config,PassCommand command) throws CorruptionException {
		context.clearSeen();
		command.processClassCollection(context);
		StoredClass[] classes=context.storedClasses(DefragContextImpl.SOURCEDB);
		for (int classIdx = 0; classIdx < classes.length; classIdx++) {
			YapClass yapClass = (YapClass)classes[classIdx];
			if(!config.yapClassFilter().accept(yapClass)) {
				continue;
			}
			processYapClass(context, yapClass,command);
			command.flush(context);
		}
		BTree uuidIndex=context.sourceUuidIndex();
		if(uuidIndex!=null) {
			command.processBTree(context, uuidIndex);
		}
		command.flush(context);
		context.targetCommit();
	}

	// TODO order of class index/object slot processing is crucial:
	// - object slots before field indices (object slots register addresses for use by string indices)
	// - class index before object slots, otherwise phantom btree entries from deletions appear in the source class index?!?
	//   reproducable with SelectiveCascadingDeleteTestCase and ObjectSetTestCase - investigate.
	private static void processYapClass(final DefragContextImpl context, final YapClass curClass, final PassCommand command) throws CorruptionException {
		processClassIndex(context, curClass, command);
		processObjectsForYapClass(context, curClass, command);
		processYapClassAndFieldIndices(context, curClass, command);
	}

	private static void processObjectsForYapClass(
			final DefragContextImpl context, final YapClass curClass,
			final PassCommand command) {
		// TODO: check for string indices specifically, not field indices in general
		final boolean withStringIndex=withFieldIndex(curClass);
		context.traverseAll(curClass, new Visitor4() {
			public void visit(Object obj) {
				int id = ((Integer)obj).intValue();
				// TODO cache mapped pair and pass target id into processObjectSlot()
				if(command.hasSeen(context,id)) {
					return;
				}
				try {
					command.processObjectSlot(context,curClass,id, withStringIndex);
				} catch (CorruptionException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private static boolean withFieldIndex(YapClass clazz) {
		Iterator4 fieldIter=clazz.fields();
		while(fieldIter.moveNext()) {
			YapField curField=(YapField)fieldIter.current();
			if(curField.hasIndex()) {
				return true;
			}
		}
		return false;
	}

	private static void processYapClassAndFieldIndices(final DefragContextImpl context,
			final YapClass curClass, final PassCommand command)
			throws CorruptionException {
		int sourceClassIndexID=0;
		int targetClassIndexID=0;
		if(curClass.hasIndex()) {
			sourceClassIndexID = curClass.index().id();
			targetClassIndexID=context.mappedID(sourceClassIndexID,-1);
		}
		command.processClass(context,curClass,curClass.getID(),targetClassIndexID);
	}

	private static void processClassIndex(final DefragContextImpl context,
			final YapClass curClass, final PassCommand command)
			throws CorruptionException {
		if(curClass.hasIndex()) {
			BTreeClassIndexStrategy indexStrategy=(BTreeClassIndexStrategy) curClass.index();
			final BTree btree=indexStrategy.btree();
			command.processBTree(context,btree);
		}
	}
	
	private static class NullListener implements DefragmentListener {
		public void notifyDefragmentInfo(DefragmentInfo info) {
		}
	}
}

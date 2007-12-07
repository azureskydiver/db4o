/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.defragment;

import java.io.IOException;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.marshall.*;

/**
 * Second step in the defragmenting process: Fills in target file pointer slots, copies
 * content slots from source to target and triggers ID remapping therein by calling the
 * appropriate yap/marshaller defrag() implementations. During the process, the actual address
 * mappings for the content slots are registered for use with string indices.
 * 
 * @exclude
 */
final class SecondPassCommand implements PassCommand {

	protected final int _objectCommitFrequency;
	protected int _objectCount=0;
	
	
	
	public SecondPassCommand(int objectCommitFrequency) {
		_objectCommitFrequency = objectCommitFrequency;
	}

	public void processClass(final DefragmentServicesImpl context, final ClassMetadata yapClass, int id,final int classIndexID) throws CorruptionException, IOException {
		if(context.mappedID(id,-1)==-1) {
			System.err.println("MAPPING NOT FOUND: "+id);
		}
		BufferPair.processCopy(context, id, new SlotCopyHandler() {
			public void processCopy(BufferPair readers) throws CorruptionException, IOException {
				yapClass.defragClass(readers, classIndexID);
			}
		});
	}

	public void processObjectSlot(final DefragmentServicesImpl context, final ClassMetadata yapClass, int id) throws CorruptionException, IOException {
		Buffer sourceBuffer = context.sourceBufferByID(id);
		ObjectHeader objHead = context.sourceObjectHeader(sourceBuffer);
		sourceBuffer._offset = 0;
		boolean registerAddresses = context.hasFieldIndex(objHead.classMetadata());
		BufferPair.processCopy(context, id, new SlotCopyHandler() {
			public void processCopy(BufferPair buffers) {
				ClassMetadata.defragObject(buffers);
				if(_objectCommitFrequency>0) {
					_objectCount++;
					if(_objectCount==_objectCommitFrequency) {
						context.targetCommit();
						_objectCount=0;
					}
				}
			}
		},registerAddresses, sourceBuffer);
	}

	public void processClassCollection(final DefragmentServicesImpl context) throws CorruptionException, IOException {
		BufferPair.processCopy(context, context.sourceClassCollectionID(), new SlotCopyHandler() {
				public void processCopy(BufferPair readers) {
					if (Deploy.debug) {
					    readers.readBegin(Const4.YAPCLASSCOLLECTION);
					}
					
					int acceptedClasses = 0;
					int numClassesOffset = readers.target().offset();
					acceptedClasses = copyAcceptedClasses(readers, acceptedClasses);
					writeIntAt(readers.target(), numClassesOffset, acceptedClasses);
					
					if (Deploy.debug) {
					    readers.readEnd();
					}
				}

				private int copyAcceptedClasses(BufferPair readers,
						int acceptedClasses) {
					int numClasses=readers.readInt();
					for(int classIdx=0;classIdx<numClasses;classIdx++) {
						int classId = readers.source().readInt();
						if (! accept(classId)) {
							continue;
						}
						++acceptedClasses;
						readers.writeMappedID(classId);
					}
					return acceptedClasses;
				}

				private void writeIntAt(Buffer target, int offset,
						int value) {
					int currentOffset = target.offset();
					target.seek(offset);
					target.writeInt(value);
					target.seek(currentOffset);
					
				}

				private boolean accept(int classId) {
					return context.accept(context.yapClass(classId));
				}
			});
	}

	public void processBTree(final DefragmentServicesImpl context, BTree btree) throws CorruptionException, IOException {
		btree.defragBTree(context);
	}

	public void flush(DefragmentServicesImpl context) {
	}
}
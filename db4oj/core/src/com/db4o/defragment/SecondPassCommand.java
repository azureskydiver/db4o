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

	public void processClass(final DefragmentServicesImpl services, final ClassMetadata yapClass, int id,final int classIndexID) throws CorruptionException, IOException {
		if(services.mappedID(id,-1)==-1) {
			System.err.println("MAPPING NOT FOUND: "+id);
		}
		DefragmentContextImpl.processCopy(services, id, new SlotCopyHandler() {
			public void processCopy(DefragmentContextImpl context) throws CorruptionException, IOException {
				yapClass.defragClass(context, classIndexID);
			}
		});
	}

	public void processObjectSlot(final DefragmentServicesImpl services, final ClassMetadata yapClass, int id) throws CorruptionException, IOException {
		BufferImpl sourceBuffer = services.sourceBufferByID(id);
		ObjectHeader objHead = services.sourceObjectHeader(sourceBuffer);
		sourceBuffer._offset = 0;
		boolean registerAddresses = services.hasFieldIndex(objHead.classMetadata());
		DefragmentContextImpl.processCopy(services, id, new SlotCopyHandler() {
			public void processCopy(DefragmentContextImpl context) {
				ClassMetadata.defragObject(context);
				if(_objectCommitFrequency>0) {
					_objectCount++;
					if(_objectCount==_objectCommitFrequency) {
						services.targetCommit();
						_objectCount=0;
					}
				}
			}
		},registerAddresses, sourceBuffer);
	}

	public void processClassCollection(final DefragmentServicesImpl services) throws CorruptionException, IOException {
		DefragmentContextImpl.processCopy(services, services.sourceClassCollectionID(), new SlotCopyHandler() {
				public void processCopy(DefragmentContextImpl context) {
					if (Deploy.debug) {
					    context.readBegin(Const4.YAPCLASSCOLLECTION);
					}
					
					int acceptedClasses = 0;
					int numClassesOffset = context.target().offset();
					acceptedClasses = copyAcceptedClasses(context, acceptedClasses);
					writeIntAt(context.target(), numClassesOffset, acceptedClasses);
					
					if (Deploy.debug) {
					    context.readEnd();
					}
				}

				private int copyAcceptedClasses(DefragmentContextImpl context,
						int acceptedClasses) {
					int numClasses=context.readInt();
					for(int classIdx=0;classIdx<numClasses;classIdx++) {
						int classId = context.source().readInt();
						if (! accept(classId)) {
							continue;
						}
						++acceptedClasses;
						context.writeMappedID(classId);
					}
					return acceptedClasses;
				}

				private void writeIntAt(BufferImpl target, int offset,
						int value) {
					int currentOffset = target.offset();
					target.seek(offset);
					target.writeInt(value);
					target.seek(currentOffset);
					
				}

				private boolean accept(int classId) {
					return services.accept(services.classMetadataForId(classId));
				}
			});
	}

	public void processBTree(final DefragmentServicesImpl context, BTree btree) throws CorruptionException, IOException {
		btree.defragBTree(context);
	}

	public void flush(DefragmentServicesImpl context) {
	}
}
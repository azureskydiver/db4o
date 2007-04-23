/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs.messages;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;

/**
 * @exclude
 */
public class MCommittedInfo extends MsgD implements ClientSideMessage {

	public MCommittedInfo encode(CallbackObjectInfoCollections callbackInfo) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			
			encodeObjectInfoCollection(os, callbackInfo.added);
			encodeObjectInfoCollection(os, callbackInfo.deleted);
			encodeObjectInfoCollection(os, callbackInfo.updated);
			
			byte[] bytes = os.toByteArray();
			MCommittedInfo committedInfo = (MCommittedInfo) getWriterForLength(transaction(),
				bytes.length);
			committedInfo._payLoad.append(bytes);
			return committedInfo;
		} catch (IOException e) {
			Exceptions4.shouldNeverHappen();
			return null;
		}
	}

	private void encodeObjectInfoCollection(ByteArrayOutputStream os, ObjectInfoCollection collection)
		throws IOException {
		Iterator4 iter = collection.iterator();
		while (iter.moveNext()) {
			LazyObjectReference obj = (LazyObjectReference) iter.current();
			writeLong(os, obj.getInternalID());
		}
		writeLong(os, -1);
	}

	public CallbackObjectInfoCollections decode() {
		try {
			CallbackObjectInfoCollections callbackInfo = CallbackObjectInfoCollections.EMTPY;
			ByteArrayInputStream is = new ByteArrayInputStream(_payLoad._buffer);
			callbackInfo.added = decodeObjectInfoCollection(is);
			callbackInfo.deleted = decodeObjectInfoCollection(is);
			callbackInfo.updated = decodeObjectInfoCollection(is);
			return callbackInfo;
		} catch (IOException e) {
			Exceptions4.shouldNeverHappen();
			return null;
		}
	}

	private ObjectInfoCollection decodeObjectInfoCollection(ByteArrayInputStream is)
		throws IOException {
		final Collection4 collection = new Collection4();
		while (true) {
			long id = readLong(is);
			if (id == -1) {
				break;
			}
			collection.add(new LazyObjectReference(transaction(), (int) id));
		}
		return new ObjectInfoCollectionImpl(collection);
	}

	private void writeLong(ByteArrayOutputStream os, long l) throws IOException {
		for (int i = 0; i < 64; i += 8) {
			os.write((int) (l >> i));
		}
	}

	private long readLong(ByteArrayInputStream is) throws IOException {
		long l = 0;
		for (int i = 0; i < 64; i += 8) {
			l += ((long) (is.read())) << i;
		}
		return l;
	}

	public boolean processAtClient() {
		final CallbackObjectInfoCollections callbackInfos = decode();
		new Thread(new Runnable() {
			public void run() {
				stream().callbacks().commitOnCompleted(transaction(), callbackInfos);
			}
		}).start();
		return true;
	}

}
